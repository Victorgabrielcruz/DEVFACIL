import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

import '../../core/input_formatters.dart';
import '../controllers/app_state.dart';
import '../theme/app_theme.dart';

class AuthScreen extends StatefulWidget {
  const AuthScreen({super.key, required this.state});

  final AppState state;

  @override
  State<AuthScreen> createState() => _AuthScreenState();
}

class _AuthScreenState extends State<AuthScreen> {
  final formKey = GlobalKey<FormState>();
  final nome = TextEditingController();
  final telefone = TextEditingController();
  final email = TextEditingController();
  final senha = TextEditingController();
  final empresa = TextEditingController();
  final stack = TextEditingController();
  final senioridade = TextEditingController();
  final portfolio = TextEditingController();
  var cadastro = false;
  var perfil = 'CLIENTE';
  var disponivel = true;

  @override
  Widget build(BuildContext context) {
    return AnimatedBuilder(
      animation: widget.state,
      builder: (context, _) => Scaffold(
        body: SafeArea(
          child: Center(
            child: SingleChildScrollView(
              padding: const EdgeInsets.all(20),
              child: ConstrainedBox(
                constraints: const BoxConstraints(maxWidth: 520),
                child: Card(
                  child: Padding(
                    padding: const EdgeInsets.all(24),
                    child: Form(
                      key: formKey,
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.stretch,
                        mainAxisSize: MainAxisSize.min,
                        children: [
                          const Text(
                            'DevFacil',
                            style: TextStyle(
                              color: AppTheme.primary,
                              fontWeight: FontWeight.w900,
                              fontSize: 32,
                            ),
                          ),
                          const SizedBox(height: 8),
                          Text(
                            cadastro ? 'Crie sua conta' : 'Entre na plataforma',
                            style: Theme.of(context).textTheme.titleLarge,
                          ),
                          const SizedBox(height: 20),
                          SegmentedButton<bool>(
                            segments: const [
                              ButtonSegment(value: false, label: Text('Login')),
                              ButtonSegment(
                                  value: true, label: Text('Cadastro')),
                            ],
                            selected: {cadastro},
                            onSelectionChanged: (value) =>
                                setState(() => cadastro = value.first),
                          ),
                          if (cadastro) ...[
                            const SizedBox(height: 16),
                            SegmentedButton<String>(
                              segments: const [
                                ButtonSegment(
                                    value: 'CLIENTE', label: Text('Cliente')),
                                ButtonSegment(
                                    value: 'DESENVOLVEDOR', label: Text('Dev')),
                              ],
                              selected: {perfil},
                              onSelectionChanged: (value) =>
                                  setState(() => perfil = value.first),
                            ),
                          ],
                          const SizedBox(height: 16),
                          if (cadastro) _field(nome, 'Nome'),
                          _field(email, 'E-mail',
                              keyboardType: TextInputType.emailAddress,
                              email: true),
                          _field(senha, 'Senha', obscureText: true),
                          if (cadastro) ..._cadastroFields(),
                          const SizedBox(height: 8),
                          FilledButton(
                            onPressed: widget.state.loading ? null : _submit,
                            child: Text(widget.state.loading
                                ? 'Aguarde...'
                                : cadastro
                                    ? 'Criar conta'
                                    : 'Entrar'),
                          ),
                          if (widget.state.error != null) ...[
                            const SizedBox(height: 16),
                            Text(
                              widget.state.error!,
                              style: TextStyle(
                                  color: Theme.of(context).colorScheme.error),
                            ),
                          ],
                        ],
                      ),
                    ),
                  ),
                ),
              ),
            ),
          ),
        ),
      ),
    );
  }

  List<Widget> _cadastroFields() {
    return [
      _field(
        telefone,
        'Telefone',
        keyboardType: TextInputType.phone,
        inputFormatters: [PhoneInputFormatter()],
        phone: true,
      ),
      if (perfil == 'CLIENTE')
        _field(empresa, 'Empresa (opcional)', required: false),
      if (perfil == 'DESENVOLVEDOR') ...[
        _field(stack, 'Stack principal'),
        _field(senioridade, 'Senioridade'),
        _field(portfolio, 'Portfolio URL', required: false),
        SwitchListTile(
          contentPadding: EdgeInsets.zero,
          title: const Text('Disponivel para demandas'),
          value: disponivel,
          onChanged: (value) => setState(() => disponivel = value),
        ),
      ],
    ];
  }

  Future<void> _submit() async {
    if (!formKey.currentState!.validate()) {
      return;
    }

    if (!cadastro) {
      await widget.state.login(email.text.trim(), senha.text);
      return;
    }

    if (perfil == 'CLIENTE') {
      await widget.state.cadastrarCliente(
        nome: nome.text.trim(),
        telefone: telefone.text.trim(),
        email: email.text.trim(),
        empresa: empresa.text.trim(),
        senha: senha.text,
      );
      return;
    }

    await widget.state.cadastrarDesenvolvedor(
      nome: nome.text.trim(),
      telefone: telefone.text.trim(),
      email: email.text.trim(),
      stack: stack.text.trim(),
      senioridade: senioridade.text.trim(),
      portfolioUrl:
          portfolio.text.trim().isEmpty ? null : portfolio.text.trim(),
      disponivel: disponivel,
      senha: senha.text,
    );
  }
}

Widget _field(
  TextEditingController controller,
  String label, {
  bool required = true,
  bool obscureText = false,
  TextInputType? keyboardType,
  List<TextInputFormatter>? inputFormatters,
  bool email = false,
  bool phone = false,
}) {
  return Padding(
    padding: const EdgeInsets.only(bottom: 12),
    child: TextFormField(
      controller: controller,
      obscureText: obscureText,
      keyboardType: keyboardType,
      inputFormatters: inputFormatters,
      decoration: InputDecoration(labelText: label),
      validator: required
          ? (value) {
              if (value == null || value.trim().isEmpty) {
                return 'Campo obrigatorio';
              }
              if (email && !_isEmail(value)) {
                return 'Informe um e-mail valido';
              }
              if (phone && onlyDigits(value).length < 10) {
                return 'Informe DDD e telefone';
              }
              if (label == 'Senha' && value.length < 6) {
                return 'Use pelo menos 6 caracteres';
              }
              return null;
            }
          : null,
    ),
  );
}

bool _isEmail(String value) {
  return RegExp(r'^[^@\s]+@[^@\s]+\.[^@\s]+$').hasMatch(value.trim());
}
