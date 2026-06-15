import 'package:file_picker/file_picker.dart';
import 'package:flutter/material.dart';

import '../../core/devfacil_constants.dart';
import '../../domain/models/devfacil_models.dart';
import '../controllers/app_state.dart';
import '../theme/app_theme.dart';
import '../widgets/form_dialogs.dart';

class HomeScreen extends StatefulWidget {
  const HomeScreen({super.key, required this.state});

  final AppState state;

  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  var selectedIndex = 0;

  @override
  Widget build(BuildContext context) {
    return AnimatedBuilder(
      animation: widget.state,
      builder: (context, _) {
        final session = widget.state.session!;
        final pages = session.isAdmin
            ? [
                _DashboardPage(state: widget.state, isDeveloper: false),
                _RequestsPage(state: widget.state),
                _DashboardPage(state: widget.state, isDeveloper: true),
                _AvailableDevelopersPage(state: widget.state),
                _PeoplePage(state: widget.state),
                _AuditPage(state: widget.state),
              ]
            : session.isCliente
                ? [
                    _DashboardPage(state: widget.state, isDeveloper: false),
                    _RequestsPage(state: widget.state),
                    _AvailableDevelopersPage(state: widget.state),
                    _ProfilePage(state: widget.state),
                  ]
                : [
                    _DashboardPage(
                      state: widget.state,
                      isDeveloper: true,
                    ),
                    _DeveloperDemandsPage(state: widget.state),
                    _ProfilePage(state: widget.state),
                  ];
        final destinations = session.isAdmin
            ? const [
                _NavItem(Icons.business_center_outlined, 'Cliente'),
                _NavItem(Icons.assignment_outlined, 'Demandas'),
                _NavItem(Icons.code_outlined, 'Dev'),
                _NavItem(Icons.manage_search_outlined, 'Disponiveis'),
                _NavItem(Icons.people_outline, 'Perfis'),
                _NavItem(Icons.query_stats_outlined, 'Auditoria'),
              ]
            : session.isCliente
                ? const [
                    _NavItem(Icons.business_center_outlined, 'Cliente'),
                    _NavItem(Icons.assignment_outlined, 'Demandas'),
                    _NavItem(Icons.manage_search_outlined, 'Devs'),
                    _NavItem(Icons.person_outline, 'Perfil'),
                  ]
                : const [
                    _NavItem(Icons.code_outlined, 'Dev'),
                    _NavItem(Icons.assignment_outlined, 'Demandas'),
                    _NavItem(Icons.person_outline, 'Perfil'),
                  ];

        if (selectedIndex >= pages.length) {
          selectedIndex = 0;
        }

        return Scaffold(
          appBar: AppBar(
            title: const Text('DevFacil',
                style: TextStyle(
                    fontWeight: FontWeight.w800, color: AppTheme.primary)),
            actions: [
              Center(
                child: Padding(
                  padding: const EdgeInsets.only(right: 8),
                  child: Text(session.perfil),
                ),
              ),
              IconButton(
                tooltip: 'Atualizar',
                onPressed: widget.state.loading ? null : widget.state.loadAll,
                icon: const Icon(Icons.refresh),
              ),
              IconButton(
                tooltip: 'Sair',
                onPressed: () => widget.state.logout(),
                icon: const Icon(Icons.logout),
              ),
            ],
          ),
          body: Row(
            children: [
              if (MediaQuery.sizeOf(context).width >= 860)
                NavigationRail(
                  selectedIndex: selectedIndex,
                  onDestinationSelected: (value) =>
                      setState(() => selectedIndex = value),
                  labelType: NavigationRailLabelType.all,
                  destinations: destinations
                      .map((item) => NavigationRailDestination(
                            icon: Icon(item.icon),
                            label: Text(item.label),
                          ))
                      .toList(),
                ),
              Expanded(
                child: Stack(
                  children: [
                    pages[selectedIndex],
                    if (widget.state.loading)
                      const LinearProgressIndicator(minHeight: 3),
                  ],
                ),
              ),
            ],
          ),
          bottomNavigationBar: MediaQuery.sizeOf(context).width < 860
              ? NavigationBar(
                  selectedIndex: selectedIndex,
                  onDestinationSelected: (value) =>
                      setState(() => selectedIndex = value),
                  destinations: destinations
                      .map((item) => NavigationDestination(
                            icon: Icon(item.icon),
                            label: item.label,
                          ))
                      .toList(),
                )
              : null,
          floatingActionButton: selectedIndex == 1 &&
                  (session.isCliente || session.isAdmin)
              ? FloatingActionButton.extended(
                  onPressed: () => showSolicitacaoDialog(context, widget.state),
                  icon: const Icon(Icons.add),
                  label: const Text('Nova demanda'),
                )
              : null,
        );
      },
    );
  }
}

