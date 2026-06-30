package com.devfacil.api.controller;

import com.devfacil.api.dto.AuthResponse;
import com.devfacil.api.dto.AdminCadastroRequest;
import com.devfacil.api.dto.ClienteCadastroRequest;
import com.devfacil.api.dto.DesenvolvedorCadastroRequest;
import com.devfacil.api.dto.LoginRequest;
import com.devfacil.api.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/auth/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/auth/cadastro/cliente")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse cadastrarCliente(@Valid @RequestBody ClienteCadastroRequest request) {
        return authService.cadastrarCliente(request);
    }

    @PostMapping("/auth/cadastro/desenvolvedor")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse cadastrarDesenvolvedor(@Valid @RequestBody DesenvolvedorCadastroRequest request) {
        return authService.cadastrarDesenvolvedor(request);
    }

    @PostMapping("/auth/cadastro/admin")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse cadastrarAdmin(@Valid @RequestBody AdminCadastroRequest request) {
        return authService.cadastrarAdmin(request);
    }

    @GetMapping("/auth/me")
    public AuthResponse me(@RequestHeader("Authorization") String authorization) {
        return authService.me(authorization.replace("Bearer ", ""));
    }
}
