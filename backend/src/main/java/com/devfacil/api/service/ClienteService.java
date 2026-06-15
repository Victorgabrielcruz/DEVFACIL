package com.devfacil.api.service;

import com.devfacil.api.dto.ClienteRequest;
import com.devfacil.api.exception.ConflitoException;
import com.devfacil.api.exception.RecursoNaoEncontradoException;
import com.devfacil.api.model.Cliente;
import com.devfacil.api.repository.ClienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
        cliente.setEmpresa(normalizarOpcional(request.empresa()));
        return clienteRepository.save(cliente);
    }

    @Transactional(readOnly = true)
    public List<Cliente> listar() {
        return clienteRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Cliente buscar(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("cliente nao encontrado"));
    }

    @Transactional
    public Cliente atualizar(Long id, ClienteRequest request) {
        Cliente cliente = buscar(id);

        if (clienteRepository.existsByEmailAndIdNot(request.email(), id)) {
            throw new ConflitoException("email de cliente ja cadastrado");
        }

        cliente.setNome(request.nome());
        cliente.setTelefone(request.telefone());
        cliente.setEmail(request.email());
        cliente.setEmpresa(normalizarOpcional(request.empresa()));
        return clienteRepository.save(cliente);
    }

    @Transactional
    public void remover(Long id) {
        if (!clienteRepository.existsById(id)) {
            throw new RecursoNaoEncontradoException("cliente nao encontrado");
        }
        clienteRepository.deleteById(id);
    }

    private String normalizarOpcional(String value) {
        return value == null ? "" : value.trim();
    }
}
