# Relatorio de Integracao - Sprint 2

## Visao geral

Na Sprint 2, o DevFacil integrou o backend REST com o RabbitMQ, utilizado como Middleware Orientado a Mensagens (MOM). O objetivo foi transformar os principais acontecimentos do dominio em eventos publicados e consumidos de forma assincrona, mantendo o backend responsavel pela persistencia no PostgreSQL e pela publicacao das mensagens.

## Escolha do RabbitMQ

O RabbitMQ foi escolhido por ser uma solucao consolidada de mensageria, com suporte a filas, exchanges, routing keys e painel administrativo. Ele tambem possui boa integracao com Spring Boot por meio do Spring AMQP, o que facilita a declaracao de filas, publicacao de mensagens e criacao de consumidores.

No DevFacil, o RabbitMQ permite desacoplar a API REST dos consumidores interessados nos eventos. Assim, o backend nao precisa chamar diretamente um componente do desenvolvedor ou do cliente. Ele apenas publica que algo aconteceu, e os consumidores processam a mensagem de forma independente.

## Padrao utilizado

Foi utilizada uma `Topic Exchange` chamada `devfacil.events`. Esse tipo de exchange permite organizar eventos por routing keys, como `development_request.created` e `development_request.status_changed`.

As filas configuradas foram:

| Fila | Routing key | Finalidade |
| --- | --- | --- |
| `devfacil.development_requests` | `development_request.created` | Receber novas solicitacoes para o consumidor do desenvolvedor. |
| `devfacil.status_updates` | `development_request.status_changed` | Receber atualizacoes de status para o consumidor do cliente. |

## Eventos implementados

Foram implementados dois eventos principais do fluxo de negocio:

| Evento | Momento de publicacao | Consumidor |
| --- | --- | --- |
| `development_request.created` | Apos uma solicitacao ser criada e salva no banco. | Consumidor de novas demandas do desenvolvedor. |
| `development_request.status_changed` | Apos o status de uma solicitacao ser alterado e salvo no banco. | Consumidor de atualizacoes para o cliente. |

Cada mensagem enviada possui um payload estruturado com identificador da solicitacao, identificador do cliente, identificador do desenvolvedor quando existir, tipo do servico, status atual e data/hora do evento.

## Demonstracao da assincronicidade

A demonstracao foi feita executando o projeto com Docker Compose, que sobe PostgreSQL, RabbitMQ e API. Em seguida, foram feitas requisicoes REST para criar uma solicitacao e alterar seu status.

Ao criar uma solicitacao, o backend publicou `development_request.created` no RabbitMQ, e o consumidor registrou no log:

```text
[RabbitMQ] Nova solicitacao recebida para desenvolvedor: solicitacaoId=1, clienteId=1, tipoServico=aplicativo_mobile, status=aberta
```

Ao alterar o status da solicitacao para `aceita`, o backend publicou `development_request.status_changed`, e o consumidor registrou:

```text
[RabbitMQ] Atualizacao de status recebida para cliente: solicitacaoId=1, clienteId=1, desenvolvedorId=1, status=aceita
```

Esses logs demonstram que o consumidor recebeu mensagens por meio das filas do RabbitMQ, sem chamada REST direta entre produtor e consumidor.

## Desafios e decisoes

A principal decisao foi manter os eventos concentrados no ciclo de vida da solicitacao, porque esse e o fluxo central do dominio DevFacil. Eventos de cadastro de cliente ou desenvolvedor foram evitados por terem menor relevancia para a comunicacao cliente-prestador exigida no projeto.

Tambem foi mantido o registro no outbox local, criado na Sprint 1, como historico dos eventos. Na Sprint 2, alem desse registro, os eventos passaram a ser publicados de fato no RabbitMQ.

## Conclusao

A integracao com RabbitMQ atende ao objetivo da Sprint 2: o MOM esta configurado, o backend atua como produtor, existem consumidores escutando filas, e os eventos principais do dominio sao processados de forma assincrona. Essa base prepara o sistema para as proximas sprints, em que os aplicativos Flutter poderao refletir notificacoes e mudancas de estado a partir desses eventos.
