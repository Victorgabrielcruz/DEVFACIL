package com.devfacil.api.service;

import com.devfacil.api.model.MessageAudit;
import com.devfacil.api.repository.MessageAuditRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageAuditService {

    private static final String PUBLISHED = "PUBLISHED";
    private static final String CONSUMED = "CONSUMED";

    private final MessageAuditRepository messageAuditRepository;
    private final ObjectMapper objectMapper;

    public MessageAuditService(
            MessageAuditRepository messageAuditRepository,
            ObjectMapper objectMapper
    ) {
        this.messageAuditRepository = messageAuditRepository;
        this.objectMapper = objectMapper;
    }

    public void registrarPublicacao(String eventName, String exchangeName, String routingKey, Object payload) {
        registrar(PUBLISHED, eventName, exchangeName, routingKey, null, payload);
    }

    public void registrarConsumo(String eventName, String exchangeName, String routingKey, String queueName, Object payload) {
        registrar(CONSUMED, eventName, exchangeName, routingKey, queueName, payload);
    }

    public List<MessageAudit> listarRecentes() {
        return messageAuditRepository.findTop100ByOrderByCreatedAtDesc();
    }

    private void registrar(
            String direction,
            String eventName,
            String exchangeName,
            String routingKey,
            String queueName,
            Object payload
    ) {
        MessageAudit audit = new MessageAudit();
        audit.setDirection(direction);
        audit.setEventName(eventName);
        audit.setExchangeName(exchangeName);
        audit.setRoutingKey(routingKey);
        audit.setQueueName(queueName);
        audit.setPayload(toJson(payload));
        messageAuditRepository.save(audit);
    }

    private String toJson(Object payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("nao foi possivel serializar o payload de auditoria", exception);
        }
    }
}
