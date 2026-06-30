package com.devfacil.api.service;

import com.devfacil.api.exception.AcessoNegadoException;
import com.devfacil.api.model.PerfilUsuario;
import com.devfacil.api.model.Usuario;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {

    public Usuario get(HttpServletRequest request) {
        Object usuario = request.getAttribute("usuario");
        if (usuario instanceof Usuario usuarioAutenticado) {
            return usuarioAutenticado;
        }
        throw new AcessoNegadoException("usuario autenticado nao encontrado");
    }

    public void exigirAdmin(HttpServletRequest request) {
        if (get(request).getPerfil() != PerfilUsuario.ADMIN) {
            throw new AcessoNegadoException("acesso restrito ao administrador");
        }
    }
}
