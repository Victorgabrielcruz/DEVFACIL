import '../../domain/models/devfacil_models.dart';
import '../../domain/repositories/devfacil_repository.dart';
import '../services/api_service.dart';

class RestDevFacilRepository implements DevFacilRepository {
  RestDevFacilRepository({ApiService? api}) : api = api ?? ApiService();

  final ApiService api;

  @override
  void setAuthToken(String? token) {
    api.authToken = token;
  }

  @override
  Future<AuthSession> login(String email, String senha) =>
      api.login(email, senha);

  @override
  Future<AuthSession> me(String token) => api.me(token);

  @override
  Future<AuthSession> cadastrarCliente({
    required String nome,
    required String telefone,
    required String email,
    required String empresa,
    required String senha,
  }) =>
      api.cadastrarCliente(
        nome: nome,
        telefone: telefone,
        email: email,
        empresa: empresa,
        senha: senha,
      );

  @override
  Future<AuthSession> cadastrarDesenvolvedor({
    required String nome,
    required String telefone,
    required String email,
    required String stack,
    required String senioridade,
    String? portfolioUrl,
    required bool disponivel,
    required String senha,
  }) =>
      api.cadastrarDesenvolvedor(
        nome: nome,
        telefone: telefone,
        email: email,
        stack: stack,
        senioridade: senioridade,
        portfolioUrl: portfolioUrl,
        disponivel: disponivel,
        senha: senha,
      );

  @override
  Future<AuthSession> cadastrarAdmin({
    required String nome,
    required String email,
    required String senha,
    required String adminKey,
  }) =>
      api.cadastrarAdmin(
        nome: nome,
        email: email,
        senha: senha,
        adminKey: adminKey,
      );

  @override
  Future<List<Cliente>> listarClientes() => api.listarClientes();

  @override
  Future<Cliente> criarCliente(Cliente value) => api.criarCliente(value);

  @override
  Future<Cliente> atualizarCliente(Cliente value) =>
      api.atualizarCliente(value);

  @override
  Future<void> removerCliente(int id) => api.removerCliente(id);

  @override
  Future<List<Desenvolvedor>> listarDesenvolvedores() =>
      api.listarDesenvolvedores();

  @override
  Future<Desenvolvedor> criarDesenvolvedor(Desenvolvedor value) =>
      api.criarDesenvolvedor(value);

  @override
  Future<Desenvolvedor> atualizarDesenvolvedor(Desenvolvedor value) =>
      api.atualizarDesenvolvedor(value);

  @override
  Future<void> removerDesenvolvedor(int id) => api.removerDesenvolvedor(id);

  @override
  Future<List<Solicitacao>> listarSolicitacoes() => api.listarSolicitacoes();

  @override
  Future<Solicitacao> criarSolicitacao(Solicitacao value) =>
      api.criarSolicitacao(value);

  @override
  Future<Solicitacao> atualizarSolicitacao(Solicitacao value) =>
      api.atualizarSolicitacao(value);

  @override
  Future<Solicitacao> atualizarStatus(
    int id,
    String status, {
    int? desenvolvedorId,
  }) =>
      api.atualizarStatus(id, status, desenvolvedorId: desenvolvedorId);

  @override
  Future<Solicitacao> cancelarSolicitacao(int id, String? motivo) =>
      api.cancelarSolicitacao(id, motivo);

  @override
  Future<Solicitacao> anexarZip(int id, String filePath, String fileName) =>
      api.anexarZip(id, filePath, fileName);

  @override
  Future<void> removerSolicitacao(int id) => api.removerSolicitacao(id);

  @override
  Future<List<MessageAudit>> listarAuditoria() => api.listarAuditoria();
}
