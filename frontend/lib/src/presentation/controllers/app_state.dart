import 'dart:async';
import 'dart:convert';

import 'package:flutter/foundation.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';

import '../../data/repositories/rest_devfacil_repository.dart';
import '../../domain/models/devfacil_models.dart';
import '../../domain/repositories/devfacil_repository.dart';

class AppState extends ChangeNotifier {
  AppState({DevFacilRepository? repository, FlutterSecureStorage? storage})
      : repository = repository ?? RestDevFacilRepository(),
        storage = storage ?? const FlutterSecureStorage();

  final DevFacilRepository repository;
  final FlutterSecureStorage storage;
  var loading = false;
  String? error;
  AuthSession? session;
  List<Cliente> clientes = [];
  List<Desenvolvedor> desenvolvedores = [];
  List<Solicitacao> solicitacoes = [];
  List<MessageAudit> auditoria = [];

  bool get isAuthenticated => session != null;

  Future<void> restoreSession() => _run(() async {
        final token = await storage.read(key: 'auth_token');
        final sessionJson = await storage.read(key: 'auth_session');
        if (token == null || sessionJson == null) {
          return;
        }

        repository.setAuthToken(token);
        session = AuthSession.fromJson(
            jsonDecode(sessionJson) as Map<String, dynamic>);
        try {
          session = await repository.me(token);
          await _persistSession();
          await _loadDataForSession();
        } catch (_) {
          await logout(clearError: false);
        }
      });

  Future<void> login(String email, String senha) =>
      _authenticate(() => repository.login(email, senha));

  Future<void> cadastrarCliente({
    required String nome,
    required String telefone,
    required String email,
    required String empresa,
    required String senha,
  }) =>
      _authenticate(
        () => repository.cadastrarCliente(
          nome: nome,
          telefone: telefone,
          email: email,
          empresa: empresa,
          senha: senha,
        ),
      );

  Future<void> cadastrarDesenvolvedor({
    required String nome,
    required String telefone,
    required String email,
    required String stack,
    required String senioridade,
    String? portfolioUrl,
    required bool disponivel,
    required String senha,
  }) =>
      _authenticate(
        () => repository.cadastrarDesenvolvedor(
          nome: nome,
          telefone: telefone,
          email: email,
          stack: stack,
          senioridade: senioridade,
          portfolioUrl: portfolioUrl,
          disponivel: disponivel,
          senha: senha,
        ),
      );

  Future<void> cadastrarAdmin({
    required String nome,
    required String email,
    required String senha,
    required String adminKey,
  }) =>
      _authenticate(
        () => repository.cadastrarAdmin(
          nome: nome,
          email: email,
          senha: senha,
          adminKey: adminKey,
        ),
      );

  Future<void> logout({bool clearError = true}) async {
    session = null;
    repository.setAuthToken(null);
    await storage.delete(key: 'auth_token');
    await storage.delete(key: 'auth_session');
    clientes = [];
    desenvolvedores = [];
    solicitacoes = [];
    auditoria = [];
    if (clearError) {
      error = null;
    }
    notifyListeners();
  }

  Future<void> loadAll() async {
    await _run(_loadDataForSession);
  }

  Future<void> _loadDataForSession() async {
    final result = await Future.wait([
      repository.listarClientes(),
      repository.listarDesenvolvedores(),
      repository.listarSolicitacoes(),
      if (session?.isAdmin ?? false)
        repository.listarAuditoria()
      else
        Future.value(<MessageAudit>[]),
    ]);
    clientes = result[0] as List<Cliente>;
    desenvolvedores = result[1] as List<Desenvolvedor>;
    solicitacoes = result[2] as List<Solicitacao>;
    auditoria = result[3] as List<MessageAudit>;
  }

  Future<void> saveCliente(Cliente value) => _run(() async {
        if (value.id == null) {
          await repository.criarCliente(value);
        } else {
          await repository.atualizarCliente(value);
        }
        clientes = await repository.listarClientes();
      });

  Future<void> deleteCliente(int id) => _run(() async {
        await repository.removerCliente(id);
        clientes = await repository.listarClientes();
      });

  Future<void> saveDesenvolvedor(Desenvolvedor value) => _run(() async {
        if (value.id == null) {
          await repository.criarDesenvolvedor(value);
        } else {
          await repository.atualizarDesenvolvedor(value);
        }
        desenvolvedores = await repository.listarDesenvolvedores();
      });

  Future<void> deleteDesenvolvedor(int id) => _run(() async {
        await repository.removerDesenvolvedor(id);
        desenvolvedores = await repository.listarDesenvolvedores();
      });

  Future<void> saveSolicitacao(Solicitacao value) => _run(() async {
        if (value.id == null) {
          await repository.criarSolicitacao(value);
        } else {
          await repository.atualizarSolicitacao(value);
        }
        solicitacoes = await repository.listarSolicitacoes();
        if (session?.isAdmin ?? false) {
          auditoria = await repository.listarAuditoria();
        }
      });

  Future<void> updateStatus(Solicitacao value, String status,
          {int? desenvolvedorId}) =>
      _run(() async {
        await repository.atualizarStatus(value.id!, status,
            desenvolvedorId: desenvolvedorId);
        solicitacoes = await repository.listarSolicitacoes();
        if (session?.isAdmin ?? false) {
          auditoria = await repository.listarAuditoria();
        }
      });

  Future<void> cancelarSolicitacao(Solicitacao value, String? motivo) =>
      _run(() async {
        await repository.cancelarSolicitacao(value.id!, motivo);
        solicitacoes = await repository.listarSolicitacoes();
        if (session?.isAdmin ?? false) {
          auditoria = await repository.listarAuditoria();
        }
      });

  Future<void> anexarZip(Solicitacao value, String filePath, String fileName) =>
      _run(() async {
        await repository.anexarZip(value.id!, filePath, fileName);
        solicitacoes = await repository.listarSolicitacoes();
      });

  Future<void> deleteSolicitacao(int id) => _run(() async {
        await repository.removerSolicitacao(id);
        solicitacoes = await repository.listarSolicitacoes();
      });

  Future<void> _run(Future<void> Function() action) async {
    loading = true;
    error = null;
    notifyListeners();
    try {
      await action();
    } catch (exception) {
      error = exception.toString();
    } finally {
      loading = false;
      notifyListeners();
    }
  }

  Future<void> _authenticate(Future<AuthSession> Function() action) async {
    loading = true;
    error = null;
    notifyListeners();
    try {
      session = await action();
      await _persistSession();
      loading = false;
      notifyListeners();
      unawaited(loadAll());
    } catch (exception) {
      error = exception.toString();
      loading = false;
      notifyListeners();
    }
  }

  Future<void> _persistSession() async {
    final current = session;
    if (current == null) {
      return;
    }
    await storage.write(key: 'auth_token', value: current.token);
    await storage.write(
        key: 'auth_session', value: jsonEncode(current.toJson()));
  }
}
