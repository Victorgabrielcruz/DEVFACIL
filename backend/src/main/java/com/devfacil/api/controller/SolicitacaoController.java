package com.devfacil.api.controller;

import com.devfacil.api.dto.CancelamentoRequest;
import com.devfacil.api.dto.SolicitacaoRequest;
import com.devfacil.api.dto.SolicitacaoResponse;
import com.devfacil.api.dto.StatusUpdateRequest;
import com.devfacil.api.model.StatusSolicitacao;
import com.devfacil.api.service.CurrentUserService;
import com.devfacil.api.service.SolicitacaoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
public class SolicitacaoController {

    private final SolicitacaoService solicitacaoService;
    private final CurrentUserService currentUserService;

    public SolicitacaoController(SolicitacaoService solicitacaoService, CurrentUserService currentUserService) {
        this.solicitacaoService = solicitacaoService;
        this.currentUserService = currentUserService;
    }

    @PostMapping("/solicitacoes")
    @ResponseStatus(HttpStatus.CREATED)
    public SolicitacaoResponse criar(@Valid @RequestBody SolicitacaoRequest request, HttpServletRequest httpRequest) {
        return SolicitacaoResponse.from(solicitacaoService.criar(request, currentUserService.get(httpRequest)));
    }

    @GetMapping("/solicitacoes")
    public List<SolicitacaoResponse> listar(@RequestParam(required = false) String status, HttpServletRequest request) {
        StatusSolicitacao statusFiltro = status == null ? null : StatusSolicitacao.fromValue(status);
        return solicitacaoService.listar(statusFiltro, currentUserService.get(request)).stream()
                .map(SolicitacaoResponse::from)
                .toList();
    }

    @GetMapping("/solicitacoes/{id}")
    public SolicitacaoResponse buscar(@PathVariable Long id, HttpServletRequest request) {
        return SolicitacaoResponse.from(solicitacaoService.buscar(id, currentUserService.get(request)));
    }

    @PutMapping("/solicitacoes/{id}")
    public SolicitacaoResponse atualizar(
            @PathVariable Long id,
            @Valid @RequestBody SolicitacaoRequest request,
            HttpServletRequest httpRequest
    ) {
        return SolicitacaoResponse.from(solicitacaoService.atualizar(id, request, currentUserService.get(httpRequest)));
    }

    @PatchMapping("/solicitacoes/{id}/status")
    public SolicitacaoResponse atualizarStatus(
            @PathVariable Long id,
            @Valid @RequestBody StatusUpdateRequest request,
            HttpServletRequest httpRequest
    ) {
        return SolicitacaoResponse.from(solicitacaoService.atualizarStatus(id, request, currentUserService.get(httpRequest)));
    }

    @PatchMapping("/solicitacoes/{id}/cancelar")
    public SolicitacaoResponse cancelar(
            @PathVariable Long id,
            @RequestBody(required = false) CancelamentoRequest request,
            HttpServletRequest httpRequest
    ) {
        return SolicitacaoResponse.from(solicitacaoService.cancelar(id, request, currentUserService.get(httpRequest)));
    }

    @DeleteMapping("/solicitacoes/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Long id, HttpServletRequest request) {
        solicitacaoService.remover(id, currentUserService.get(request));
    }

    @PostMapping("/solicitacoes/{id}/zip")
    public SolicitacaoResponse anexarZip(
            @PathVariable Long id,
            @RequestParam("arquivo") MultipartFile arquivo,
            HttpServletRequest request
    ) {
        return SolicitacaoResponse.from(solicitacaoService.anexarZip(id, arquivo, currentUserService.get(request)));
    }
}
