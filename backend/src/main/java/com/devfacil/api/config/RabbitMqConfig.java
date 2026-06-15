package com.devfacil.api.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Classe de configuracao do RabbitMQ utilizado pelo DevFacil.
 *
 * Esta classe declara os principais componentes do Middleware Orientado a
 * Mensagens (MOM): exchange, filas, bindings e conversor de mensagens JSON.
 *
 * Ao iniciar a aplicacao, o Spring Boot usa esta configuracao para criar a
 * estrutura necessaria no RabbitMQ, permitindo que o backend publique eventos
 * e que consumidores recebam essas mensagens de forma assincrona.
 */
@Configuration
public class RabbitMqConfig {

    /**
     * Nome da exchange principal de eventos do DevFacil.
     *
     * A exchange recebe as mensagens publicadas pelo backend e decide para qual
     * fila cada mensagem deve ser enviada, de acordo com a routing key.
     */
    public static final String EXCHANGE_NAME = "devfacil.events";

    /**
     * Fila responsavel por armazenar eventos de novas solicitacoes criadas.
     *
     * Essa fila pode ser consumida futuramente pelo aplicativo do desenvolvedor,
     * para que ele seja notificado quando um cliente criar uma nova demanda.
     */
    public static final String DEVELOPMENT_REQUESTS_QUEUE = "devfacil.development_requests";

    /**
     * Fila responsavel por armazenar eventos de alteracao de status.
     *
     * Essa fila pode ser consumida futuramente pelo aplicativo do cliente,
     * permitindo que ele receba atualizacoes quando uma solicitacao for aceita,
     * recusada ou concluida.
     */
    public static final String STATUS_UPDATES_QUEUE = "devfacil.status_updates";

    /**
     * Routing key usada quando uma nova solicitacao de desenvolvimento e criada.
     *
     * Mensagens publicadas com essa chave sao direcionadas para a fila
     * devfacil.development_requests.
     */
    public static final String DEVELOPMENT_REQUEST_CREATED_ROUTING_KEY =
            "development_request.created";

    /**
     * Routing key usada quando o status de uma solicitacao e alterado.
     *
     * Mensagens publicadas com essa chave sao direcionadas para a fila
     * devfacil.status_updates.
     */
    public static final String DEVELOPMENT_REQUEST_STATUS_CHANGED_ROUTING_KEY =
            "development_request.status_changed";

    public static final String DEVELOPMENT_REQUEST_CANCELLED_ROUTING_KEY =
            "development_request.cancelled";

    /**
     * Declara a exchange de eventos do DevFacil.
     *
     * Foi utilizada uma TopicExchange porque os eventos seguem um padrao de
     * nomes baseado em topicos, como development_request.created e
     * development_request.status_changed.
     *
     * @return exchange do tipo topico usada para distribuir eventos do sistema
     */
    @Bean
    public TopicExchange devfacilEventsExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    /**
     * Declara a fila que recebe eventos de criacao de solicitacoes.
     *
     * O parametro true indica que a fila e duravel, ou seja, ela continua
     * existindo mesmo se o RabbitMQ for reiniciado.
     *
     * @return fila de eventos de novas solicitacoes
     */
    @Bean
    public Queue developmentRequestsQueue() {
        return new Queue(DEVELOPMENT_REQUESTS_QUEUE, true);
    }

    /**
     * Declara a fila que recebe eventos de alteracao de status.
     *
     * O parametro true indica que a fila e duravel.
     *
     * @return fila de eventos de atualizacao de status
     */
    @Bean
    public Queue statusUpdatesQueue() {
        return new Queue(STATUS_UPDATES_QUEUE, true);
    }

    /**
     * Cria o binding entre a exchange principal e a fila de novas solicitacoes.
     *
     * Esse binding informa ao RabbitMQ que toda mensagem publicada na exchange
     * devfacil.events com a routing key development_request.created deve ser
     * entregue na fila devfacil.development_requests.
     *
     * @return ligacao entre exchange, routing key e fila de novas solicitacoes
     */
    @Bean
    public Binding developmentRequestsBinding() {
        return BindingBuilder
                .bind(developmentRequestsQueue())
                .to(devfacilEventsExchange())
                .with(DEVELOPMENT_REQUEST_CREATED_ROUTING_KEY);
    }

    /**
     * Cria o binding entre a exchange principal e a fila de atualizacao de status.
     *
     * Esse binding informa ao RabbitMQ que toda mensagem publicada na exchange
     * devfacil.events com a routing key development_request.status_changed deve
     * ser entregue na fila devfacil.status_updates.
     *
     * @return ligacao entre exchange, routing key e fila de status
     */
    @Bean
    public Binding statusUpdatesBinding() {
        return BindingBuilder
                .bind(statusUpdatesQueue())
                .to(devfacilEventsExchange())
                .with(DEVELOPMENT_REQUEST_STATUS_CHANGED_ROUTING_KEY);
    }

    @Bean
    public Binding cancellationUpdatesBinding() {
        return BindingBuilder
                .bind(statusUpdatesQueue())
                .to(devfacilEventsExchange())
                .with(DEVELOPMENT_REQUEST_CANCELLED_ROUTING_KEY);
    }

    /**
     * Configura o conversor de mensagens para JSON.
     *
     * Esse conversor permite que objetos Java sejam enviados ao RabbitMQ como
     * mensagens JSON, facilitando a leitura, o log, a demonstracao e o consumo
     * por outros componentes do sistema.
     *
     * @return conversor JSON usado pelo Spring AMQP
     */
    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
