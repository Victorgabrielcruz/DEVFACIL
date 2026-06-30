package com.devfacil.api.controller;

import com.devfacil.api.dto.ClienteRequest;
import com.devfacil.api.dto.ClienteResponse;
import com.devfacil.api.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

    @GetMapping("/clientes")
    public List<ClienteResponse> listar() {
        return clienteService.listar().stream()
                .map(ClienteResponse::from)
                .toList();
    }

    @GetMapping("/clientes/{id}")
    public ClienteResponse buscar(@PathVariable Long id) {
        return ClienteResponse.from(clienteService.buscar(id));
    }

    @PutMapping("/clientes/{id}")
    public ClienteResponse atualizar(@PathVariable Long id, @Valid @RequestBody ClienteRequest request) {
        return ClienteResponse.from(clienteService.atualizar(id, request));
    }

    @DeleteMapping("/clientes/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Long id) {
        clienteService.remover(id);
    }
}