class _NavItem {
  const _NavItem(this.icon, this.label);

  final IconData icon;
  final String label;
}

class _PageShell extends StatelessWidget {
  const _PageShell(
      {required this.title, required this.subtitle, required this.children});

  final String title;
  final String subtitle;
  final List<Widget> children;

  @override
  Widget build(BuildContext context) {
    return ListView(
      padding: const EdgeInsets.fromLTRB(20, 16, 20, 96),
      children: [
        Text(title,
            style: Theme.of(context)
                .textTheme
                .headlineMedium
                ?.copyWith(fontWeight: FontWeight.w800)),
        const SizedBox(height: 4),
        Text(subtitle,
            style: Theme.of(context)
                .textTheme
                .bodyLarge
                ?.copyWith(color: Colors.black54)),
        const SizedBox(height: 24),
        ...children,
      ],
    );
  }
}

class _DashboardPage extends StatelessWidget {
  const _DashboardPage({required this.state, required this.isDeveloper});

  final AppState state;
  final bool isDeveloper;

  @override
  Widget build(BuildContext context) {
    final abertas =
        state.solicitacoes.where((item) => item.status == 'aberta').length;
    final andamento = state.solicitacoes
        .where((item) => item.status == 'em_andamento')
        .length;
    final concluidas =
        state.solicitacoes.where((item) => item.status == 'concluida').length;
    final title = isDeveloper ? 'Painel do desenvolvedor' : 'Painel do cliente';
    final subtitle = isDeveloper
        ? 'Acompanhe demandas abertas e projetos aceitos.'
        : 'Gerencie pedidos, prazos e status dos seus projetos.';

    return _PageShell(
      title: title,
      subtitle: subtitle,
      children: [
        Wrap(
          spacing: 12,
          runSpacing: 12,
          children: [
            _MetricCard(
                label: 'Demandas abertas',
                value: '$abertas',
                icon: Icons.inbox_outlined),
            _MetricCard(
                label: 'Em andamento',
                value: '$andamento',
                icon: Icons.pending_actions_outlined),
            _MetricCard(
                label: 'Concluidas',
                value: '$concluidas',
                icon: Icons.verified_outlined),
          ],
        ),
        const SizedBox(height: 24),
        if (!isDeveloper || state.session?.isAdmin == true) ...[
          FilledButton.icon(
            onPressed: () => showSolicitacaoDialog(context, state),
            icon: const Icon(Icons.add),
            label: const Text('Criar solicitacao de servico'),
          ),
          const SizedBox(height: 24),
        ],
        Text('Atualizacoes recentes',
            style: Theme.of(context)
                .textTheme
                .titleLarge
                ?.copyWith(fontWeight: FontWeight.w700)),
        const SizedBox(height: 12),
        ...state.solicitacoes
            .take(4)
            .map((item) => _SolicitacaoTile(state: state, item: item)),
        if (state.error != null) _ErrorBanner(message: state.error!),
      ],
    );
  }
}

class _RequestsPage extends StatelessWidget {
  const _RequestsPage({required this.state});

  final AppState state;

  @override
  Widget build(BuildContext context) {
    return _PageShell(
      title: 'Demandas disponiveis',
      subtitle:
          'Liste, edite, aceite, conclua ou remova solicitacoes reais da API.',
      children: [
        if (state.solicitacoes.isEmpty)
          const _EmptyState(text: 'Nenhuma solicitacao cadastrada.'),
        ...state.solicitacoes.map((item) =>
            _SolicitacaoTile(state: state, item: item, expanded: true)),
        if (state.error != null) _ErrorBanner(message: state.error!),
      ],
    );
  }
}

class _DeveloperDemandsPage extends StatelessWidget {
  const _DeveloperDemandsPage({required this.state});

  final AppState state;

