package com.devfacil.api.service;

import com.devfacil.api.dto.AuthResponse;
import com.devfacil.api.dto.AdminCadastroRequest;
import com.devfacil.api.dto.ClienteCadastroRequest;
import com.devfacil.api.dto.DesenvolvedorCadastroRequest;
import com.devfacil.api.dto.LoginRequest;
import com.devfacil.api.exception.ConflitoException;
import com.devfacil.api.exception.RecursoNaoEncontradoException;
import com.devfacil.api.model.Cliente;
import com.devfacil.api.model.Desenvolvedor;
import com.devfacil.api.model.PerfilUsuario;
import com.devfacil.api.model.Usuario;
import com.devfacil.api.repository.ClienteRepository;
import com.devfacil.api.repository.DesenvolvedorRepository;
import com.devfacil.api.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final ClienteRepository clienteRepository;
    private final DesenvolvedorRepository desenvolvedorRepository;
    private final PasswordService passwordService;
    private final TokenService tokenService;
    private final String adminKey;

    public AuthService(
            UsuarioRepository usuarioRepository,
            ClienteRepository clienteRepository,
            DesenvolvedorRepository desenvolvedorRepository,
            PasswordService passwordService,
            TokenService tokenService,
            @Value("${devfacil.auth.admin-key:devfacil-admin}") String adminKey
    ) {
        this.usuarioRepository = usuarioRepository;
        this.clienteRepository = clienteRepository;
        this.desenvolvedorRepository = desenvolvedorRepository;
        this.passwordService = passwordService;
        this.tokenService = tokenService;
        this.adminKey = adminKey;
    }

    @Transactional
    public AuthResponse cadastrarCliente(ClienteCadastroRequest request) {
        validarEmailLivre(request.email());

        Cliente cliente = new Cliente();
        cliente.setNome(request.nome());
        cliente.setTelefone(request.telefone());
        cliente.setEmail(request.email());
        cliente.setEmpresa(normalizarOpcional(request.empresa()));
        Cliente clienteSalvo = clienteRepository.save(cliente);

        Usuario usuario = criarUsuario(request.nome(), request.email(), request.senha(), PerfilUsuario.CLIENTE);
        usuario.setCliente(clienteSalvo);
        Usuario usuarioSalvo = usuarioRepository.save(usuario);
        return AuthResponse.from(tokenService.gerar(usuarioSalvo), usuarioSalvo);
    }

    @Transactional
    public AuthResponse cadastrarDesenvolvedor(DesenvolvedorCadastroRequest request) {
        validarEmailLivre(request.email());

        Desenvolvedor desenvolvedor = new Desenvolvedor();
        desenvolvedor.setNome(request.nome());
        desenvolvedor.setTelefone(request.telefone());
        desenvolvedor.setEmail(request.email());
        desenvolvedor.setStack(request.stack());
        desenvolvedor.setSenioridade(request.senioridade());
        desenvolvedor.setPortfolioUrl(request.portfolioUrl());
        desenvolvedor.setDisponivel(request.disponivel() == null || request.disponivel());
        Desenvolvedor desenvolvedorSalvo = desenvolvedorRepository.save(desenvolvedor);

        Usuario usuario = criarUsuario(request.nome(), request.email(), request.senha(), PerfilUsuario.DESENVOLVEDOR);
        usuario.setDesenvolvedor(desenvolvedorSalvo);
        Usuario usuarioSalvo = usuarioRepository.save(usuario);
        return AuthResponse.from(tokenService.gerar(usuarioSalvo), usuarioSalvo);
    }

    @Transactional
    public AuthResponse cadastrarAdmin(AdminCadastroRequest request) {
        if (!adminKey.equals(request.adminKey())) {
            throw new IllegalArgumentException("chave de administrador invalida");
        }
        validarEmailLivre(request.email());

        Usuario usuario = criarUsuario(request.nome(), request.email(), request.senha(), PerfilUsuario.ADMIN);
        Usuario usuarioSalvo = usuarioRepository.save(usuario);
        return AuthResponse.from(tokenService.gerar(usuarioSalvo), usuarioSalvo);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(request.email())
                .orElseThrow(() -> new RecursoNaoEncontradoException("email ou senha invalidos"));
        if (!passwordService.matches(request.senha(), usuario.getSenhaHash())) {
            throw new RecursoNaoEncontradoException("email ou senha invalidos");
        }
        return AuthResponse.from(tokenService.gerar(usuario), usuario);
    }

    @Transactional(readOnly = true)
    public AuthResponse me(String token) {
        Usuario usuario = tokenService.validar(token);
        return AuthResponse.from(tokenService.gerar(usuario), usuario);
    }

    private Usuario criarUsuario(String nome, String email, String senha, PerfilUsuario perfil) {
        Usuario usuario = new Usuario();
        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setSenhaHash(passwordService.hash(senha));
        usuario.setPerfil(perfil);
        return usuario;
    }

    private void validarEmailLivre(String email) {
        if (usuarioRepository.existsByEmail(email) || clienteRepository.existsByEmail(email) || desenvolvedorRepository.existsByEmail(email)) {
            throw new ConflitoException("email ja cadastrado");
        }
    }

    private String normalizarOpcional(String value) {
        return value == null ? "" : value.trim();
    }
}
