package com.devfacil.api.event;

import com.devfacil.api.config.RabbitMqConfig;
import com.devfacil.api.service.MessageAuditService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Consumidor de eventos do DevFacil no RabbitMQ.
 *
 * Esta classe escuta as filas configuradas no RabbitMQ e processa as mensagens
 * recebidas de forma assincrona. Na Sprint 2, o processamento e demonstrativo:
 * os eventos recebidos sao registrados no log da aplicacao.
 */
@Component
public class RabbitMqEventConsumer {

    private final MessageAuditService messageAuditService;

    public RabbitMqEventConsumer(MessageAuditService messageAuditService) {
        this.messageAuditService = messageAuditService;
    }

    /**
     * Consome eventos de novas solicitacoes criadas.
     *
     * Essa fila recebe mensagens quando um cliente cria uma nova solicitacao.
     * Futuramente, este consumidor pode ser substituido ou complementado pelo
     * aplicativo do desenvolvedor, que sera notificado sobre novas demandas.
     *
     * @param payload dados da solicitacao criada
     */
    @RabbitListener(queues = RabbitMqConfig.DEVELOPMENT_REQUESTS_QUEUE)
    public void consumeDevelopmentRequestCreated(DevelopmentRequestEventPayload payload) {
        messageAuditService.registrarConsumo(
                RabbitMqConfig.DEVELOPMENT_REQUEST_CREATED_ROUTING_KEY,
                RabbitMqConfig.EXCHANGE_NAME,
                RabbitMqConfig.DEVELOPMENT_REQUEST_CREATED_ROUTING_KEY,
                RabbitMqConfig.DEVELOPMENT_REQUESTS_QUEUE,
                payload
        );
        System.out.println(
                "[RabbitMQ] Nova solicitacao recebida para desenvolvedor: "
                        + "solicitacaoId=" + payload.getSolicitacaoId()
                        + ", clienteId=" + payload.getClienteId()
                        + ", tipoServico=" + payload.getTipoServico()
                        + ", status=" + payload.getStatus()
        );
    }

    /**
     * Consome eventos de alteracao de status das solicitacoes.
     *
     * Essa fila recebe mensagens quando uma solicitacao muda de status.
     * Futuramente, este consumidor pode ser usado para notificar o cliente no
     * aplicativo Flutter.
     *
     * @param payload dados da solicitacao com status atualizado
     */
    @RabbitListener(queues = RabbitMqConfig.STATUS_UPDATES_QUEUE)
    public void consumeDevelopmentRequestStatusChanged(DevelopmentRequestEventPayload payload) {
        String routingKey = "cancelada".equals(payload.getStatus())
                ? RabbitMqConfig.DEVELOPMENT_REQUEST_CANCELLED_ROUTING_KEY
                : RabbitMqConfig.DEVELOPMENT_REQUEST_STATUS_CHANGED_ROUTING_KEY;
        messageAuditService.registrarConsumo(
                routingKey,
                RabbitMqConfig.EXCHANGE_NAME,
                routingKey,
                RabbitMqConfig.STATUS_UPDATES_QUEUE,
                payload
        );
        System.out.println(
                "[RabbitMQ] Atualizacao de status recebida para cliente: "
                        + "solicitacaoId=" + payload.getSolicitacaoId()
                        + ", clienteId=" + payload.getClienteId()
                        + ", desenvolvedorId=" + payload.getDesenvolvedorId()
                        + ", status=" + payload.getStatus()
        );
    }
}