  @override
  Widget build(BuildContext context) {
    final devId = state.session?.desenvolvedorId;
    final disponiveis = state.solicitacoes
        .where((item) =>
            item.status == 'aberta' &&
            (item.desenvolvedorId == null || item.desenvolvedorId == devId))
        .toList();
    final aceitas = state.solicitacoes
        .where(
            (item) => item.desenvolvedorId == devId && item.status != 'aberta')
        .toList();

    return DefaultTabController(
      length: 2,
      child: Column(
        children: [
          const TabBar(
            tabs: [
              Tab(icon: Icon(Icons.public), text: 'Disponiveis'),
              Tab(icon: Icon(Icons.task_alt), text: 'Aceitas'),
            ],
          ),
          Expanded(
            child: TabBarView(
              children: [
                _DemandList(
                  state: state,
                  items: disponiveis,
                  empty: 'Nenhuma demanda aberta no momento.',
                ),
                _DemandList(
                  state: state,
                  items: aceitas,
                  empty: 'Nenhuma demanda aceita por voce.',
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}

class _DemandList extends StatelessWidget {
  const _DemandList({
    required this.state,
    required this.items,
    required this.empty,
  });

  final AppState state;
  final List<Solicitacao> items;
  final String empty;

  @override
  Widget build(BuildContext context) {
    return ListView(
      padding: const EdgeInsets.fromLTRB(20, 16, 20, 96),
      children: [
        if (items.isEmpty) _EmptyState(text: empty),
        ...items.map(
          (item) => _SolicitacaoTile(
            state: state,
            item: item,
            expanded: true,
          ),
        ),
        if (state.error != null) _ErrorBanner(message: state.error!),
      ],
    );
  }
}

class _AvailableDevelopersPage extends StatelessWidget {
  const _AvailableDevelopersPage({required this.state});

  final AppState state;

  @override
  Widget build(BuildContext context) {
    final devs =
        state.desenvolvedores.where((item) => item.disponivel).toList();
    return _PageShell(
      title: 'Desenvolvedores disponiveis',
      subtitle: 'Toque em um perfil para ver detalhes e criar uma solicitacao.',
      children: [
        if (devs.isEmpty)
          const _EmptyState(text: 'Nenhum desenvolvedor disponivel.'),
        ...devs.map(
          (dev) => Card(
            margin: const EdgeInsets.only(bottom: 12),
            child: ListTile(
              onTap: () => _showDeveloperProfile(context, state, dev),
              leading: const CircleAvatar(child: Icon(Icons.code)),
              title: Text(dev.nome),
              subtitle: Text('${dev.stack} • ${dev.senioridade}\n${dev.email}'),
              isThreeLine: true,
              trailing: const Icon(Icons.chevron_right),
            ),
          ),
        ),
      ],
    );
  }
}

class _ProfileLine extends StatelessWidget {
  const _ProfileLine({required this.icon, required this.text});

  final IconData icon;
  final String text;

  @override
  Widget build(BuildContext context) {
    return Row(
      children: [
        Icon(icon, size: 18, color: Colors.black54),
        const SizedBox(width: 8),
        Expanded(child: Text(text)),
      ],
    );
  }
}

Future<void> _showDeveloperProfile(
  BuildContext context,
  AppState state,
  Desenvolvedor dev,
) {
  return showDialog<void>(
    context: context,
    builder: (context) => AlertDialog(
      title: Text(dev.nome),
      content: SizedBox(
        width: 420,
        child: Column(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            _ProfileLine(
                icon: Icons.layers_outlined, text: 'Stack: ${dev.stack}'),
            const SizedBox(height: 10),
            _ProfileLine(
              icon: Icons.workspace_premium_outlined,
              text: 'Senioridade: ${dev.senioridade}',
            ),
            const SizedBox(height: 10),
            _ProfileLine(icon: Icons.email_outlined, text: dev.email),
            if (dev.portfolioUrl != null && dev.portfolioUrl!.isNotEmpty) ...[
              const SizedBox(height: 10),
              _ProfileLine(icon: Icons.link_outlined, text: dev.portfolioUrl!),
            ],
            const SizedBox(height: 18),
            Text(
              'Crie uma solicitacao direcionada para este desenvolvedor. Ela aparecera para ele nas demandas disponiveis.',
              style: Theme.of(context)
                  .textTheme
                  .bodyMedium
                  ?.copyWith(color: Colors.black54),
            ),
          ],
        ),
      ),
      actions: [
        TextButton(
          onPressed: () => Navigator.pop(context),
          child: const Text('Fechar'),
        ),
        FilledButton.icon(
          onPressed: () {
            Navigator.pop(context);
            showSolicitacaoDialog(context, state, desenvolvedor: dev);
          },
          icon: const Icon(Icons.add),
          label: const Text('Criar solicitacao'),
        ),
      ],
    ),
  );
}

class _PeoplePage extends StatelessWidget {
  const _PeoplePage({required this.state});

  final AppState state;

  @override
  Widget build(BuildContext context) {
    return _PageShell(
      title: 'Perfis e cadastros',
      subtitle: 'CRUD completo de clientes e desenvolvedores.',
      children: [
        Wrap(
          spacing: 12,
          runSpacing: 12,
          children: [
            OutlinedButton.icon(
              onPressed: () => showClienteDialog(context, state),
              icon: const Icon(Icons.person_add_alt),
              label: const Text('Novo cliente'),
            ),
            OutlinedButton.icon(
              onPressed: () => showDesenvolvedorDialog(context, state),
              icon: const Icon(Icons.person_add_alt_1),
              label: const Text('Novo desenvolvedor'),
            ),
          ],
        ),
        const SizedBox(height: 20),
        Text('Clientes',
            style: Theme.of(context)
                .textTheme
                .titleLarge
                ?.copyWith(fontWeight: FontWeight.w700)),
        const SizedBox(height: 8),
        ...state.clientes.map((item) => _PersonTile(
              title: item.nome,
              subtitle: '${item.empresa} • ${item.email}',
              onEdit: () => showClienteDialog(context, state, cliente: item),
              onDelete:
                  item.id == null ? null : () => state.deleteCliente(item.id!),
            )),
        const SizedBox(height: 20),
        Text('Desenvolvedores',
            style: Theme.of(context)
                .textTheme
                .titleLarge
                ?.copyWith(fontWeight: FontWeight.w700)),
        const SizedBox(height: 8),
        ...state.desenvolvedores.map((item) => _PersonTile(
              title: item.nome,
              subtitle: '${item.stack} • ${item.senioridade} • ${item.email}',
              trailing: item.disponivel ? 'Disponivel' : 'Indisponivel',
              onEdit: () =>
                  showDesenvolvedorDialog(context, state, desenvolvedor: item),
              onDelete: item.id == null
                  ? null
                  : () => state.deleteDesenvolvedor(item.id!),
            )),
        if (state.error != null) _ErrorBanner(message: state.error!),
      ],
    );
  }
}

class _ProfilePage extends StatelessWidget {
  const _ProfilePage({required this.state});

  final AppState state;

  @override
  Widget build(BuildContext context) {
    final session = state.session!;
    final cliente = state.clientes
        .where((item) => item.id == session.clienteId)
        .firstOrNull;
    final desenvolvedor = state.desenvolvedores
        .where((item) => item.id == session.desenvolvedorId)
        .firstOrNull;

    return _PageShell(
      title: 'Meu perfil',
      subtitle: session.isDesenvolvedor
          ? 'Cadastro de desenvolvedor vinculado a sua conta.'
          : 'Cadastro de cliente vinculado a sua conta.',
      children: [
        Card(
          child: Padding(
            padding: const EdgeInsets.all(20),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(session.nome,
                    style: Theme.of(context)
                        .textTheme
                        .headlineSmall
                        ?.copyWith(fontWeight: FontWeight.w800)),
                const SizedBox(height: 6),
                Text(session.email),
                const SizedBox(height: 12),
                Chip(label: Text(session.perfil)),
                const SizedBox(height: 16),
                if (cliente != null) ...[
                  Text('Empresa: ${cliente.empresa}'),
                  Text('Telefone: ${cliente.telefone}'),
                ],
                if (desenvolvedor != null) ...[
                  Text('Stack: ${desenvolvedor.stack}'),
                  Text('Senioridade: ${desenvolvedor.senioridade}'),
                  Text(
                      desenvolvedor.disponivel ? 'Disponivel' : 'Indisponivel'),
                ],
              ],
            ),
          ),
        ),
      ],
    );
  }
}

class _AuditPage extends StatelessWidget {
  const _AuditPage({required this.state});

  final AppState state;

  @override
  Widget build(BuildContext context) {
    return _PageShell(
      title: 'Auditoria de infraestrutura',
      subtitle:
          'Logs de publicacao e consumo RabbitMQ registrados pelo backend.',
      children: [
        Wrap(
          spacing: 12,
          runSpacing: 12,
          children: [
            _MetricCard(
                label: 'Total',
                value: '${state.auditoria.length}',
                icon: Icons.all_inclusive),
            _MetricCard(
                label: 'Publicadas',
                value:
                    '${state.auditoria.where((e) => e.direction == 'PUBLISHED').length}',
                icon: Icons.upload),
            _MetricCard(
                label: 'Consumidas',
                value:
                    '${state.auditoria.where((e) => e.direction == 'CONSUMED').length}',
                icon: Icons.download),
          ],
        ),
        const SizedBox(height: 16),
        ...state.auditoria.map((log) => Card(
              child: ListTile(
                leading: Icon(
                    log.direction == 'PUBLISHED'
                        ? Icons.north_east
                        : Icons.south_west,
                    color: AppTheme.primary),
                title: Text(log.eventName),
                subtitle: Text(
                    '${log.direction} • ${log.routingKey}\n${log.payload}',
                    maxLines: 4,
                    overflow: TextOverflow.ellipsis),
                isThreeLine: true,
              ),
            )),
      ],
    );
  }
}

class _SolicitacaoTile extends StatelessWidget {
  const _SolicitacaoTile(
      {required this.state, required this.item, this.expanded = false});

  final AppState state;
  final Solicitacao item;
  final bool expanded;

  @override
  Widget build(BuildContext context) {
    final cliente = state.clientes
            .where((value) => value.id == item.clienteId)
            .firstOrNull
            ?.nome ??
        'Cliente ${item.clienteId}';
    return Card(
      margin: const EdgeInsets.only(bottom: 12),
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              children: [
                Expanded(
                    child: Text(item.tipoServico,
                        style: Theme.of(context)
                            .textTheme
                            .titleMedium
                            ?.copyWith(fontWeight: FontWeight.w700))),
                _StatusChip(status: item.status),
              ],
            ),
            const SizedBox(height: 8),
            Text(cliente, style: const TextStyle(color: Colors.black54)),
            const SizedBox(height: 8),
            Text(item.descricao,
                maxLines: expanded ? 6 : 2, overflow: TextOverflow.ellipsis),
            if (item.zipNome != null) ...[
              const SizedBox(height: 8),
              Row(
                children: [
                  const Icon(Icons.folder_zip_outlined, size: 18),
                  const SizedBox(width: 6),
                  Expanded(child: Text('ZIP: ${item.zipNome}')),
                ],
              ),
            ],
            const SizedBox(height: 12),
            Wrap(
              spacing: 8,
              runSpacing: 8,
              children: [
                ActionChip(
                  label: const Text('Detalhes'),
                  avatar: const Icon(Icons.info_outline, size: 18),
                  onPressed: () => _showDemandDetails(context, state, item),
                ),
                if (state.session?.isCliente == true ||
                    state.session?.isAdmin == true)
                  ActionChip(
                      label: const Text('Editar'),
                      avatar: const Icon(Icons.edit, size: 18),
                      onPressed: _isFinalized(item)
                          ? null
                          : () => showSolicitacaoDialog(context, state,
                              solicitacao: item)),
                if (state.session?.isDesenvolvedor == true ||
                    state.session?.isAdmin == true)
                  ActionChip(
                      label: const Text('Aceitar'),
                      avatar: const Icon(Icons.play_arrow, size: 18),
                      onPressed: item.status != 'aberta' ||
                              (item.desenvolvedorId != null &&
                                  state.session?.isAdmin != true &&
                                  item.desenvolvedorId !=
                                      state.session?.desenvolvedorId)
                          ? null
                          : () => state.updateStatus(item, 'em_andamento',
                              desenvolvedorId: state.session?.desenvolvedorId ??
                                  state.desenvolvedores.firstOrNull?.id)),
                if (state.session?.isDesenvolvedor == true ||
                    state.session?.isAdmin == true)
                  ActionChip(
                      label: const Text('Concluir'),
                      avatar: const Icon(Icons.check, size: 18),
                      onPressed: item.status != 'em_andamento'
                          ? null
                          : () => state.updateStatus(item, 'concluida')),
                if ((state.session?.isDesenvolvedor == true ||
                        state.session?.isAdmin == true) &&
                    item.desenvolvedorId != null)
                  ActionChip(
                    label: Text(
                        item.zipNome == null ? 'Anexar ZIP' : 'Trocar ZIP'),
                    avatar: const Icon(Icons.attach_file, size: 18),
                    onPressed: _isFinalized(item)
                        ? null
                        : () => _pickAndUploadZip(context, state, item),
                  ),
                if (!_isFinalized(item) &&
                    (state.session?.isCliente == true ||
                        (state.session?.isDesenvolvedor == true &&
                            item.desenvolvedorId ==
                                state.session?.desenvolvedorId) ||
                        state.session?.isAdmin == true))
                  ActionChip(
                    label: const Text('Cancelar'),
                    avatar: const Icon(Icons.cancel_outlined, size: 18),
                    onPressed: () =>
                        _showCancelDemandDialog(context, state, item),
                  ),
                if (state.session?.isCliente == true ||
                    state.session?.isAdmin == true)
                  ActionChip(
                      label: const Text('Excluir'),
                      avatar: const Icon(Icons.delete_outline, size: 18),
                      onPressed: item.id == null || !_isFinalized(item)
                          ? null
                          : () => state.deleteSolicitacao(item.id!)),
              ],
            ),
          ],
        ),
      ),
    );
  }
}

bool _isFinalized(Solicitacao item) =>
    DevFacilConstants.finalizedStatuses.contains(item.status);

Future<void> _showDemandDetails(
  BuildContext context,
  AppState state,
  Solicitacao item,
) {
  final cliente = state.clientes
          .where((value) => value.id == item.clienteId)
          .firstOrNull
          ?.nome ??
      'Cliente ${item.clienteId}';
  final dev = item.desenvolvedorId == null
      ? 'Sem responsavel'
      : state.desenvolvedores
              .where((value) => value.id == item.desenvolvedorId)
              .firstOrNull
              ?.nome ??
          'Dev ${item.desenvolvedorId}';

  return showDialog<void>(
    context: context,
    builder: (context) => AlertDialog(
      title: Text(item.tipoServico),
      content: SizedBox(
        width: 520,
        child: SingleChildScrollView(
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            mainAxisSize: MainAxisSize.min,
            children: [
              _DetailLine(
                  label: 'Status', value: item.status.replaceAll('_', ' ')),
              _DetailLine(label: 'Cliente', value: cliente),
              _DetailLine(label: 'Desenvolvedor', value: dev),
              _DetailLine(label: 'Prioridade', value: item.prioridade),
              if (item.prazoDesejado != null)
                _DetailLine(label: 'Prazo', value: item.prazoDesejado!),
              if (item.orcamentoEstimado != null)
                _DetailLine(
                    label: 'Orcamento',
                    value: 'R\$ ${item.orcamentoEstimado!.toStringAsFixed(2)}'),
              const SizedBox(height: 12),
              Text('Descricao', style: Theme.of(context).textTheme.titleSmall),
              Text(item.descricao),
              const SizedBox(height: 12),
              Text('Requisitos', style: Theme.of(context).textTheme.titleSmall),
              Text(item.requisitos),
              if (item.zipNome != null) ...[
                const SizedBox(height: 12),
                _DetailLine(label: 'ZIP anexado', value: item.zipNome!),
              ],
              if (item.motivoCancelamento != null) ...[
                const SizedBox(height: 12),
                _DetailLine(
                    label: 'Motivo do cancelamento',
                    value: item.motivoCancelamento!),
              ],
            ],
          ),
        ),
      ),
      actions: [
        TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('Fechar')),
      ],
    ),
  );
}

