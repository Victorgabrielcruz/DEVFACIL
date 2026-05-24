# Schema PostgreSQL - DevFacil

As tabelas sao criadas automaticamente pelo Spring Data JPA. A nomenclatura abaixo representa o modelo relacional usado pela API.

## `clientes`

| Campo | Tipo | Descricao |
| --- | --- | --- |
| id | BIGSERIAL PK | Identificador do cliente. |
| nome | VARCHAR | Nome completo ou nome do responsavel. |
| telefone | VARCHAR | Telefone de contato. |
| email | VARCHAR UNIQUE | Email unico. |
| empresa | VARCHAR | Empresa, projeto ou organizacao do cliente. |
| created_at | TIMESTAMP | Data de criacao. |

## `desenvolvedores`

| Campo | Tipo | Descricao |
| --- | --- | --- |
| id | BIGSERIAL PK | Identificador do desenvolvedor. |
| nome | VARCHAR | Nome completo. |
| telefone | VARCHAR | Telefone de contato. |
| email | VARCHAR UNIQUE | Email unico. |
| stack | VARCHAR | Principais tecnologias, como Java, Flutter, React, Node.js ou Python. |
| senioridade | VARCHAR | Junior, pleno, senior ou especialista. |
| portfolio_url | VARCHAR | Link para GitHub, LinkedIn ou portfolio. |
| disponivel | BOOLEAN | Indica se o desenvolvedor esta disponivel. |
| created_at | TIMESTAMP | Data de criacao. |

## `solicitacoes`

| Campo | Tipo | Descricao |
| --- | --- | --- |
| id | BIGSERIAL PK | Identificador da solicitacao. |
| cliente_id | BIGINT FK | Cliente solicitante. |
| desenvolvedor_id | BIGINT FK NULL | Desenvolvedor responsavel, quando definido. |
| tipo_servico | VARCHAR | Tipo do servico: site, app mobile, API, sistema web, automacao, manutencao ou consultoria. |
| descricao | VARCHAR(1000) | Descricao geral da necessidade. |
| requisitos | VARCHAR | Requisitos principais do projeto. |
| prazo_desejado | VARCHAR | Prazo desejado pelo cliente. |
| orcamento_estimado | DOUBLE PRECISION | Orcamento estimado informado pelo cliente. |
| prioridade | VARCHAR | baixa, normal ou alta. |
| status | VARCHAR | aberta, aceita, recusada, em_andamento, concluida ou cancelada. |
| created_at | TIMESTAMP | Data de criacao. |
| updated_at | TIMESTAMP | Data da ultima alteracao. |

## `event_outbox`

| Campo | Tipo | Descricao |
| --- | --- | --- |
| id | BIGSERIAL PK | Identificador do evento. |
| event_name | VARCHAR | Nome do evento de dominio. |
| aggregate_type | VARCHAR | Tipo do agregado relacionado. |
| aggregate_id | BIGINT | ID do agregado relacionado. |
| payload | TEXT | JSON serializado com os dados do evento. |
| published | BOOLEAN | Na Sprint 1 fica `false`; na Sprint 2 indicara se o evento foi publicado no MOM. |
| created_at | TIMESTAMP | Data de criacao do evento. |
