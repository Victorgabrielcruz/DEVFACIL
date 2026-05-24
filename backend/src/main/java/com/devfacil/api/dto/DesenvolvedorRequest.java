package com.devfacil.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import com.fasterxml.jackson.annotation.JsonProperty;

public record DesenvolvedorRequest(
        @NotBlank String nome,
        @NotBlank String telefone,
        @Email @NotBlank String email,
        @NotBlank String stack,
        @NotBlank String senioridade,
        @JsonProperty("portfolio_url") String portfolioUrl,
        Boolean disponivel
) {
}
