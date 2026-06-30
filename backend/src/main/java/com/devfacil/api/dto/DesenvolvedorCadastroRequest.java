package com.devfacil.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DesenvolvedorCadastroRequest(
        @NotBlank String nome,
        @NotBlank String telefone,
        @Email @NotBlank String email,
        @NotBlank String stack,
        @NotBlank String senioridade,
        @JsonProperty("portfolio_url") String portfolioUrl,
        Boolean disponivel,
        @Size(min = 6) @NotBlank String senha
) {
}
