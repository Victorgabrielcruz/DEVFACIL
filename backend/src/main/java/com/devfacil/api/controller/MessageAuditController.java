package com.devfacil.api.controller;

import com.devfacil.api.model.MessageAudit;
import com.devfacil.api.service.CurrentUserService;
import com.devfacil.api.service.MessageAuditService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class MessageAuditController {

    private final MessageAuditService messageAuditService;
    private final CurrentUserService currentUserService;

    public MessageAuditController(MessageAuditService messageAuditService, CurrentUserService currentUserService) {
        this.messageAuditService = messageAuditService;
        this.currentUserService = currentUserService;
    }

    @GetMapping("/auditoria-mensageria")
    public List<MessageAudit> listar(HttpServletRequest request) {
        currentUserService.exigirAdmin(request);
        return messageAuditService.listarRecentes();
    }
}
