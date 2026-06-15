import '../models/devfacil_models.dart';

abstract class DevFacilRepository {
  void setAuthToken(String? token);

  Future<AuthSession> login(String email, String senha);
  Future<AuthSession> me(String token);
  Future<AuthSession> cadastrarCliente({
    required String nome,
    required String telefone,
    required String email,
    required String empresa,
    required String senha,
  });
  Future<AuthSession> cadastrarDesenvolvedor({
    required String nome,
    required String telefone,
    required String email,
    required String stack,
    required String senioridade,
    String? portfolioUrl,
    required bool disponivel,
    required String senha,
  });
  Future<AuthSession> cadastrarAdmin({
    required String nome,
    required String email,
    required String senha,
    required String adminKey,
  });

  Future<List<Cliente>> listarClientes();
  Future<Cliente> criarCliente(Cliente value);
  Future<Cliente> atualizarCliente(Cliente value);
  Future<void> removerCliente(int id);

  Future<List<Desenvolvedor>> listarDesenvolvedores();
  Future<Desenvolvedor> criarDesenvolvedor(Desenvolvedor value);
  Future<Desenvolvedor> atualizarDesenvolvedor(Desenvolvedor value);
  Future<void> removerDesenvolvedor(int id);

  Future<List<Solicitacao>> listarSolicitacoes();
  Future<Solicitacao> criarSolicitacao(Solicitacao value);
  Future<Solicitacao> atualizarSolicitacao(Solicitacao value);
  Future<Solicitacao> atualizarStatus(
    int id,
    String status, {
    int? desenvolvedorId,
  });
  Future<Solicitacao> cancelarSolicitacao(int id, String? motivo);
  Future<Solicitacao> anexarZip(int id, String filePath, String fileName);
  Future<void> removerSolicitacao(int id);

  Future<List<MessageAudit>> listarAuditoria();
}
