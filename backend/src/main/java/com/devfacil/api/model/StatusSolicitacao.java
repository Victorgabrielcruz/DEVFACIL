package com.devfacil.api.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum StatusSolicitacao {
    ABERTA("aberta"),
    ACEITA("aceita"),
    RECUSADA("recusada"),
    EM_ANDAMENTO("em_andamento"),
    CONCLUIDA("concluida"),
    CANCELADA("cancelada");

    private final String value;

    StatusSolicitacao(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static StatusSolicitacao fromValue(String value) {
        return Arrays.stream(values())
                .filter(status -> status.value.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("status invalido: " + value));
    }
}
