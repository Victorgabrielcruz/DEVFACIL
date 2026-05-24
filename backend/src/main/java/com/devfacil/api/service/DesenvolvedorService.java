package com.devfacil.api.service;

import com.devfacil.api.dto.DesenvolvedorRequest;
import com.devfacil.api.exception.ConflitoException;
import com.devfacil.api.model.Desenvolvedor;
import com.devfacil.api.repository.DesenvolvedorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DesenvolvedorService {

    private final DesenvolvedorRepository desenvolvedorRepository;

    public DesenvolvedorService(DesenvolvedorRepository desenvolvedorRepository) {
        this.desenvolvedorRepository = desenvolvedorRepository;
    }

    @Transactional
    public Desenvolvedor criar(DesenvolvedorRequest request) {
        if (desenvolvedorRepository.existsByEmail(request.email())) {
            throw new ConflitoException("email de desenvolvedor ja cadastrado");
        }

        Desenvolvedor desenvolvedor = new Desenvolvedor();
        desenvolvedor.setNome(request.nome());
        desenvolvedor.setTelefone(request.telefone());
        desenvolvedor.setEmail(request.email());
        desenvolvedor.setStack(request.stack());
        desenvolvedor.setSenioridade(request.senioridade());
        desenvolvedor.setPortfolioUrl(request.portfolioUrl());
        desenvolvedor.setDisponivel(request.disponivel() == null || request.disponivel());
        return desenvolvedorRepository.save(desenvolvedor);
    }

    @Transactional(readOnly = true)
    public List<Desenvolvedor> listar() {
        return desenvolvedorRepository.findAll();
    }
}