class _DetailLine extends StatelessWidget {
  const _DetailLine({required this.label, required this.value});

  final String label;
  final String value;

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.only(bottom: 6),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          SizedBox(
              width: 128,
              child: Text(label,
                  style: const TextStyle(fontWeight: FontWeight.w700))),
          Expanded(child: Text(value)),
        ],
      ),
    );
  }
}

Future<void> _showCancelDemandDialog(
  BuildContext context,
  AppState state,
  Solicitacao item,
) {
  final motivo = TextEditingController();
  return showDialog<void>(
    context: context,
    builder: (context) => AlertDialog(
      title: const Text('Cancelar demanda'),
      content: TextField(
        controller: motivo,
        maxLines: 3,
        decoration: const InputDecoration(
          labelText: 'Motivo (opcional)',
          hintText: 'Explique brevemente o motivo do cancelamento',
        ),
      ),
      actions: [
        TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('Voltar')),
        FilledButton(
          onPressed: () async {
            await state.cancelarSolicitacao(
                item, motivo.text.trim().isEmpty ? null : motivo.text.trim());
            if (context.mounted) {
              Navigator.pop(context);
            }
          },
          child: const Text('Cancelar demanda'),
        ),
      ],
    ),
  );
}

Future<void> _pickAndUploadZip(
  BuildContext context,
  AppState state,
  Solicitacao item,
) async {
  final result = await FilePicker.platform.pickFiles(
    type: FileType.custom,
    allowedExtensions: ['zip'],
    withData: false,
  );
  final file = result?.files.single;
  if (file == null || file.path == null) {
    return;
  }
  await state.anexarZip(item, file.path!, file.name);
  if (context.mounted) {
    ScaffoldMessenger.of(context).showSnackBar(
      const SnackBar(content: Text('ZIP anexado com sucesso.')),
    );
  }
}

