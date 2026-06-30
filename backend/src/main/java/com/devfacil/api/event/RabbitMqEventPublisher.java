package com.devfacil.api.event;

import com.devfacil.api.config.RabbitMqConfig;
import com.devfacil.api.service.MessageAuditService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * Publicador de eventos do DevFacil no RabbitMQ.
 *
 * Esta classe centraliza o envio de eventos do dominio para a exchange
 * configurada no RabbitMQ. Ela nao contem regra de negocio; apenas recebe
 * um payload ja montado e publica esse payload usando a routing key correta.
 */
@Component
public class RabbitMqEventPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final MessageAuditService messageAuditService;

    /**
     * Cria o publicador usando o RabbitTemplate do Spring.
     *
     * O RabbitTemplate e o componente responsavel por enviar mensagens para
     * o RabbitMQ.
     *
     * @param rabbitTemplate componente de envio de mensagens do Spring AMQP
     * @param messageAuditService servico responsavel por auditar a mensageria
     */
    public RabbitMqEventPublisher(
            RabbitTemplate rabbitTemplate,
            MessageAuditService messageAuditService
    ) {
        this.rabbitTemplate = rabbitTemplate;
        this.messageAuditService = messageAuditService;
    }

    /**
     * Publica o evento de nova solicitacao criada.
     *
     * Esse evento representa que um cliente criou uma nova demanda de
     * desenvolvimento. A mensagem e enviada para a exchange devfacil.events
     * usando a routing key development_request.created. Pelo binding definido
     * na configuracao, o RabbitMQ entrega a mensagem na fila
     * devfacil.development_requests.
     *
     * @param payload dados da solicitacao criada
     */
    public void publishDevelopmentRequestCreated(DevelopmentRequestEventPayload payload) {
        rabbitTemplate.convertAndSend(
                RabbitMqConfig.EXCHANGE_NAME,
                RabbitMqConfig.DEVELOPMENT_REQUEST_CREATED_ROUTING_KEY,
                payload
        );
        messageAuditService.registrarPublicacao(
                RabbitMqConfig.DEVELOPMENT_REQUEST_CREATED_ROUTING_KEY,
                RabbitMqConfig.EXCHANGE_NAME,
                RabbitMqConfig.DEVELOPMENT_REQUEST_CREATED_ROUTING_KEY,
                payload
        );
    }

    /**
     * Publica o evento de alteracao de status da solicitacao.
     *
     * Esse evento representa que uma solicitacao foi aceita, recusada,
     * concluida ou teve outro status alterado. A mensagem e enviada para a
     * exchange devfacil.events usando a routing key
     * development_request.status_changed. Pelo binding definido na
     * configuracao, o RabbitMQ entrega a mensagem na fila
     * devfacil.status_updates.
     *
     * @param payload dados da solicitacao com o status atualizado
     */
    public void publishDevelopmentRequestStatusChanged(DevelopmentRequestEventPayload payload) {
        rabbitTemplate.convertAndSend(
                RabbitMqConfig.EXCHANGE_NAME,
                RabbitMqConfig.DEVELOPMENT_REQUEST_STATUS_CHANGED_ROUTING_KEY,
                payload
        );
        messageAuditService.registrarPublicacao(
                RabbitMqConfig.DEVELOPMENT_REQUEST_STATUS_CHANGED_ROUTING_KEY,
                RabbitMqConfig.EXCHANGE_NAME,
                RabbitMqConfig.DEVELOPMENT_REQUEST_STATUS_CHANGED_ROUTING_KEY,
                payload
        );
    }

    public void publishDevelopmentRequestCancelled(DevelopmentRequestEventPayload payload) {
        rabbitTemplate.convertAndSend(
                RabbitMqConfig.EXCHANGE_NAME,
                RabbitMqConfig.DEVELOPMENT_REQUEST_CANCELLED_ROUTING_KEY,
                payload
        );
        messageAuditService.registrarPublicacao(
                RabbitMqConfig.DEVELOPMENT_REQUEST_CANCELLED_ROUTING_KEY,
                RabbitMqConfig.EXCHANGE_NAME,
                RabbitMqConfig.DEVELOPMENT_REQUEST_CANCELLED_ROUTING_KEY,
                payload
        );
    }
}
