package com.devfacil.api.controller;

import com.devfacil.api.dto.DesenvolvedorRequest;
import com.devfacil.api.dto.DesenvolvedorResponse;
import com.devfacil.api.service.DesenvolvedorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class DesenvolvedorController {

    private final DesenvolvedorService desenvolvedorService;

    public DesenvolvedorController(DesenvolvedorService desenvolvedorService) {
        this.desenvolvedorService = desenvolvedorService;
    }

    @PostMapping("/desenvolvedores")
    @ResponseStatus(HttpStatus.CREATED)
    public DesenvolvedorResponse criar(@Valid @RequestBody DesenvolvedorRequest request) {
        return DesenvolvedorResponse.from(desenvolvedorService.criar(request));
    }

    @GetMapping("/desenvolvedores")
    public List<DesenvolvedorResponse> listar() {
        return desenvolvedorService.listar().stream()
                .map(DesenvolvedorResponse::from)
                .toList();
    }

    @GetMapping("/desenvolvedores/{id}")
    public DesenvolvedorResponse buscar(@PathVariable Long id) {
        return DesenvolvedorResponse.from(desenvolvedorService.buscar(id));
    }

    @PutMapping("/desenvolvedores/{id}")
    public DesenvolvedorResponse atualizar(@PathVariable Long id, @Valid @RequestBody DesenvolvedorRequest request) {
        return DesenvolvedorResponse.from(desenvolvedorService.atualizar(id, request));
    }

    @DeleteMapping("/desenvolvedores/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Long id) {
        desenvolvedorService.remover(id);
    }
}
