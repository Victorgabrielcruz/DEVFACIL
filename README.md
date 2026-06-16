# DevFacil

DevFacil e uma plataforma para conectar clientes a desenvolvedores e engenheiros de software para contratacao de servicos como sites, aplicativos, APIs, sistemas web, manutencao e consultoria tecnica.

## Tecnologias usadas

- Backend: Java 17, Spring Boot 3.3, Spring Web, Spring Data JPA, Bean Validation, Spring AMQP.
- Banco de dados: PostgreSQL 16.
- Mensageria: RabbitMQ com exchange `devfacil.events`.
- Frontend: Flutter, Material 3, `ChangeNotifier`, pacote `http`.
- Infraestrutura local: Docker Compose ou Podman Compose.

## Estrutura

```text
backend/              API REST Spring Boot
collections/          Colecao Postman
docs/                 Proposta, arquitetura, schema e eventos
frontend/             App Flutter com telas integradas a API
compose.yaml          PostgreSQL, RabbitMQ e API
```

## Backend

Arquitetura seguida:

```text
controller -> service -> repository -> model
dto        -> contratos de entrada/saida da API
exception  -> respostas padronizadas de erro
event      -> eventos de dominio e publicacao RabbitMQ
```

Funcionalidades existentes e mantidas:

- Cadastro de clientes.
- Cadastro/listagem de desenvolvedores.
- Criacao, listagem, busca, status e exclusao de solicitacoes.
- Outbox de eventos.
- Publicacao/consumo demonstrativo no RabbitMQ.
- Auditoria de mensagens.

Funcionalidades completadas:

- CRUD completo de clientes: listar, buscar, atualizar e excluir.
- CRUD completo de desenvolvedores: buscar, atualizar e excluir.
- Atualizacao completa de solicitacoes via `PUT`.
- Autenticacao com cadastro separado para cliente e desenvolvedor.
- Cadastro de administrador protegido por chave `DEVFACIL_ADMIN_KEY`.
- Login com token assinado e senha armazenada com PBKDF2.
- Protecao das rotas da API por `Authorization: Bearer <token>`, exceto `/auth/**` e `/health`.
- Regras por perfil nas solicitacoes:
  - cliente cria, lista, edita e remove apenas suas proprias solicitacoes;
  - cliente pode criar solicitacao aberta para todos os devs ou direcionada a um dev disponivel;
  - desenvolvedor lista demandas abertas sem responsavel, demandas direcionadas a ele e demandas ja aceitas por ele;
  - desenvolvedor aceita uma demanda, conclui e pode anexar um arquivo `.zip`;
  - admin lista e gerencia todas as solicitacoes.
- Upload de ZIP da entrega em `/solicitacoes/{id}/zip`, com limite local de 50MB.
- Cancelamento de demanda com motivo opcional em `/solicitacoes/{id}/cancelar`.
- Evento de cancelamento `development_request.cancelled` registrado no outbox/auditoria e entregue na fila de status.
- Validacoes de demanda para reduzir erros de uso: descricao/requisitos minimos, orcamento nao negativo e prazo futuro no formato `yyyy-MM-dd`.
- CORS global para consumo pelo Flutter.
- Tratamento de conflito para remocao de registros vinculados.

O cadastro cria o perfil correto automaticamente: contas de cliente ficam vinculadas a `Cliente`; contas de desenvolvedor ficam vinculadas a `Desenvolvedor`. O app usa esse perfil para exibir o fluxo correspondente.

## Rotas da API

