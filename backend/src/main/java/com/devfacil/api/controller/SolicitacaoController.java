package com.devfacil.api.controller;

import com.devfacil.api.dto.SolicitacaoRequest;
import com.devfacil.api.dto.SolicitacaoResponse;
import com.devfacil.api.dto.StatusUpdateRequest;
import com.devfacil.api.model.StatusSolicitacao;
import com.devfacil.api.service.SolicitacaoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SolicitacaoController {

    private final SolicitacaoService solicitacaoService;

    public SolicitacaoController(SolicitacaoService solicitacaoService) {
        this.solicitacaoService = solicitacaoService;
    }

    @PostMapping("/solicitacoes")
    @ResponseStatus(HttpStatus.CREATED)
    public SolicitacaoResponse criar(@Valid @RequestBody SolicitacaoRequest request) {
        return SolicitacaoResponse.from(solicitacaoService.criar(request));
    }

    @GetMapping("/solicitacoes")
    public List<SolicitacaoResponse> listar(@RequestParam(required = false) String status) {
        StatusSolicitacao statusFiltro = status == null ? null : StatusSolicitacao.fromValue(status);
        return solicitacaoService.listar(statusFiltro).stream()
                .map(SolicitacaoResponse::from)
                .toList();
    }

    @GetMapping("/solicitacoes/{id}")
    public SolicitacaoResponse buscar(@PathVariable Long id) {
        return SolicitacaoResponse.from(solicitacaoService.buscar(id));
    }

    @PatchMapping("/solicitacoes/{id}/status")
    public SolicitacaoResponse atualizarStatus(
            @PathVariable Long id,
            @Valid @RequestBody StatusUpdateRequest request
    ) {
        return SolicitacaoResponse.from(solicitacaoService.atualizarStatus(id, request));
    }

    @DeleteMapping("/solicitacoes/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Long id) {
        solicitacaoService.remover(id);
    }
}
