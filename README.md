# DevFacil

DevFacil e uma plataforma que conecta clientes a desenvolvedores e engenheiros de software para contratacao de servicos de desenvolvimento, como sites, aplicativos, APIs, sistemas web, manutencao de sistemas e consultoria tecnica.


## Estrutura

```text
backend/              API REST Spring Boot
collections/          Colecao Postman
docs/                 Proposta, arquitetura, schema e eventos
compose.yaml          Infraestrutura para API, PostgreSQL e RabbitMQ
```

## Como executar

```bash
podman compose up --build
```

Ou, usando Docker:

```bash
docker compose up --build
```

Servicos principais:

```text
API REST: http://127.0.0.1:8080
PostgreSQL: localhost:5432
RabbitMQ AMQP: localhost:5672
RabbitMQ Management: http://localhost:15672
```

Credenciais do RabbitMQ Management:

```text
usuario: devfacil
senha: devfacil
```

O PostgreSQL e o RabbitMQ sobem como containers junto com a API. A API cria as tabelas automaticamente via JPA/Hibernate e declara a exchange, filas e bindings do RabbitMQ na inicializacao.

Se voce ja rodou uma versao anterior do projeto com outro dominio, limpe o volume do banco antes de subir novamente:

```bash
podman compose down -v
podman compose up --build
```

## Endpoints principais

| Metodo | Rota | Descricao |
| --- | --- | --- |
| GET | `/health` | Verifica se a API esta ativa |
| POST | `/clientes` | Cadastra um cliente |
| POST | `/desenvolvedores` | Cadastra um desenvolvedor |
| GET | `/desenvolvedores` | Lista desenvolvedores |
| POST | `/solicitacoes` | Cria uma solicitacao de servico de desenvolvimento |
| GET | `/solicitacoes` | Lista solicitacoes |
| GET | `/solicitacoes/{id}` | Consulta uma solicitacao |
| PATCH | `/solicitacoes/{id}/status` | Atualiza o status |
| DELETE | `/solicitacoes/{id}` | Remove uma solicitacao |
| GET | `/eventos` | Lista eventos registrados no outbox |

## Fluxo Sprint 2 - RabbitMQ

1. Cliente cria uma solicitacao de desenvolvimento, informando tipo de servico, requisitos, prazo e orcamento estimado.
2. Backend persiste a solicitacao no PostgreSQL.
3. Backend registra no outbox e publica o evento `development_request.created` no RabbitMQ.
4. Desenvolvedor aceita, recusa ou atualiza o andamento pelo endpoint REST.
5. Backend registra no outbox e publica o evento `development_request.status_changed` no RabbitMQ.
6. Consumidores demonstrativos escutam as filas e registram no log o recebimento dos eventos.

Eventos implementados:

| Evento | Exchange | Routing key | Fila |
| --- | --- | --- | --- |
| `development_request.created` | `devfacil.events` | `development_request.created` | `devfacil.development_requests` |
| `development_request.status_changed` | `devfacil.events` | `development_request.status_changed` | `devfacil.status_updates` |

Logs esperados na API:

```text
[RabbitMQ] Nova solicitacao recebida para desenvolvedor...
[RabbitMQ] Atualizacao de status recebida para cliente...
```

## Artefatos do projeto

- [Proposta de dominio](docs/proposta_sprint1.md)
- [Arquitetura](docs/arquitetura.md)
- [Schema PostgreSQL](docs/schema_postgresql.md)
- [Eventos implementados na Sprint 2](docs/eventos_previstos.md)
- [Relatorio de integracao da Sprint 2](docs/relatorio_integracao_sprint2.md)
- [Colecao Postman](collections/devfacil.postman_collection.json)
