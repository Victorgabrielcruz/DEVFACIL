package com.devfacil.api.service;

import com.devfacil.api.dto.CancelamentoRequest;
import com.devfacil.api.dto.SolicitacaoRequest;
import com.devfacil.api.dto.StatusUpdateRequest;
import com.devfacil.api.event.DevelopmentRequestEventPayload;
import com.devfacil.api.event.DomainEvent;
import com.devfacil.api.event.RabbitMqEventPublisher;
import com.devfacil.api.exception.AcessoNegadoException;
import com.devfacil.api.exception.RecursoNaoEncontradoException;
import com.devfacil.api.model.Cliente;
import com.devfacil.api.model.Desenvolvedor;
import com.devfacil.api.model.PerfilUsuario;
import com.devfacil.api.model.Solicitacao;
import com.devfacil.api.model.StatusSolicitacao;
import com.devfacil.api.model.Prioridade;
import com.devfacil.api.model.Usuario;
import com.devfacil.api.repository.ClienteRepository;
import com.devfacil.api.repository.DesenvolvedorRepository;
import com.devfacil.api.repository.SolicitacaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
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
    public Solicitacao criar(SolicitacaoRequest request, Usuario usuario) {
        validarSolicitacao(request);
        if (usuario.getPerfil() == PerfilUsuario.DESENVOLVEDOR) {
            throw new AcessoNegadoException("desenvolvedor nao pode criar solicitacao");
        }

        Long clienteId = usuario.getPerfil() == PerfilUsuario.CLIENTE
                ? usuario.getCliente().getId()
                : request.clienteId();
        Cliente cliente = clienteRepository.findById(clienteId)
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
    public List<Solicitacao> listar(StatusSolicitacao status, Usuario usuario) {
        if (usuario.getPerfil() == PerfilUsuario.CLIENTE) {
            return filtrarStatus(solicitacaoRepository.findByClienteId(usuario.getCliente().getId()), status);
        }
        if (usuario.getPerfil() == PerfilUsuario.DESENVOLVEDOR) {
            return filtrarStatus(
                    solicitacaoRepository.findDisponiveisOuDoDesenvolvedor(
                            usuario.getDesenvolvedor().getId(),
                            StatusSolicitacao.ABERTA
                    ),
                    status
            );
        }
        return status == null ? solicitacaoRepository.findAll() : solicitacaoRepository.findByStatus(status);
    }

    @Transactional(readOnly = true)
    public Solicitacao buscar(Long id, Usuario usuario) {
        Solicitacao solicitacao = solicitacaoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("solicitacao nao encontrada"));
        exigirPodeVisualizar(solicitacao, usuario);
        return solicitacao;
    }

    @Transactional
    public Solicitacao atualizar(Long id, SolicitacaoRequest request, Usuario usuario) {
        validarSolicitacao(request);
        Solicitacao solicitacao = buscar(id, usuario);
        exigirPodeEditar(solicitacao, usuario);
        exigirNaoFinalizada(solicitacao);

        Long clienteId = usuario.getPerfil() == PerfilUsuario.CLIENTE
                ? usuario.getCliente().getId()
                : request.clienteId();
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("cliente_id nao encontrado"));
        Desenvolvedor desenvolvedor = buscarDesenvolvedorOpcional(request.desenvolvedorId());

        solicitacao.setCliente(cliente);
        solicitacao.setDesenvolvedor(desenvolvedor);
        solicitacao.setTipoServico(request.tipoServico());
        solicitacao.setDescricao(request.descricao());
        solicitacao.setRequisitos(request.requisitos());
        solicitacao.setPrazoDesejado(request.prazoDesejado());
        solicitacao.setOrcamentoEstimado(request.orcamentoEstimado());
        solicitacao.setPrioridade(request.prioridade() == null ? Prioridade.NORMAL : request.prioridade());
        return solicitacaoRepository.save(solicitacao);
    }

    @Transactional
    public Solicitacao atualizarStatus(Long id, StatusUpdateRequest request, Usuario usuario) {
        Solicitacao solicitacao = buscar(id, usuario);
        exigirPodeAtualizarStatus(solicitacao, usuario);
        validarTransicaoStatus(solicitacao, request.status(), usuario);

        Long desenvolvedorId = usuario.getPerfil() == PerfilUsuario.DESENVOLVEDOR
                ? usuario.getDesenvolvedor().getId()
                : request.desenvolvedorId();
        Desenvolvedor desenvolvedor = buscarDesenvolvedorOpcional(desenvolvedorId);

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
    public Solicitacao cancelar(Long id, CancelamentoRequest request, Usuario usuario) {
        Solicitacao solicitacao = buscar(id, usuario);
        exigirPodeCancelar(solicitacao, usuario);
        solicitacao.setStatus(StatusSolicitacao.CANCELADA);
        solicitacao.setMotivoCancelamento(normalizarTextoOpcional(
                request == null ? null : request.motivoCancelamento(),
                500
        ));

        Solicitacao salva = solicitacaoRepository.save(solicitacao);
        eventOutboxService.registrar(new DomainEvent(
                "development_request.cancelled",
                "Solicitacao",
                salva.getId(),
                Instant.now(),
                payloadSolicitacao(salva)
        ));
        rabbitMqEventPublisher.publishDevelopmentRequestCancelled(payloadEventoRabbitMq(salva));
        return salva;
    }

    @Transactional
    public Solicitacao anexarZip(Long id, MultipartFile arquivo, Usuario usuario) {
        Solicitacao solicitacao = buscar(id, usuario);
        exigirPodeAnexarZip(solicitacao, usuario);

        String originalFilename = arquivo.getOriginalFilename() == null ? "" : arquivo.getOriginalFilename();
        if (!originalFilename.toLowerCase().endsWith(".zip")) {
            throw new IllegalArgumentException("envie um arquivo .zip");
        }

        try {
            Path uploads = Path.of("uploads", "solicitacoes");
            Files.createDirectories(uploads);
            String filename = "solicitacao-" + id + "-" + Instant.now().toEpochMilli() + ".zip";
            Path destino = uploads.resolve(filename);
            Files.copy(arquivo.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);
            solicitacao.setZipNome(originalFilename);
            solicitacao.setZipPath(destino.toString());
            return solicitacaoRepository.save(solicitacao);
        } catch (IOException exception) {
            throw new IllegalStateException("nao foi possivel salvar o zip", exception);
        }
    }

    @Transactional
    public void remover(Long id, Usuario usuario) {
        Solicitacao solicitacao = buscar(id, usuario);
        exigirPodeEditar(solicitacao, usuario);
        solicitacaoRepository.delete(solicitacao);
    }

    private List<Solicitacao> filtrarStatus(List<Solicitacao> solicitacoes, StatusSolicitacao status) {
        if (status == null) {
            return solicitacoes;
        }
        return solicitacoes.stream()
                .filter(solicitacao -> solicitacao.getStatus() == status)
                .toList();
    }

    private void validarSolicitacao(SolicitacaoRequest request) {
        if (request.descricao() == null || request.descricao().trim().length() < 10) {
            throw new IllegalArgumentException("descricao deve ter pelo menos 10 caracteres");
        }
        if (request.requisitos() == null || request.requisitos().trim().length() < 10) {
            throw new IllegalArgumentException("requisitos deve ter pelo menos 10 caracteres");
        }
        if (request.orcamentoEstimado() != null && request.orcamentoEstimado() < 0) {
            throw new IllegalArgumentException("orcamento nao pode ser negativo");
        }
        if (request.prazoDesejado() != null && !request.prazoDesejado().isBlank()) {
            try {
                LocalDate prazo = LocalDate.parse(request.prazoDesejado());
                if (prazo.isBefore(LocalDate.now())) {
                    throw new IllegalArgumentException("prazo nao pode estar no passado");
                }
            } catch (DateTimeParseException exception) {
                throw new IllegalArgumentException("prazo deve usar o formato yyyy-MM-dd");
            }
        }
    }

    private void exigirPodeVisualizar(Solicitacao solicitacao, Usuario usuario) {
        if (usuario.getPerfil() == PerfilUsuario.ADMIN) {
            return;
        }
        if (usuario.getPerfil() == PerfilUsuario.CLIENTE
                && solicitacao.getCliente().getId().equals(usuario.getCliente().getId())) {
            return;
        }
        if (usuario.getPerfil() == PerfilUsuario.DESENVOLVEDOR
                && (solicitacao.getStatus() == StatusSolicitacao.ABERTA
                || (solicitacao.getDesenvolvedor() != null
                && solicitacao.getDesenvolvedor().getId().equals(usuario.getDesenvolvedor().getId())))) {
            return;
        }
        throw new AcessoNegadoException("sem permissao para acessar esta solicitacao");
    }

    private void exigirPodeEditar(Solicitacao solicitacao, Usuario usuario) {
        if (usuario.getPerfil() == PerfilUsuario.ADMIN) {
            return;
        }
        if (usuario.getPerfil() == PerfilUsuario.CLIENTE
                && solicitacao.getCliente().getId().equals(usuario.getCliente().getId())) {
            return;
        }
        throw new AcessoNegadoException("sem permissao para alterar esta solicitacao");
    }

    private void exigirNaoFinalizada(Solicitacao solicitacao) {
        if (solicitacao.getStatus() == StatusSolicitacao.CONCLUIDA
                || solicitacao.getStatus() == StatusSolicitacao.CANCELADA) {
            throw new AcessoNegadoException("solicitacao finalizada nao pode ser alterada");
        }
    }

    private void exigirPodeAtualizarStatus(Solicitacao solicitacao, Usuario usuario) {
        if (usuario.getPerfil() == PerfilUsuario.ADMIN) {
            return;
        }
        if (usuario.getPerfil() == PerfilUsuario.DESENVOLVEDOR
                && (solicitacao.getStatus() == StatusSolicitacao.ABERTA
                || (solicitacao.getDesenvolvedor() != null
                && solicitacao.getDesenvolvedor().getId().equals(usuario.getDesenvolvedor().getId())))) {
            return;
        }
        throw new AcessoNegadoException("sem permissao para atualizar status");
    }

    private void validarTransicaoStatus(Solicitacao solicitacao, StatusSolicitacao novoStatus, Usuario usuario) {
        if (solicitacao.getStatus() == StatusSolicitacao.CONCLUIDA
                || solicitacao.getStatus() == StatusSolicitacao.CANCELADA) {
            throw new AcessoNegadoException("solicitacao finalizada nao pode mudar status");
        }
        if (novoStatus == StatusSolicitacao.CANCELADA) {
            throw new IllegalArgumentException("use a rota de cancelamento");
        }
        if (usuario.getPerfil() == PerfilUsuario.DESENVOLVEDOR
                && novoStatus != StatusSolicitacao.EM_ANDAMENTO
                && novoStatus != StatusSolicitacao.CONCLUIDA) {
            throw new AcessoNegadoException("status nao permitido para desenvolvedor");
        }
        if (novoStatus == StatusSolicitacao.CONCLUIDA && solicitacao.getDesenvolvedor() == null) {
            throw new AcessoNegadoException("aceite a demanda antes de concluir");
        }
    }

    private void exigirPodeCancelar(Solicitacao solicitacao, Usuario usuario) {
        if (solicitacao.getStatus() == StatusSolicitacao.CONCLUIDA
                || solicitacao.getStatus() == StatusSolicitacao.CANCELADA) {
            throw new AcessoNegadoException("solicitacao finalizada nao pode ser cancelada");
        }
        if (usuario.getPerfil() == PerfilUsuario.ADMIN) {
            return;
        }
        if (usuario.getPerfil() == PerfilUsuario.CLIENTE
                && solicitacao.getCliente().getId().equals(usuario.getCliente().getId())) {
            return;
        }
        if (usuario.getPerfil() == PerfilUsuario.DESENVOLVEDOR
                && solicitacao.getDesenvolvedor() != null
                && solicitacao.getDesenvolvedor().getId().equals(usuario.getDesenvolvedor().getId())) {
            return;
        }
        throw new AcessoNegadoException("sem permissao para cancelar esta solicitacao");
    }

    private void exigirPodeAnexarZip(Solicitacao solicitacao, Usuario usuario) {
        if (usuario.getPerfil() == PerfilUsuario.ADMIN) {
            return;
        }
        if (usuario.getPerfil() == PerfilUsuario.DESENVOLVEDOR
                && solicitacao.getDesenvolvedor() != null
                && solicitacao.getDesenvolvedor().getId().equals(usuario.getDesenvolvedor().getId())) {
            return;
        }
        throw new AcessoNegadoException("somente o desenvolvedor responsavel pode anexar zip");
    }

    private String normalizarTextoOpcional(String value, int maxLength) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.length() > maxLength ? trimmed.substring(0, maxLength) : trimmed;
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
