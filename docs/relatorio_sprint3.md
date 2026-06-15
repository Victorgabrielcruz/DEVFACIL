# Relatorio da Sprint 3

## Objetivo

A Sprint 3 consolidou o DevFacil como app mobile integrado ao backend REST. O foco foi fechar o fluxo autenticado por perfil, melhorar a experiencia de uso, conectar as demandas a mensageria e organizar o Flutter em camadas mais proximas de Clean Architecture.

## Entregas principais

- Autenticacao por perfil com cliente, desenvolvedor e admin.
- Cadastro publico apenas para cliente e desenvolvedor.
- Admin inicial criado por variaveis de ambiente no backend.
- Cliente cria demandas abertas para todos os devs ou direcionadas a um dev pelo painel de desenvolvedores.
- Desenvolvedor visualiza demandas disponiveis, aceita uma demanda, acompanha aceitas, conclui e anexa ZIP.
- Cliente e dev podem cancelar demandas dentro das regras de autorizacao.
- Cancelamento gera evento de dominio e auditoria de mensageria.
- Botao de aceitar fica desabilitado quando a demanda nao esta mais aberta.
- Validacoes no formulario de demanda para descricao, requisitos, prazo e orcamento.
- Mascaras nos inputs de telefone, data e valor monetario.
- Login otimizado para liberar a navegacao apos a autenticacao e carregar listas em segundo plano.
- Feedback visual de loading, erro, sucesso e status.
- Camada Flutter reorganizada em `core`, `domain`, `data` e `presentation`.

## Arquitetura Flutter adotada

```text
frontend/lib/src/
  core/                         constantes e regras compartilhadas de UI
  domain/
    models/                     entidades usadas pelo app
    repositories/               contratos de acesso a dados
  data/
    services/                   cliente HTTP e serializacao REST
    repositories/               implementacao REST dos contratos do dominio
  presentation/
    controllers/                estado assincrono com ChangeNotifier
    screens/                    telas e navegacao por perfil
    widgets/                    formularios e componentes reutilizaveis
    theme/                      tema Material 3
```

Responsabilidades:

- `presentation` nao conhece detalhes HTTP.
- `domain` define modelos e o contrato `DevFacilRepository`.
- `data` implementa `RestDevFacilRepository` usando `ApiService`.
- `core` centraliza constantes como tipos de servico, prioridades e status finais.

## Fluxos validados

1. Cliente realiza cadastro ou login.
2. Cliente acessa o painel, cria demanda aberta ou escolhe um dev disponivel.
3. Dev realiza login, ve demandas disponiveis e aceita uma demanda.
4. Demanda aceita sai do fluxo aberto e entra na aba de aceitas do dev.
5. Dev conclui a demanda e pode anexar um arquivo ZIP.
6. Cliente/dev/admin podem cancelar conforme permissao.
7. Admin acompanha solicitacoes, perfis e auditoria de mensageria.

## Rotas REST usadas pelo app

- `POST /auth/login`
- `POST /auth/cadastro/cliente`
- `POST /auth/cadastro/desenvolvedor`
- `GET /auth/me`
- `GET /clientes`
- `POST /clientes`
- `PUT /clientes/{id}`
- `DELETE /clientes/{id}`
- `GET /desenvolvedores`
- `POST /desenvolvedores`
- `PUT /desenvolvedores/{id}`
- `DELETE /desenvolvedores/{id}`
- `GET /solicitacoes`
- `POST /solicitacoes`
- `PUT /solicitacoes/{id}`
- `PATCH /solicitacoes/{id}/status`
- `PATCH /solicitacoes/{id}/cancelar`
- `POST /solicitacoes/{id}/zip`
- `DELETE /solicitacoes/{id}`
- `GET /auditoria-mensageria`

## Como testar no Android

Subir backend:

```powershell
cd "C:\Users\vgppl\Documents\New project 3"
docker compose up --build
```

Rodar no emulador:

```powershell
cd "C:\Users\vgppl\Documents\New project 3\frontend"
C:\src\flutter\bin\flutter.bat run -d emulator-5554 --dart-define=API_BASE_URL=http://10.0.2.2:8080
```

Credenciais locais ja usadas durante os testes:

```text
Admin: admin@devfacil.local / admin123
Dev: dev@teste.com / 123456
Cliente: cliente@teste.com / 123456
```

Se o banco for recriado, os usuarios de cliente/dev precisam ser cadastrados novamente pelo app.

## Validacoes tecnicas executadas

```powershell
cd "C:\Users\vgppl\Documents\New project 3\frontend"
C:\src\flutter\bin\cache\dart-sdk\bin\dart.exe analyze
C:\src\flutter\bin\flutter.bat test test\widget_test.dart
C:\src\flutter\bin\flutter.bat build apk --debug --dart-define=API_BASE_URL=http://10.0.2.2:8080
```

Resultado:

- `dart analyze`: sem problemas encontrados.
- `flutter test test\widget_test.dart`: teste automatizado passou.
- `flutter build apk --debug`: APK gerado em `frontend/build/app/outputs/flutter-apk/app-debug.apk`.

Observacao: o build exibiu um aviso futuro do plugin `file_picker` sobre Kotlin Gradle Plugin. O aviso nao bloqueia a build atual.

## Criterios de avaliacao

| Criterio | Evidencia |
| --- | --- |
| Funcionalidade do app | Fluxo completo cliente, dev e admin executavel no mobile. |
| Integracao REST | Flutter consome backend real com token, CRUD, status, cancelamento e ZIP. |
| Estado assincrono | `AppState` centraliza loading, erro, sessao persistida e reload de listas apos mutacoes. |
| Organizacao Flutter | Separacao em `core`, `domain`, `data` e `presentation`, com contrato de repositorio. |
| Interface | Navegacao por perfil, abas para demandas, formulario validado, chips de status e feedback visual. |

## Pontos de melhoria futuros

- Adicionar testes de widget com repositorio fake.
- Criar tela dedicada de detalhes da demanda alem do modal.
- Adicionar notificacoes em tempo real via WebSocket ou polling.
- Trocar autenticacao HMAC simples por Spring Security/JWT.
- Adicionar Flyway/Liquibase quando migrations forem permitidas.
