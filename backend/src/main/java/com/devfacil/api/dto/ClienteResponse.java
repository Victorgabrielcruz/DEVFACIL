package com.devfacil.api.dto;

import com.devfacil.api.model.Cliente;

import java.time.Instant;

public record ClienteResponse(
        Long id,
        String nome,
        String telefone,
        String email,
        String empresa,
        Instant createdAt
) {
    public static ClienteResponse from(Cliente cliente) {
        return new ClienteResponse(
                cliente.getId(),
                cliente.getNome(),
                cliente.getTelefone(),
                cliente.getEmail(),
                cliente.getEmpresa(),
                cliente.getCreatedAt()
        );
    }
}
