# Proposta de Dominio - DevFacil

## Descricao

DevFacil e uma plataforma para conectar clientes que precisam desenvolver produtos digitais a desenvolvedores e engenheiros de software disponiveis para prestar servicos. O cliente pode solicitar o desenvolvimento de um site, aplicativo, API, sistema web, automacao, manutencao de sistema legado ou consultoria tecnica. O desenvolvedor cadastra seu perfil, stack, senioridade e portfolio, recebe demandas compativeis e atualiza o status do atendimento.

## Justificativa

Clientes que precisam de software normalmente dependem de indicacoes, mensagens informais ou plataformas genericas de freelancer. Isso dificulta comparar perfis tecnicos, organizar requisitos, acompanhar o andamento e manter historico das solicitacoes. O DevFacil centraliza esse fluxo em um sistema distribuido simples, com backend REST, banco relacional, aplicativos moveis e eventos assincronos previstos para integracao via MOM.

## Perfis de usuario

| Perfil | Papel no sistema |
| --- | --- |
| Cliente | Cadastra dados de contato, cria solicitacoes de desenvolvimento e acompanha o status do projeto. |
| Desenvolvedor | Cadastra stack, senioridade e portfolio, consulta demandas, aceita ou recusa solicitacoes e atualiza o andamento do servico. |

## Funcionalidades principais

- Cadastro de clientes.
- Cadastro e listagem de desenvolvedores.
- Criacao de solicitacoes de servicos de desenvolvimento.
- Consulta de solicitacoes por lista ou por identificador.
- Atualizacao de status da solicitacao.
- Registro dos eventos previstos no outbox para orientar a Sprint 2.

## Escopo da Sprint 1

Nesta sprint, o foco esta no backend REST, na persistencia relacional, na arquitetura e na colecao de testes. Como o uso de Spring Boot foi autorizado pelo professor, o backend foi implementado em Java/Spring Boot e a infraestrutura foi preparada para Podman com PostgreSQL. A MOM aparece no diagrama e na documentacao como componente planejado para a Sprint 2.
