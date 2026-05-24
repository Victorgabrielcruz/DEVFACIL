package com.devfacil.api.dto;

import com.devfacil.api.model.Prioridade;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SolicitacaoRequest(
        @JsonProperty("cliente_id") @NotNull Long clienteId,
        @JsonProperty("desenvolvedor_id") Long desenvolvedorId,
        @JsonProperty("tipo_servico") @NotBlank String tipoServico,
        @NotBlank String descricao,
        @NotBlank String requisitos,
        @JsonProperty("prazo_desejado") String prazoDesejado,
        @JsonProperty("orcamento_estimado") Double orcamentoEstimado,
        Prioridade prioridade
) {
}
