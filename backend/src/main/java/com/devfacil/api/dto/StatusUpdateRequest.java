package com.devfacil.api.dto;

import com.devfacil.api.model.StatusSolicitacao;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

public record StatusUpdateRequest(
        @NotNull StatusSolicitacao status,
        @JsonProperty("desenvolvedor_id") Long desenvolvedorId
) {
}
