package com.devfacil.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ClienteCadastroRequest(
        @NotBlank String nome,
        @NotBlank String telefone,
        @Email @NotBlank String email,
        String empresa,
        @Size(min = 6) @NotBlank String senha
) {
}
