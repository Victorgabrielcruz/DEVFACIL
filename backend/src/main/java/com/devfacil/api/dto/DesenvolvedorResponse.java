package com.devfacil.api.dto;

import com.devfacil.api.model.Desenvolvedor;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public record DesenvolvedorResponse(
        Long id,
        String nome,
        String telefone,
        String email,
        String stack,
        String senioridade,
        @JsonProperty("portfolio_url") String portfolioUrl,
        boolean disponivel,
        Instant createdAt
) {
    public static DesenvolvedorResponse from(Desenvolvedor desenvolvedor) {
        return new DesenvolvedorResponse(
                desenvolvedor.getId(),
                desenvolvedor.getNome(),
                desenvolvedor.getTelefone(),
                desenvolvedor.getEmail(),
                desenvolvedor.getStack(),
                desenvolvedor.getSenioridade(),
                desenvolvedor.getPortfolioUrl(),
                desenvolvedor.isDisponivel(),
                desenvolvedor.getCreatedAt()
        );
    }
}