class _MetricCard extends StatelessWidget {
  const _MetricCard(
      {required this.label, required this.value, required this.icon});

  final String label;
  final String value;
  final IconData icon;

  @override
  Widget build(BuildContext context) {
    return SizedBox(
      width: 220,
      child: Card(
        child: Padding(
          padding: const EdgeInsets.all(18),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Icon(icon, color: AppTheme.primary),
              const SizedBox(height: 16),
              Text(value,
                  style: Theme.of(context)
                      .textTheme
                      .headlineMedium
                      ?.copyWith(fontWeight: FontWeight.w800)),
              Text(label, style: const TextStyle(color: Colors.black54)),
            ],
          ),
        ),
      ),
    );
  }
}

class _PersonTile extends StatelessWidget {
  const _PersonTile(
      {required this.title,
      required this.subtitle,
      this.trailing,
      this.onEdit,
      this.onDelete});

  final String title;
  final String subtitle;
  final String? trailing;
  final VoidCallback? onEdit;
  final VoidCallback? onDelete;

  @override
  Widget build(BuildContext context) {
    return Card(
      margin: const EdgeInsets.only(bottom: 10),
      child: ListTile(
        leading: const CircleAvatar(child: Icon(Icons.person_outline)),
        title: Text(title),
        subtitle: Text(subtitle),
        trailing: Wrap(
          crossAxisAlignment: WrapCrossAlignment.center,
          children: [
            if (trailing != null)
              Padding(
                  padding: const EdgeInsets.only(right: 8),
                  child: Text(trailing!)),
            IconButton(
                onPressed: onEdit,
                icon: const Icon(Icons.edit_outlined),
                tooltip: 'Editar'),
            IconButton(
                onPressed: onDelete,
                icon: const Icon(Icons.delete_outline),
                tooltip: 'Excluir'),
          ],
        ),
      ),
    );
  }
}

