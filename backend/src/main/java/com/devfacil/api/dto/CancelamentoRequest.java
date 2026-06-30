package com.devfacil.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CancelamentoRequest(
        @JsonProperty("motivo_cancelamento") String motivoCancelamento
) {
}