| Metodo | Rota | Descricao |
| --- | --- | --- |
| GET | `/health` | Verifica se a API esta ativa |
| POST | `/auth/cadastro/cliente` | Cria usuario e perfil de cliente |
| POST | `/auth/cadastro/desenvolvedor` | Cria usuario e perfil de desenvolvedor |
| POST | `/auth/cadastro/admin` | Cria usuario administrador usando `admin_key` |
| POST | `/auth/login` | Autentica e retorna token/perfil |
| GET | `/auth/me` | Retorna usuario autenticado |
| POST | `/clientes` | Cadastra cliente |
| GET | `/clientes` | Lista clientes |
| GET | `/clientes/{id}` | Busca cliente |
| PUT | `/clientes/{id}` | Atualiza cliente |
| DELETE | `/clientes/{id}` | Remove cliente |
| POST | `/desenvolvedores` | Cadastra desenvolvedor |
| GET | `/desenvolvedores` | Lista desenvolvedores |
| GET | `/desenvolvedores/{id}` | Busca desenvolvedor |
| PUT | `/desenvolvedores/{id}` | Atualiza desenvolvedor |
| DELETE | `/desenvolvedores/{id}` | Remove desenvolvedor |
| POST | `/solicitacoes` | Cria solicitacao de servico |
| GET | `/solicitacoes` | Lista solicitacoes; aceita `?status=aberta` |
| GET | `/solicitacoes/{id}` | Busca solicitacao |
| PUT | `/solicitacoes/{id}` | Atualiza dados da solicitacao |
| PATCH | `/solicitacoes/{id}/status` | Atualiza status e responsavel |
| PATCH | `/solicitacoes/{id}/cancelar` | Cancela demanda com motivo opcional |
| POST | `/solicitacoes/{id}/zip` | Anexa arquivo `.zip` da entrega |
| DELETE | `/solicitacoes/{id}` | Remove solicitacao |
| GET | `/eventos` | Lista eventos registrados no outbox |
| GET | `/auditoria-mensageria` | Lista logs RabbitMQ publicados/consumidos |

Valores aceitos:

- `prioridade`: `baixa`, `normal`, `alta`.
- `status`: `aberta`, `aceita`, `recusada`, `em_andamento`, `concluida`, `cancelada`.
- `perfil`: `CLIENTE`, `DESENVOLVEDOR`, `ADMIN`.

Exemplo de login:

```json
{
  "email": "cliente@email.com",
  "senha": "123456"
}
```

Exemplo de header nas rotas protegidas:

```text
Authorization: Bearer TOKEN_RETORNADO_NO_LOGIN
```

Exemplo de cadastro admin:

```json
{
  "nome": "Admin DevFacil",
  "email": "admin@devfacil.com",
  "senha": "123456",
  "admin_key": "devfacil-admin"
}
```

Em producao, altere a chave padrao via variavel de ambiente:

```text
DEVFACIL_ADMIN_KEY=sua-chave-forte
```

No `compose.yaml`, o ambiente local tambem cria um admin inicial automaticamente:

```text
email: admin@devfacil.local
senha: admin123
```

## Sprint 3

A Sprint 3 fechou o app mobile integrado ao backend REST, com autenticacao por perfil, fluxo de demandas abertas/direcionadas, cancelamento integrado a mensageria, upload de ZIP pelo dev responsavel e reorganizacao do Flutter em camadas mais proximas de Clean Architecture.

Resumo tecnico da Sprint 3:

- Cliente cria solicitacoes abertas para todos os devs ou direcionadas pela aba de desenvolvedores.
- Desenvolvedor ve abas de demandas disponiveis e aceitas, aceita, conclui, cancela e anexa ZIP.
- Admin acompanha perfis, demandas e auditoria de eventos/mensageria.
- Cadastro publico removeu admin e deixou empresa do cliente opcional.
- Formularios ganharam validacoes contra erros comuns.
- Inputs receberam mascaras para telefone, data e valor monetario.
- Login ficou mais responsivo: o app entra apos autenticar e carrega listas em segundo plano.
- O Flutter passou a usar contrato de repositorio no dominio e implementacao REST na camada de dados.

## Frontend Flutter

Arquitetura criada:

```text
frontend/lib/main.dart
frontend/lib/src/app.dart
frontend/lib/src/core/                         constantes compartilhadas
frontend/lib/src/domain/models/                entidades e mapeamento JSON
frontend/lib/src/domain/repositories/          contratos de acesso a dados
frontend/lib/src/data/services/                cliente HTTP da API REST
frontend/lib/src/data/repositories/            implementacao REST dos contratos
frontend/lib/src/presentation/controllers/     estado assincrono com ChangeNotifier
frontend/lib/src/presentation/screens/         navegacao, dashboards, listas e auditoria
frontend/lib/src/presentation/widgets/         formularios reutilizaveis
frontend/lib/src/presentation/theme/           tema Material 3
```

Separacao de responsabilidades:

- `presentation`: telas, widgets, navegacao, loading, erro e sucesso.
- `domain`: modelos do app e contrato `DevFacilRepository`.
- `data`: `ApiService` HTTP e `RestDevFacilRepository`.
- `core`: constantes reutilizaveis como tipos de servico, prioridades e status finais.

Telas implementadas:

