package com.devfacil.api.service;

import com.devfacil.api.dto.SolicitacaoRequest;
import com.devfacil.api.dto.StatusUpdateRequest;
import com.devfacil.api.event.DevelopmentRequestEventPayload;
import com.devfacil.api.event.DomainEvent;
import com.devfacil.api.event.RabbitMqEventPublisher;
import com.devfacil.api.exception.RecursoNaoEncontradoException;
import com.devfacil.api.model.Cliente;
import com.devfacil.api.model.Desenvolvedor;
import com.devfacil.api.model.Solicitacao;
import com.devfacil.api.model.StatusSolicitacao;
import com.devfacil.api.model.Prioridade;
import com.devfacil.api.repository.ClienteRepository;
import com.devfacil.api.repository.DesenvolvedorRepository;
import com.devfacil.api.repository.SolicitacaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class SolicitacaoService {

    private final SolicitacaoRepository solicitacaoRepository;
    private final ClienteRepository clienteRepository;
    private final DesenvolvedorRepository desenvolvedorRepository;
    private final EventOutboxService eventOutboxService;
    private final RabbitMqEventPublisher rabbitMqEventPublisher;

    public SolicitacaoService(
            SolicitacaoRepository solicitacaoRepository,
            ClienteRepository clienteRepository,
            DesenvolvedorRepository desenvolvedorRepository,
            EventOutboxService eventOutboxService,
            RabbitMqEventPublisher rabbitMqEventPublisher
    ) {
        this.solicitacaoRepository = solicitacaoRepository;
        this.clienteRepository = clienteRepository;
        this.desenvolvedorRepository = desenvolvedorRepository;
        this.eventOutboxService = eventOutboxService;
        this.rabbitMqEventPublisher = rabbitMqEventPublisher;
    }

    @Transactional
    public Solicitacao criar(SolicitacaoRequest request) {
        Cliente cliente = clienteRepository.findById(request.clienteId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("cliente_id nao encontrado"));
        Desenvolvedor desenvolvedor = buscarDesenvolvedorOpcional(request.desenvolvedorId());

        Solicitacao solicitacao = new Solicitacao();
        solicitacao.setCliente(cliente);
        solicitacao.setDesenvolvedor(desenvolvedor);
        solicitacao.setTipoServico(request.tipoServico());
        solicitacao.setDescricao(request.descricao());
        solicitacao.setRequisitos(request.requisitos());
        solicitacao.setPrazoDesejado(request.prazoDesejado());
        solicitacao.setOrcamentoEstimado(request.orcamentoEstimado());
        solicitacao.setPrioridade(request.prioridade() == null ? Prioridade.NORMAL : request.prioridade());

        Solicitacao salva = solicitacaoRepository.save(solicitacao);
        eventOutboxService.registrar(new DomainEvent(
                "development_request.created",
                "Solicitacao",
                salva.getId(),
                Instant.now(),
                payloadSolicitacao(salva)
        ));
        rabbitMqEventPublisher.publishDevelopmentRequestCreated(payloadEventoRabbitMq(salva));
        return salva;
    }

    @Transactional(readOnly = true)
    public List<Solicitacao> listar(StatusSolicitacao status) {
        if (status == null) {
            return solicitacaoRepository.findAll();
        }
        return solicitacaoRepository.findByStatus(status);
    }

    @Transactional(readOnly = true)
    public Solicitacao buscar(Long id) {
        return solicitacaoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("solicitacao nao encontrada"));
    }

    @Transactional
    public Solicitacao atualizarStatus(Long id, StatusUpdateRequest request) {
        Solicitacao solicitacao = buscar(id);
        Desenvolvedor desenvolvedor = buscarDesenvolvedorOpcional(request.desenvolvedorId());

        if (desenvolvedor != null) {
            solicitacao.setDesenvolvedor(desenvolvedor);
        }
        solicitacao.setStatus(request.status());

        Solicitacao salva = solicitacaoRepository.save(solicitacao);
        eventOutboxService.registrar(new DomainEvent(
                "development_request.status_changed",
                "Solicitacao",
                salva.getId(),
                Instant.now(),
                payloadSolicitacao(salva)
        ));
        rabbitMqEventPublisher.publishDevelopmentRequestStatusChanged(payloadEventoRabbitMq(salva));
        return salva;
    }

    @Transactional
    public void remover(Long id) {
        if (!solicitacaoRepository.existsById(id)) {
            throw new RecursoNaoEncontradoException("solicitacao nao encontrada");
        }
        solicitacaoRepository.deleteById(id);
    }

    private Desenvolvedor buscarDesenvolvedorOpcional(Long id) {
        if (id == null) {
            return null;
        }
        return desenvolvedorRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("desenvolvedor_id nao encontrado"));
    }

    private Map<String, Object> payloadSolicitacao(Solicitacao solicitacao) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("solicitacao_id", solicitacao.getId());
        payload.put("cliente_id", solicitacao.getCliente().getId());
        payload.put("desenvolvedor_id", solicitacao.getDesenvolvedor() == null ? null : solicitacao.getDesenvolvedor().getId());
        payload.put("tipo_servico", solicitacao.getTipoServico());
        payload.put("status", solicitacao.getStatus().getValue());
        return payload;
    }

    private DevelopmentRequestEventPayload payloadEventoRabbitMq(Solicitacao solicitacao) {
        return new DevelopmentRequestEventPayload(
                solicitacao.getId(),
                solicitacao.getCliente().getId(),
                solicitacao.getDesenvolvedor() == null ? null : solicitacao.getDesenvolvedor().getId(),
                solicitacao.getTipoServico(),
                solicitacao.getStatus().getValue(),
                Instant.now()
        );
    }
}
