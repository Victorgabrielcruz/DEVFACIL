package com.devfacil.api.service;

import com.devfacil.api.dto.ClienteRequest;
import com.devfacil.api.exception.ConflitoException;
import com.devfacil.api.model.Cliente;
import com.devfacil.api.repository.ClienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Transactional
    public Cliente criar(ClienteRequest request) {
        if (clienteRepository.existsByEmail(request.email())) {
            throw new ConflitoException("email de cliente ja cadastrado");
        }

        Cliente cliente = new Cliente();
        cliente.setNome(request.nome());
        cliente.setTelefone(request.telefone());
        cliente.setEmail(request.email());
        cliente.setEmpresa(request.empresa());
        return clienteRepository.save(cliente);
    }
}