- Login e cadastro com selecao de perfil cliente/desenvolvedor.
- Sessao persistida no mobile com armazenamento seguro.
- Painel do cliente com metricas e solicitacoes recentes.
- Aba de desenvolvedores disponiveis, com perfil do dev antes de criar solicitacao direcionada.
- Painel do desenvolvedor com abas de demandas disponiveis e aceitas.
- Modal de detalhes da demanda com cliente, dev, prazo, orcamento, requisitos, ZIP e motivo de cancelamento.
- Listagem de demandas com editar, aceitar, concluir e excluir.
- Cancelamento com confirmacao/motivo opcional.
- Botao aceitar fica desabilitado quando a demanda ja foi aceita, concluida ou cancelada.
- Anexo de arquivo `.zip` pelo desenvolvedor responsavel.
- Cadastro, edicao e exclusao de clientes.
- Cadastro, edicao e exclusao de desenvolvedores.
- Auditoria de infraestrutura/mensageria.
- Navegacao responsiva com `NavigationRail` no desktop e `NavigationBar` no mobile.
- Menus e acoes adaptados ao perfil autenticado.
- Estados de loading, erro, formularios obrigatorios e feedback visual.
- Mascaras de telefone `(DD) 99999-9999`, prazo `yyyy-MM-dd` e orcamento decimal com virgula.

## Criterios da Sprint 3

| Criterio | Evidencia no projeto |
| --- | --- |
| Funcionalidade do app | Fluxo cliente/dev/admin executavel no mobile, com demanda aberta, aceite, conclusao, cancelamento e ZIP. |
| Integracao REST | Flutter usa API real com token, JSON, CRUD, status, auditoria e upload multipart. |
| Atualizacao assincrona | `AppState` usa `Future`, `ChangeNotifier`, loading, erro e recarrega listas apos cada mutacao. |
| Clean Architecture | App separado em `core`, `domain`, `data` e `presentation`, com repositorio abstrato no dominio. |
| Qualidade da interface | Navegacao por perfil, abas, chips de status, formularios validados e feedback visual. |
| Documentacao | README e relatorio da Sprint 3 descrevem estrutura, rotas, execucao e testes. |
| Restricao do projeto | Nao foram criadas migrations; o banco segue com `ddl-auto: update`. |

## Como rodar

Suba backend, PostgreSQL e RabbitMQ:

```bash
docker compose up --build
```

Ou:

```bash
podman compose up --build
```

Servicos:

```text
API REST: http://127.0.0.1:8080
PostgreSQL: localhost:5432
RabbitMQ AMQP: localhost:5672
RabbitMQ Management: http://localhost:15672
```

Credenciais RabbitMQ:

```text
usuario: devfacil
senha: devfacil
```

Rodar o Flutter:

```bash
cd frontend
flutter pub get
flutter run -d chrome --web-port 5173
```

Rodar no Android:

```bash
cd frontend
flutter pub get
flutter run -d android --dart-define=API_BASE_URL=http://10.0.2.2:8080
```

Para gerar um APK:

```bash
cd frontend
flutter build apk --debug --dart-define=API_BASE_URL=http://10.0.2.2:8080
```

Validar o Flutter:

```bash
cd frontend
dart analyze
flutter test
```

O app consome por padrao:

```text
http://localhost:8080
```

Se estiver rodando em emulador Android, use `http://10.0.2.2:8080`. Se estiver rodando em celular fisico, use o IP da maquina na rede, por exemplo `http://192.168.0.10:8080`.

## Pontos de melhoria

- Trocar o token HMAC simples por JWT padronizado com Spring Security quando o projeto exigir hardening.
- Ampliar regras de autorizacao finas para CRUD de clientes/desenvolvedores, se esses dados deixarem de ser publicos para usuarios logados.
- Criar migrations com Flyway ou Liquibase em vez de `ddl-auto: update`.
- Adicionar testes automatizados para controllers/services do backend.
- Adicionar testes de widget e mocks HTTP no Flutter.
- Criar pagina de detalhes dedicada para solicitacao, cliente e desenvolvedor.
- Parametrizar a URL da API por `--dart-define`.

## Artefatos

- [Proposta de dominio](docs/proposta_sprint1.md)
- [Arquitetura](docs/arquitetura.md)
- [Schema PostgreSQL](docs/schema_postgresql.md)
- [Eventos implementados na Sprint 2](docs/eventos_previstos.md)
- [Relatorio de integracao da Sprint 2](docs/relatorio_integracao_sprint2.md)
- [Relatorio da Sprint 3](docs/relatorio_sprint3.md)
- [Colecao Postman](collections/devfacil.postman_collection.json)