class _StatusChip extends StatelessWidget {
  const _StatusChip({required this.status});

  final String status;

  @override
  Widget build(BuildContext context) {
    final colors = _statusColors(status);
    return Chip(
      label: Text(status.replaceAll('_', ' ')),
      visualDensity: VisualDensity.compact,
      backgroundColor: colors.$1,
      labelStyle: TextStyle(
        color: colors.$2,
        fontWeight: FontWeight.w700,
      ),
    );
  }

  (Color, Color) _statusColors(String value) {
    return switch (value) {
      'concluida' => (Colors.green.shade50, Colors.green.shade800),
      'cancelada' => (Colors.red.shade50, Colors.red.shade800),
      'em_andamento' => (Colors.amber.shade50, Colors.amber.shade900),
      _ => (AppTheme.primary.withValues(alpha: 0.08), AppTheme.primary),
    };
  }
}

class _ErrorBanner extends StatelessWidget {
  const _ErrorBanner({required this.message});

  final String message;

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.only(top: 16),
      child: MaterialBanner(
        content: Text(message),
        leading: const Icon(Icons.error_outline),
        actions: const [SizedBox.shrink()],
      ),
    );
  }
}

class _EmptyState extends StatelessWidget {
  const _EmptyState({required this.text});

  final String text;

  @override
  Widget build(BuildContext context) {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(24),
        child: Center(child: Text(text)),
      ),
    );
  }
}
