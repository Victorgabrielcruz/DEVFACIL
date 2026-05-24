package com.devfacil.api.dto;

import com.devfacil.api.model.Solicitacao;
import com.devfacil.api.model.StatusSolicitacao;
import com.devfacil.api.model.Prioridade;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public record SolicitacaoResponse(
        Long id,
        @JsonProperty("cliente_id") Long clienteId,
        @JsonProperty("desenvolvedor_id") Long desenvolvedorId,
        @JsonProperty("tipo_servico") String tipoServico,
        String descricao,
        String requisitos,
        @JsonProperty("prazo_desejado") String prazoDesejado,
        @JsonProperty("orcamento_estimado") Double orcamentoEstimado,
        Prioridade prioridade,
        StatusSolicitacao status,
        Instant createdAt,
        Instant updatedAt
) {
    public static SolicitacaoResponse from(Solicitacao solicitacao) {
        Long desenvolvedorId = solicitacao.getDesenvolvedor() == null ? null : solicitacao.getDesenvolvedor().getId();
        return new SolicitacaoResponse(
                solicitacao.getId(),
                solicitacao.getCliente().getId(),
                desenvolvedorId,
                solicitacao.getTipoServico(),
                solicitacao.getDescricao(),
                solicitacao.getRequisitos(),
                solicitacao.getPrazoDesejado(),
                solicitacao.getOrcamentoEstimado(),
                solicitacao.getPrioridade(),
                solicitacao.getStatus(),
                solicitacao.getCreatedAt(),
                solicitacao.getUpdatedAt()
        );
    }
}
