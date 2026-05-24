package com.devfacil.api.controller;

import com.devfacil.api.dto.ClienteRequest;
import com.devfacil.api.dto.ClienteResponse;
import com.devfacil.api.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @PostMapping("/clientes")
    @ResponseStatus(HttpStatus.CREATED)
    public ClienteResponse criar(@Valid @RequestBody ClienteRequest request) {
        return ClienteResponse.from(clienteService.criar(request));
    }
}
