import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

import '../../core/devfacil_constants.dart';
import '../../core/input_formatters.dart';
import '../../domain/models/devfacil_models.dart';
import '../controllers/app_state.dart';

Future<void> showClienteDialog(BuildContext context, AppState state,
    {Cliente? cliente}) {
  final nome = TextEditingController(text: cliente?.nome);
  final telefone = TextEditingController(text: cliente?.telefone);
  final email = TextEditingController(text: cliente?.email);
  final empresa = TextEditingController(text: cliente?.empresa);

  return _showForm(
    context,
    title: cliente == null ? 'Novo cliente' : 'Editar cliente',
    fields: [
      _field(nome, 'Nome'),
      _field(
        telefone,
        'Telefone',
        keyboardType: TextInputType.phone,
        inputFormatters: [PhoneInputFormatter()],
        phone: true,
      ),
      _field(
        email,
        'E-mail',
        keyboardType: TextInputType.emailAddress,
        email: true,
      ),
      _field(empresa, 'Empresa (opcional)', required: false),
    ],
    onSave: () => state.saveCliente(Cliente(
      id: cliente?.id,
      nome: nome.text.trim(),
      telefone: telefone.text.trim(),
      email: email.text.trim(),
      empresa: empresa.text.trim(),
    )),
  );
}

Future<void> showDesenvolvedorDialog(BuildContext context, AppState state,
    {Desenvolvedor? desenvolvedor}) {
  final nome = TextEditingController(text: desenvolvedor?.nome);
  final telefone = TextEditingController(text: desenvolvedor?.telefone);
  final email = TextEditingController(text: desenvolvedor?.email);
  final stack = TextEditingController(text: desenvolvedor?.stack);
  final senioridade = TextEditingController(text: desenvolvedor?.senioridade);
  final portfolio = TextEditingController(text: desenvolvedor?.portfolioUrl);
  var disponivel = desenvolvedor?.disponivel ?? true;

  return _showForm(
    context,
    title:
        desenvolvedor == null ? 'Novo desenvolvedor' : 'Editar desenvolvedor',
    fields: [
      _field(nome, 'Nome'),
      _field(
        telefone,
        'Telefone',
        keyboardType: TextInputType.phone,
        inputFormatters: [PhoneInputFormatter()],
        phone: true,
      ),
      _field(
        email,
        'E-mail',
        keyboardType: TextInputType.emailAddress,
        email: true,
      ),
      _field(stack, 'Stack principal'),
      _field(senioridade, 'Senioridade'),
      _field(portfolio, 'Portfolio URL', required: false, url: true),
      StatefulBuilder(
        builder: (context, setState) => SwitchListTile(
          contentPadding: EdgeInsets.zero,
          title: const Text('Disponivel para novas demandas'),
          value: disponivel,
          onChanged: (value) => setState(() => disponivel = value),
        ),
      ),
    ],
    onSave: () => state.saveDesenvolvedor(Desenvolvedor(
      id: desenvolvedor?.id,
      nome: nome.text.trim(),
      telefone: telefone.text.trim(),
      email: email.text.trim(),
      stack: stack.text.trim(),
      senioridade: senioridade.text.trim(),
      portfolioUrl:
          portfolio.text.trim().isEmpty ? null : portfolio.text.trim(),
      disponivel: disponivel,
    )),
  );
}

