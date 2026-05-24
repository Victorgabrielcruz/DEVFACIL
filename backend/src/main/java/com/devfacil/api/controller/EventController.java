package com.devfacil.api.controller;

import com.devfacil.api.model.EventOutbox;
import com.devfacil.api.repository.EventOutboxRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class EventController {

    private final EventOutboxRepository eventOutboxRepository;

    public EventController(EventOutboxRepository eventOutboxRepository) {
        this.eventOutboxRepository = eventOutboxRepository;
    }

    @GetMapping("/eventos")
    public List<EventOutbox> listar() {
        return eventOutboxRepository.findAll();
    }
}
