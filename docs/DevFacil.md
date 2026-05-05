# DevFácil – Documentação Técnica

## 1. Visão Geral

**DevFácil** é uma plataforma que conecta clientes a prestadores de serviços de manutenção e reparos domésticos (ex.: encanadores, eletricistas, pintores). O sistema é composto por aplicativos móveis (cliente e prestador), um backend REST e um banco de dados relacional, comunicando-se através de uma Message-Oriented Middleware (MOM) para eventos assíncronos.

---

## 2. Arquitetura do Sistema

```
┌──────────────────────────────────────────────────────────────┐
│                        Clientes                              │
│   App Mobile (Cliente)          App Mobile (Prestador)       │
│         │  REST/HTTPS                   │  REST/HTTPS        │
└─────────┼───────────────────────────────┼────────────────────┘
          │                               │
          ▼                               ▼
   ┌─────────────────────────────────────────────┐
   │                Backend REST                  │
   │              (Node.js / Express)             │
   │                                              │
   │  ┌──────────────┐   ┌─────────────────────┐ │
   │  │  Controllers │   │      Services        │ │
   │  └──────┬───────┘   └──────────┬──────────┘ │
   │         └──────────────────────┘            │
   │                    │                         │
   │          ┌─────────┴────────┐                │
   │          │    Repository    │                │
   │          └─────────┬────────┘                │
   └────────────────────┼────────────────────────┘
                        │
          ┌─────────────┴──────────────┐
          │                            │
   ┌──────▼──────┐             ┌───────▼──────┐
   │  PostgreSQL  │             │  MOM (MQTT/  │
   │  (Dados      │             │  RabbitMQ)   │
   │  persistentes│             │  Notificações│
   └─────────────┘             └──────────────┘
```

### Protocolos de Comunicação

| Camada                  | Protocolo        | Descrição                                      |
|-------------------------|------------------|------------------------------------------------|
| App ↔ Backend           | HTTPS / REST     | Comunicação síncrona via JSON                  |
| Backend ↔ MOM           | AMQP / MQTT      | Mensagens assíncronas (notificações, eventos)  |
| Backend ↔ Banco         | TCP (driver ORM) | Queries SQL via ORM (ex.: Prisma / Sequelize)  |

---

## 3. Tecnologias Utilizadas

### Backend
- **Linguagem:** Node.js (TypeScript)
- **Framework:** Express.js
- **ORM:** Prisma
- **Banco de dados:** PostgreSQL (produção) / SQLite (desenvolvimento)
- **MOM:** RabbitMQ ou MQTT Broker
- **Autenticação:** JWT

### Mobile
- **Framework:** React Native (Expo)
- **Linguagem:** TypeScript
- **Gerenciador de Estado:** Context API / Zustand

### Infraestrutura
- **Containerização:** Docker + Docker Compose
- **CI/CD:** GitHub Actions

---

## 4. Estrutura de Pastas

```
DEVFACIL/
├── docs/
│   ├── DevFacil.md          ← Este arquivo
│   └── sprints/
│       └── sprint0.md
└── code/
    ├── backend/             ← API REST (Node.js / Express)
    └── mobile/              ← Aplicativo móvel (React Native)
```

---

## 5. Perfis de Usuário

### 5.1 Cliente
Pessoa que busca contratar serviços de manutenção doméstica. Pode:
- Criar solicitações de serviço.
- Consultar prestadores disponíveis.
- Acompanhar o status da solicitação em tempo real.
- Avaliar o prestador após a conclusão do serviço.

### 5.2 Prestador de Serviço
Profissional autônomo que oferta serviços. Pode:
- Visualizar solicitações disponíveis na sua área de atuação.
- Aceitar ou recusar solicitações.
- Atualizar o status do serviço (em andamento, concluído).
- Gerenciar seu perfil e disponibilidade.

---

## 6. Principais Funcionalidades

| Funcionalidade               | Cliente | Prestador |
|------------------------------|:-------:|:---------:|
| Cadastro / Login             | ✔       | ✔         |
| Criar solicitação de serviço | ✔       |           |
| Listar solicitações          | ✔       | ✔         |
| Aceitar / Recusar solicitação|         | ✔         |
| Atualizar status do serviço  |         | ✔         |
| Avaliar prestador            | ✔       |           |
| Receber notificações (push)  | ✔       | ✔         |

---

## 7. Endpoints da API (Resumo)

| Método | Rota                          | Descrição                         |
|--------|-------------------------------|-----------------------------------|
| POST   | `/api/solicitacoes`           | Criar nova solicitação            |
| GET    | `/api/solicitacoes`           | Listar todas as solicitações      |
| GET    | `/api/solicitacoes/:id`       | Consultar solicitação por ID      |
| PATCH  | `/api/solicitacoes/:id/status`| Atualizar status da solicitação   |

> Detalhes completos em [`sprint0.md`](./sprints/sprint0.md).

---

## 8. Schema do Banco de Dados (Resumo)

```sql
-- Usuários (clientes e prestadores)
CREATE TABLE usuarios (
  id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  nome       VARCHAR(100) NOT NULL,
  email      VARCHAR(150) UNIQUE NOT NULL,
  senha_hash TEXT         NOT NULL,
  perfil     VARCHAR(20)  NOT NULL CHECK (perfil IN ('cliente','prestador')),
  criado_em  TIMESTAMP    DEFAULT NOW()
);

-- Solicitações de serviço
CREATE TABLE solicitacoes (
  id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  cliente_id    UUID        REFERENCES usuarios(id),
  prestador_id  UUID        REFERENCES usuarios(id),
  descricao     TEXT        NOT NULL,
  status        VARCHAR(30) NOT NULL DEFAULT 'aguardando',
  criado_em     TIMESTAMP   DEFAULT NOW(),
  atualizado_em TIMESTAMP   DEFAULT NOW()
);
```

> Schema completo em [`sprint0.md`](./sprints/sprint0.md).

---

## 9. Como Executar (Desenvolvimento)

```bash
# Clonar o repositório
git clone https://github.com/Victorgabrielcruz/DEVFACIL.git
cd DEVFACIL/code/backend

# Instalar dependências
npm install

# Configurar variáveis de ambiente
cp .env.example .env

# Executar migrações
npx prisma migrate dev

# Iniciar servidor
npm run dev
```

---

## 10. Sprints

| Sprint   | Objetivo                                                        | Documento                          |
|----------|-----------------------------------------------------------------|------------------------------------|
| Sprint 0 | Proposta, arquitetura, backend inicial e banco de dados         | [sprint0.md](./sprints/sprint0.md) |