Future<void> showSolicitacaoDialog(BuildContext context, AppState state,
    {Solicitacao? solicitacao, Desenvolvedor? desenvolvedor}) {
  final tipo = TextEditingController(text: solicitacao?.tipoServico);
  final descricao = TextEditingController(text: solicitacao?.descricao);
  final requisitos = TextEditingController(text: solicitacao?.requisitos);
  final prazo = TextEditingController(text: solicitacao?.prazoDesejado);
  final orcamento =
      TextEditingController(text: solicitacao?.orcamentoEstimado?.toString());
  var clienteId = solicitacao?.clienteId ??
      state.session?.clienteId ??
      state.clientes.firstOrNull?.id;
  var desenvolvedorId = solicitacao?.desenvolvedorId ??
      desenvolvedor?.id ??
      state.session?.desenvolvedorId;
  var prioridade = solicitacao?.prioridade ?? 'normal';
  final isClienteRegular =
      state.session?.isCliente == true && state.session?.isAdmin != true;
  const serviceTypes = DevFacilConstants.serviceTypes;
  var tipoServico =
      serviceTypes.contains(tipo.text) ? tipo.text : serviceTypes.first;
  if (tipo.text.isEmpty) {
    tipo.text = tipoServico;
  }

  return _showForm(
    context,
    title: solicitacao == null
        ? desenvolvedor == null
            ? 'Nova solicitacao'
            : 'Solicitar ${desenvolvedor.nome}'
        : 'Editar solicitacao',
    fields: [
      StatefulBuilder(
        builder: (context, setState) => Column(
          children: [
            DropdownButtonFormField<int>(
              initialValue: clienteId,
              decoration: const InputDecoration(labelText: 'Cliente'),
              items: state.clientes
                  .where((item) =>
                      state.session?.isAdmin == true ||
                      state.session?.clienteId == null ||
                      item.id == state.session?.clienteId)
                  .map((item) =>
                      DropdownMenuItem(value: item.id, child: Text(item.nome)))
                  .toList(),
              validator: (value) => value == null ? 'Campo obrigatorio' : null,
              onChanged: (value) => setState(() => clienteId = value),
            ),
            const SizedBox(height: 12),
            if (isClienteRegular)
              _ReadOnlySelection(
                label: 'Destino da demanda',
                value: _developerName(state, desenvolvedorId) ??
                    'Aberta para todos os desenvolvedores',
              )
            else
              DropdownButtonFormField<int?>(
                initialValue: desenvolvedorId,
                decoration: const InputDecoration(
                    labelText: 'Desenvolvedor responsavel'),
                items: [
                  const DropdownMenuItem<int?>(
                      value: null, child: Text('Sem responsavel')),
                  ...state.desenvolvedores
                      .where((item) =>
                          state.session?.isAdmin == true ||
                          state.session?.desenvolvedorId == null ||
                          item.id == state.session?.desenvolvedorId)
                      .map((item) => DropdownMenuItem<int?>(
                          value: item.id, child: Text(item.nome))),
                ],
                onChanged: (value) => setState(() => desenvolvedorId = value),
              ),
            const SizedBox(height: 12),
            DropdownButtonFormField<String>(
              initialValue: prioridade,
              decoration: const InputDecoration(labelText: 'Prioridade'),
              items: DevFacilConstants.priorities
                  .map((item) => DropdownMenuItem(
                        value: item,
                        child: Text(_capitalize(item)),
                      ))
                  .toList(),
              onChanged: (value) =>
                  setState(() => prioridade = value ?? 'normal'),
            ),
            const SizedBox(height: 12),
            DropdownButtonFormField<String>(
              initialValue: tipoServico,
              decoration: const InputDecoration(labelText: 'Tipo de servico'),
              items: serviceTypes
                  .map((item) =>
                      DropdownMenuItem(value: item, child: Text(item)))
                  .toList(),
              onChanged: (value) {
                setState(() {
                  tipoServico = value ?? serviceTypes.first;
                  tipo.text = tipoServico;
                });
              },
            ),
          ],
        ),
      ),
      _field(descricao, 'Descricao', maxLines: 3, minLength: 10),
      _field(requisitos, 'Requisitos', maxLines: 4, minLength: 10),
      _field(
        prazo,
        'Prazo desejado',
        required: false,
        dateNotPast: true,
        keyboardType: TextInputType.datetime,
        inputFormatters: [DateInputFormatter()],
      ),
      _field(orcamento, 'Orcamento estimado',
          required: false,
          keyboardType: const TextInputType.numberWithOptions(decimal: true),
          inputFormatters: [MoneyInputFormatter()],
          mustBePositive: true),
    ],
    onSave: () => state.saveSolicitacao(Solicitacao(
      id: solicitacao?.id,
      clienteId: clienteId!,
      desenvolvedorId: isClienteRegular && desenvolvedor == null
          ? solicitacao?.desenvolvedorId
          : desenvolvedorId,
      tipoServico: tipoServico,
      descricao: descricao.text.trim(),
      requisitos: requisitos.text.trim(),
      prazoDesejado: prazo.text.trim().isEmpty ? null : prazo.text.trim(),
      orcamentoEstimado: double.tryParse(orcamento.text.replaceAll(',', '.')),
      prioridade: prioridade,
    )),
  );
}

String? _developerName(AppState state, int? id) {
  if (id == null) {
    return null;
  }
  return state.desenvolvedores
      .where((item) => item.id == id)
      .firstOrNull
      ?.nome;
}

String _capitalize(String value) =>
    value.isEmpty ? value : '${value[0].toUpperCase()}${value.substring(1)}';

