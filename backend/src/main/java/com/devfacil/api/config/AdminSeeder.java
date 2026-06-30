package com.devfacil.api.config;

import com.devfacil.api.model.PerfilUsuario;
import com.devfacil.api.model.Usuario;
import com.devfacil.api.repository.UsuarioRepository;
import com.devfacil.api.service.PasswordService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class AdminSeeder implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordService passwordService;
    private final String adminName;
    private final String adminEmail;
    private final String adminPassword;

    public AdminSeeder(
            UsuarioRepository usuarioRepository,
            PasswordService passwordService,
            @Value("${devfacil.auth.admin-name:Admin DevFacil}") String adminName,
            @Value("${devfacil.auth.admin-email:}") String adminEmail,
            @Value("${devfacil.auth.admin-password:}") String adminPassword
    ) {
        this.usuarioRepository = usuarioRepository;
        this.passwordService = passwordService;
        this.adminName = adminName;
        this.adminEmail = adminEmail;
        this.adminPassword = adminPassword;
    }

    @Override
    public void run(String... args) {
        if (adminEmail.isBlank() || adminPassword.isBlank() || usuarioRepository.existsByEmail(adminEmail)) {
            return;
        }

        Usuario usuario = new Usuario();
        usuario.setNome(adminName);
        usuario.setEmail(adminEmail);
        usuario.setSenhaHash(passwordService.hash(adminPassword));
        usuario.setPerfil(PerfilUsuario.ADMIN);
        usuarioRepository.save(usuario);
    }
}
