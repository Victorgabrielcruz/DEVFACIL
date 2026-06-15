package com.devfacil.api.controller;

import com.devfacil.api.model.EventOutbox;
import com.devfacil.api.repository.EventOutboxRepository;
import com.devfacil.api.service.CurrentUserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class EventController {

    private final EventOutboxRepository eventOutboxRepository;
    private final CurrentUserService currentUserService;

    public EventController(EventOutboxRepository eventOutboxRepository, CurrentUserService currentUserService) {
        this.eventOutboxRepository = eventOutboxRepository;
        this.currentUserService = currentUserService;
    }

    @GetMapping("/eventos")
    public List<EventOutbox> listar(HttpServletRequest request) {
        currentUserService.exigirAdmin(request);
        return eventOutboxRepository.findAll();
    }
}