class _ReadOnlySelection extends StatelessWidget {
  const _ReadOnlySelection({required this.label, required this.value});

  final String label;
  final String value;

  @override
  Widget build(BuildContext context) {
    return InputDecorator(
      decoration: InputDecoration(labelText: label),
      child: Text(value),
    );
  }
}

Future<void> _showForm(
  BuildContext context, {
  required String title,
  required List<Widget> fields,
  required Future<void> Function() onSave,
}) {
  final formKey = GlobalKey<FormState>();

  return showDialog<void>(
    context: context,
    builder: (context) => AlertDialog(
      title: Text(title),
      content: SizedBox(
        width: 560,
        child: Form(
          key: formKey,
          child: SingleChildScrollView(
            child: Column(
              mainAxisSize: MainAxisSize.min,
              children: fields
                  .map((field) => Padding(
                      padding: const EdgeInsets.only(bottom: 12), child: field))
                  .toList(),
            ),
          ),
        ),
      ),
      actions: [
        TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('Cancelar')),
        FilledButton(
          onPressed: () async {
            if (!formKey.currentState!.validate()) {
              return;
            }
            await onSave();
            if (context.mounted) {
              Navigator.pop(context);
            }
          },
          child: const Text('Salvar'),
        ),
      ],
    ),
  );
}

Widget _field(
  TextEditingController controller,
  String label, {
  bool required = true,
  int maxLines = 1,
  TextInputType? keyboardType,
  List<TextInputFormatter>? inputFormatters,
  int? minLength,
  bool mustBePositive = false,
  bool dateNotPast = false,
  bool email = false,
  bool phone = false,
  bool url = false,
}) {
  return TextFormField(
    controller: controller,
    maxLines: maxLines,
    keyboardType: keyboardType,
    inputFormatters: inputFormatters,
    decoration: InputDecoration(labelText: label),
    validator: required
        ? (value) {
            if (value == null || value.trim().isEmpty) {
              return 'Campo obrigatorio';
            }
            if (minLength != null && value.trim().length < minLength) {
              return 'Use pelo menos $minLength caracteres';
            }
            if (email && !_isEmail(value)) {
              return 'Informe um e-mail valido';
            }
            if (phone && onlyDigits(value).length < 10) {
              return 'Informe DDD e telefone';
            }
            if (url && !_isUrl(value)) {
              return 'Informe uma URL valida';
            }
            if (mustBePositive) {
              final parsed = double.tryParse(value.replaceAll(',', '.'));
              if (parsed == null || parsed < 0) {
                return 'Informe um valor valido';
              }
            }
            if (dateNotPast) {
              final parsed = DateTime.tryParse(value);
              final today = DateTime.now();
              final startOfToday = DateTime(today.year, today.month, today.day);
              if (parsed == null || parsed.isBefore(startOfToday)) {
                return 'Use yyyy-MM-dd e uma data futura';
              }
            }
            return null;
          }
        : (value) {
            if (mustBePositive && value != null && value.trim().isNotEmpty) {
              final parsed = double.tryParse(value.replaceAll(',', '.'));
              if (parsed == null || parsed < 0) {
                return 'Informe um valor valido';
              }
            }
            if (email && value != null && value.trim().isNotEmpty) {
              if (!_isEmail(value)) {
                return 'Informe um e-mail valido';
              }
            }
            if (phone && value != null && value.trim().isNotEmpty) {
              if (onlyDigits(value).length < 10) {
                return 'Informe DDD e telefone';
              }
            }
            if (url && value != null && value.trim().isNotEmpty) {
              if (!_isUrl(value)) {
                return 'Informe uma URL valida';
              }
            }
            if (dateNotPast && value != null && value.trim().isNotEmpty) {
              final parsed = DateTime.tryParse(value);
              final today = DateTime.now();
              final startOfToday = DateTime(today.year, today.month, today.day);
              if (parsed == null || parsed.isBefore(startOfToday)) {
                return 'Use yyyy-MM-dd e uma data futura';
              }
            }
            return null;
          },
  );
}

bool _isEmail(String value) {
  return RegExp(r'^[^@\s]+@[^@\s]+\.[^@\s]+$').hasMatch(value.trim());
}

bool _isUrl(String value) {
  final parsed = Uri.tryParse(value.trim());
  return parsed != null &&
      parsed.hasAbsolutePath == true &&
      (parsed.scheme == 'http' || parsed.scheme == 'https') &&
      parsed.host.isNotEmpty;
}
