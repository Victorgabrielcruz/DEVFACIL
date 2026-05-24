package com.devfacil.api.event;

import java.time.Instant;
import java.util.Map;

public record DomainEvent(
        String eventName,
        String aggregateType,
        Long aggregateId,
        Instant occurredAt,
        Map<String, Object> payload
) {
}
