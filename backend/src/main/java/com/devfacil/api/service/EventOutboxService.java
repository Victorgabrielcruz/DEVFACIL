package com.devfacil.api.service;

import com.devfacil.api.event.DomainEvent;
import com.devfacil.api.model.EventOutbox;
import com.devfacil.api.repository.EventOutboxRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

@Service
public class EventOutboxService {

    private final EventOutboxRepository eventOutboxRepository;
    private final ObjectMapper objectMapper;

    public EventOutboxService(
            EventOutboxRepository eventOutboxRepository,
            ObjectMapper objectMapper
    ) {
        this.eventOutboxRepository = eventOutboxRepository;
        this.objectMapper = objectMapper;
    }

    public void registrar(DomainEvent event) {
        EventOutbox outbox = new EventOutbox();
        outbox.setEventName(event.eventName());
        outbox.setAggregateType(event.aggregateType());
        outbox.setAggregateId(event.aggregateId());
        outbox.setPayload(toJson(event));
        outbox.setPublished(false);
        eventOutboxRepository.save(outbox);
    }

    private String toJson(DomainEvent event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("nao foi possivel serializar o evento", exception);
        }
    }
}
