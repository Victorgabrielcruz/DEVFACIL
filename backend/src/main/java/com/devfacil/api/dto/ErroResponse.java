package com.devfacil.api.dto;

import java.time.Instant;

public record ErroResponse(String erro, Instant timestamp) {
    public static ErroResponse of(String erro) {
        return new ErroResponse(erro, Instant.now());
    }
}
