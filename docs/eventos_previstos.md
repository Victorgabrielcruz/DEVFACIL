# Eventos Implementados - Sprint 2

Na Sprint 2, o DevFacil passou a publicar e consumir eventos reais por meio do RabbitMQ. O backend REST continua recebendo as acoes principais por HTTP, mas os acontecimentos importantes do dominio sao propagados de forma assincrona usando uma exchange do RabbitMQ.

## Componentes RabbitMQ

| Componente | Nome | Funcao |
| --- | --- | --- |
| Exchange | `devfacil.events` | Recebe os eventos publicados pelo backend e roteia para as filas corretas. |
| Tipo da exchange | `topic` | Permite rotear mensagens por routing keys como `development_request.created`. |
| Fila | `devfacil.development_requests` | Armazena eventos de novas solicitacoes para o consumidor do desenvolvedor. |
| Fila | `devfacil.status_updates` | Armazena eventos de mudanca de status para o consumidor do cliente. |

## Eventos

| Evento | Produtor | Consumidor | Exchange | Routing key | Fila | Payload exemplo |
| --- | --- | --- | --- | --- | --- | --- |
| `development_request.created` | Backend REST | `RabbitMqEventConsumer` do desenvolvedor | `devfacil.events` | `development_request.created` | `devfacil.development_requests` | `{"solicitacaoId":1,"clienteId":1,"desenvolvedorId":null,"tipoServico":"aplicativo_mobile","status":"aberta","occurredAt":"2026-05-24T21:08:41Z"}` |
| `development_request.status_changed` | Backend REST | `RabbitMqEventConsumer` do cliente | `devfacil.events` | `development_request.status_changed` | `devfacil.status_updates` | `{"solicitacaoId":1,"clienteId":1,"desenvolvedorId":1,"tipoServico":"aplicativo_mobile","status":"aceita","occurredAt":"2026-05-24T21:08:42Z"}` |

## Fluxo de criacao de solicitacao

1. O cliente chama `POST /solicitacoes`.
2. O backend valida os dados e salva a solicitacao no PostgreSQL.
3. O backend registra o evento no outbox local para historico.
4. O backend publica `development_request.created` na exchange `devfacil.events`.
5. O RabbitMQ roteia a mensagem para a fila `devfacil.development_requests`.
6. O consumidor escuta a fila e registra no log que uma nova solicitacao foi recebida para o desenvolvedor.

Log esperado:

```text
[RabbitMQ] Nova solicitacao recebida para desenvolvedor: solicitacaoId=1, clienteId=1, tipoServico=aplicativo_mobile, status=aberta
```

## Fluxo de alteracao de status

1. O desenvolvedor ou operador chama `PATCH /solicitacoes/{id}/status`.
2. O backend atualiza o status da solicitacao no PostgreSQL.
3. O backend registra o evento no outbox local para historico.
4. O backend publica `development_request.status_changed` na exchange `devfacil.events`.
5. O RabbitMQ roteia a mensagem para a fila `devfacil.status_updates`.
6. O consumidor escuta a fila e registra no log que o cliente recebeu uma atualizacao de status.

Log esperado:

```text
[RabbitMQ] Atualizacao de status recebida para cliente: solicitacaoId=1, clienteId=1, desenvolvedorId=1, status=aceita
```

## Evidencia de assincronicidade

A comunicacao assincrona e demonstrada porque o backend nao chama diretamente o consumidor por REST. O backend apenas publica uma mensagem no RabbitMQ. O consumidor recebe a mensagem posteriormente a partir da fila configurada.

Fluxo resumido:

```text
Backend REST -> RabbitMQ exchange -> RabbitMQ fila -> Consumer
```

Isso desacopla o produtor do consumidor e permite que futuras aplicacoes Flutter ou servicos de notificacao consumam os eventos sem alterar o fluxo principal de criacao e atualizacao de solicitacoes.
