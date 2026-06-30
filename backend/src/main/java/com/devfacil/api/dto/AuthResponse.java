package com.devfacil.api.dto;

import com.devfacil.api.model.PerfilUsuario;
import com.devfacil.api.model.Usuario;

public record AuthResponse(
        String token,
        Long usuarioId,
        String nome,
        String email,
        PerfilUsuario perfil,
        Long clienteId,
        Long desenvolvedorId
) {
    public static AuthResponse from(String token, Usuario usuario) {
        return new AuthResponse(
                token,
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getPerfil(),
                usuario.getCliente() == null ? null : usuario.getCliente().getId(),
                usuario.getDesenvolvedor() == null ? null : usuario.getDesenvolvedor().getId()
        );
    }
}
