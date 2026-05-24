package com.devfacil.api.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum Prioridade {
    BAIXA("baixa"),
    NORMAL("normal"),
    ALTA("alta");

    private final String value;

    Prioridade(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static Prioridade fromValue(String value) {
        return Arrays.stream(values())
                .filter(prioridade -> prioridade.value.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("prioridade invalida: " + value));
    }
}
