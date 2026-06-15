import 'dart:convert';

import 'package:http/http.dart' as http;

import '../../domain/models/devfacil_models.dart';

class ApiService {
  ApiService({String? baseUrl})
      : baseUrl = baseUrl ??
            const String.fromEnvironment(
              'API_BASE_URL',
              defaultValue: 'http://localhost:8080',
            );

  final String baseUrl;
  String? authToken;

  Future<AuthSession> login(String email, String senha) async {
    final session = await _send(
        '/auth/login',
        'POST',
        {
          'email': email,
          'senha': senha,
        },
        AuthSession.fromJson,
        authenticated: false);
    authToken = session.token;
    return session;
  }

  Future<AuthSession> me(String token) async {
    authToken = token;
    final response = await http.get(_uri('/auth/me'), headers: _headers());
    _ensureSuccess(response);
    final session =
        AuthSession.fromJson(jsonDecode(response.body) as Map<String, dynamic>);
    authToken = session.token;
    return session;
  }

  Future<AuthSession> cadastrarCliente({
    required String nome,
    required String telefone,
    required String email,
    required String empresa,
    required String senha,
  }) async {
    final session = await _send(
        '/auth/cadastro/cliente',
        'POST',
        {
          'nome': nome,
          'telefone': telefone,
          'email': email,
          'empresa': empresa,
          'senha': senha,
        },
        AuthSession.fromJson,
        authenticated: false);
    authToken = session.token;
    return session;
  }

  Future<AuthSession> cadastrarDesenvolvedor({
    required String nome,
    required String telefone,
    required String email,
    required String stack,
    required String senioridade,
    String? portfolioUrl,
    required bool disponivel,
    required String senha,
  }) async {
    final session = await _send(
        '/auth/cadastro/desenvolvedor',
        'POST',
        {
          'nome': nome,
          'telefone': telefone,
          'email': email,
          'stack': stack,
          'senioridade': senioridade,
          'portfolio_url': portfolioUrl,
          'disponivel': disponivel,
          'senha': senha,
        },
        AuthSession.fromJson,
        authenticated: false);
    authToken = session.token;
    return session;
  }

  Future<AuthSession> cadastrarAdmin({
    required String nome,
    required String email,
    required String senha,
    required String adminKey,
  }) async {
    final session = await _send(
        '/auth/cadastro/admin',
        'POST',
        {
          'nome': nome,
          'email': email,
          'senha': senha,
          'admin_key': adminKey,
        },
        AuthSession.fromJson,
        authenticated: false);
    authToken = session.token;
    return session;
  }

  Future<List<Cliente>> listarClientes() =>
      _list('/clientes', Cliente.fromJson);
  Future<Cliente> criarCliente(Cliente value) =>
      _send('/clientes', 'POST', value.toJson(), Cliente.fromJson);
  Future<Cliente> atualizarCliente(Cliente value) =>
      _send('/clientes/${value.id}', 'PUT', value.toJson(), Cliente.fromJson);
  Future<void> removerCliente(int id) => _delete('/clientes/$id');

  Future<List<Desenvolvedor>> listarDesenvolvedores() =>
      _list('/desenvolvedores', Desenvolvedor.fromJson);
  Future<Desenvolvedor> criarDesenvolvedor(Desenvolvedor value) =>
      _send('/desenvolvedores', 'POST', value.toJson(), Desenvolvedor.fromJson);
  Future<Desenvolvedor> atualizarDesenvolvedor(Desenvolvedor value) => _send(
      '/desenvolvedores/${value.id}',
      'PUT',
      value.toJson(),
      Desenvolvedor.fromJson);
  Future<void> removerDesenvolvedor(int id) => _delete('/desenvolvedores/$id');

  Future<List<Solicitacao>> listarSolicitacoes() =>
      _list('/solicitacoes', Solicitacao.fromJson);
  Future<Solicitacao> criarSolicitacao(Solicitacao value) =>
      _send('/solicitacoes', 'POST', value.toJson(), Solicitacao.fromJson);
  Future<Solicitacao> atualizarSolicitacao(Solicitacao value) => _send(
      '/solicitacoes/${value.id}', 'PUT', value.toJson(), Solicitacao.fromJson);
  Future<Solicitacao> atualizarStatus(int id, String status,
      {int? desenvolvedorId}) {
    return _send(
        '/solicitacoes/$id/status',
        'PATCH',
        {
          'status': status,
          'desenvolvedor_id': desenvolvedorId,
        },
        Solicitacao.fromJson);
  }

  Future<Solicitacao> cancelarSolicitacao(int id, String? motivo) {
    return _send(
        '/solicitacoes/$id/cancelar',
        'PATCH',
        {
          'motivo_cancelamento': motivo,
        },
        Solicitacao.fromJson);
  }

  Future<Solicitacao> anexarZip(
      int id, String filePath, String fileName) async {
    final request = http.MultipartRequest('POST', _uri('/solicitacoes/$id/zip'))
      ..headers.addAll(_headers()..remove('Content-Type'))
      ..files.add(await http.MultipartFile.fromPath(
        'arquivo',
        filePath,
        filename: fileName,
      ));
    final streamed = await request.send();
    final response = await http.Response.fromStream(streamed);
    _ensureSuccess(response);
    return Solicitacao.fromJson(
        jsonDecode(response.body) as Map<String, dynamic>);
  }

  Future<void> removerSolicitacao(int id) => _delete('/solicitacoes/$id');
  Future<List<MessageAudit>> listarAuditoria() =>
      _list('/auditoria-mensageria', MessageAudit.fromJson);

  Future<List<T>> _list<T>(
      String path, T Function(Map<String, dynamic>) fromJson) async {
    final response = await http.get(_uri(path), headers: _headers());
    _ensureSuccess(response);
    return (jsonDecode(response.body) as List)
        .map((item) => fromJson(item as Map<String, dynamic>))
        .toList();
  }

  Future<T> _send<T>(
    String path,
    String method,
    Map<String, dynamic> body,
    T Function(Map<String, dynamic>) fromJson, {
    bool authenticated = true,
  }) async {
    final request = http.Request(method, _uri(path))
      ..headers.addAll(_headers(authenticated: authenticated))
      ..body = jsonEncode(body);
    final streamed = await request.send();
    final response = await http.Response.fromStream(streamed);
    _ensureSuccess(response);
    return fromJson(jsonDecode(response.body) as Map<String, dynamic>);
  }

  Future<void> _delete(String path) async {
    final response = await http.delete(_uri(path), headers: _headers());
    _ensureSuccess(response);
  }

  Uri _uri(String path) => Uri.parse('$baseUrl$path');

  Map<String, String> _headers({bool authenticated = true}) {
    return {
      'Content-Type': 'application/json',
      if (authenticated && authToken != null)
        'Authorization': 'Bearer $authToken',
    };
  }

  void _ensureSuccess(http.Response response) {
    if (response.statusCode >= 200 && response.statusCode < 300) {
      return;
    }

    String message = 'Erro HTTP ${response.statusCode}';
    try {
      final body = jsonDecode(response.body) as Map<String, dynamic>;
      message = body['erro'] ?? body['mensagem'] ?? body['message'] ?? message;
    } catch (_) {
      // Keeps the HTTP status fallback for non-JSON error responses.
    }
    throw ApiException(message);
  }
}

class ApiException implements Exception {
  ApiException(this.message);

  final String message;

  @override
  String toString() => message;
}
