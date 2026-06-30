package com.devfacil.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ClienteRequest(
        @NotBlank String nome,
        @NotBlank String telefone,
        @Email @NotBlank String email,
        String empresa
) {
}
