package com.devfacil.api.event;

import java.time.Instant;

/**
 * Payload enviado nos eventos de solicitacao de desenvolvimento.
 *
 * Esta classe representa os dados publicados no RabbitMQ quando uma solicitacao
 * e criada ou quando seu status e alterado. O payload permite que consumidores
 * entendam qual solicitacao mudou, quem e o cliente, quem e o desenvolvedor
 * associado e qual e o status atual.
 */
public class DevelopmentRequestEventPayload {

    /**
     * Identificador da solicitacao de desenvolvimento.
     */
    private Long solicitacaoId;

    /**
     * Identificador do cliente que criou a solicitacao.
     */
    private Long clienteId;

    /**
     * Identificador do desenvolvedor associado a solicitacao.
     *
     * Pode ser null quando a solicitacao ainda esta aberta e nenhum
     * desenvolvedor aceitou a demanda.
     */
    private Long desenvolvedorId;

    /**
     * Tipo de servico solicitado pelo cliente.
     *
     * Exemplos: site, aplicativo mobile, API, sistema web, manutencao ou
     * consultoria tecnica.
     */
    private String tipoServico;

    /**
     * Status atual da solicitacao.
     *
     * Exemplos: aberta, aceita, recusada, em_andamento ou concluida.
     */
    private String status;

    /**
     * Data e hora em que o evento foi gerado.
     */
    private Instant occurredAt;

    /**
     * Construtor vazio usado pelo Jackson para serializar e desserializar o
     * objeto como JSON.
     */
    public DevelopmentRequestEventPayload() {
    }

    /**
     * Cria um payload completo para eventos de solicitacao.
     *
     * @param solicitacaoId identificador da solicitacao
     * @param clienteId identificador do cliente
     * @param desenvolvedorId identificador do desenvolvedor, quando existir
     * @param tipoServico tipo de servico solicitado
     * @param status status atual da solicitacao
     * @param occurredAt data e hora em que o evento ocorreu
     */
    public DevelopmentRequestEventPayload(
            Long solicitacaoId,
            Long clienteId,
            Long desenvolvedorId,
            String tipoServico,
            String status,
            Instant occurredAt
    ) {
        this.solicitacaoId = solicitacaoId;
        this.clienteId = clienteId;
        this.desenvolvedorId = desenvolvedorId;
        this.tipoServico = tipoServico;
        this.status = status;
        this.occurredAt = occurredAt;
    }

    public Long getSolicitacaoId() {
        return solicitacaoId;
    }

    public void setSolicitacaoId(Long solicitacaoId) {
        this.solicitacaoId = solicitacaoId;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public Long getDesenvolvedorId() {
        return desenvolvedorId;
    }

    public void setDesenvolvedorId(Long desenvolvedorId) {
        this.desenvolvedorId = desenvolvedorId;
    }

    public String getTipoServico() {
        return tipoServico;
    }

    public void setTipoServico(String tipoServico) {
        this.tipoServico = tipoServico;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }

    public void setOccurredAt(Instant occurredAt) {
        this.occurredAt = occurredAt;
    }
}
