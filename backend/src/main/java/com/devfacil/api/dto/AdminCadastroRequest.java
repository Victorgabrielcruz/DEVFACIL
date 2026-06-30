package com.devfacil.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AdminCadastroRequest(
        @NotBlank String nome,
        @Email @NotBlank String email,
        @Size(min = 6) @NotBlank String senha,
        @JsonProperty("admin_key") @NotBlank String adminKey
) {
}
