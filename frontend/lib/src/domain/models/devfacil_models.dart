class Cliente {
  Cliente({
    this.id,
    required this.nome,
    required this.telefone,
    required this.email,
    required this.empresa,
    this.createdAt,
  });

  final int? id;
  final String nome;
  final String telefone;
  final String email;
  final String empresa;
  final DateTime? createdAt;

  factory Cliente.fromJson(Map<String, dynamic> json) => Cliente(
        id: json['id'],
        nome: json['nome'] ?? '',
        telefone: json['telefone'] ?? '',
        email: json['email'] ?? '',
        empresa: json['empresa'] ?? '',
        createdAt: _date(json['createdAt']),
      );

  Map<String, dynamic> toJson() => {
        'nome': nome,
        'telefone': telefone,
        'email': email,
        'empresa': empresa,
      };
}

class Desenvolvedor {
  Desenvolvedor({
    this.id,
    required this.nome,
    required this.telefone,
    required this.email,
    required this.stack,
    required this.senioridade,
    this.portfolioUrl,
    this.disponivel = true,
    this.createdAt,
  });

  final int? id;
  final String nome;
  final String telefone;
  final String email;
  final String stack;
  final String senioridade;
  final String? portfolioUrl;
  final bool disponivel;
  final DateTime? createdAt;

  factory Desenvolvedor.fromJson(Map<String, dynamic> json) => Desenvolvedor(
        id: json['id'],
        nome: json['nome'] ?? '',
        telefone: json['telefone'] ?? '',
        email: json['email'] ?? '',
        stack: json['stack'] ?? '',
        senioridade: json['senioridade'] ?? '',
        portfolioUrl: json['portfolio_url'],
        disponivel: json['disponivel'] ?? true,
        createdAt: _date(json['createdAt']),
      );

  Map<String, dynamic> toJson() => {
        'nome': nome,
        'telefone': telefone,
        'email': email,
        'stack': stack,
        'senioridade': senioridade,
        'portfolio_url': portfolioUrl,
        'disponivel': disponivel,
      };
}

class Solicitacao {
  Solicitacao({
    this.id,
    required this.clienteId,
    this.desenvolvedorId,
    required this.tipoServico,
    required this.descricao,
    required this.requisitos,
    this.prazoDesejado,
    this.orcamentoEstimado,
    this.prioridade = 'normal',
    this.status = 'aberta',
    this.zipNome,
    this.motivoCancelamento,
    this.createdAt,
    this.updatedAt,
  });

  final int? id;
  final int clienteId;
  final int? desenvolvedorId;
  final String tipoServico;
  final String descricao;
  final String requisitos;
  final String? prazoDesejado;
  final double? orcamentoEstimado;
  final String prioridade;
  final String status;
  final String? zipNome;
  final String? motivoCancelamento;
  final DateTime? createdAt;
  final DateTime? updatedAt;

  factory Solicitacao.fromJson(Map<String, dynamic> json) => Solicitacao(
        id: json['id'],
        clienteId: json['cliente_id'] ?? 0,
        desenvolvedorId: json['desenvolvedor_id'],
        tipoServico: json['tipo_servico'] ?? '',
        descricao: json['descricao'] ?? '',
        requisitos: json['requisitos'] ?? '',
        prazoDesejado: json['prazo_desejado'],
        orcamentoEstimado: (json['orcamento_estimado'] as num?)?.toDouble(),
        prioridade: json['prioridade'] ?? 'normal',
        status: json['status'] ?? 'aberta',
        zipNome: json['zip_nome'],
        motivoCancelamento: json['motivo_cancelamento'],
        createdAt: _date(json['createdAt']),
        updatedAt: _date(json['updatedAt']),
      );

  Map<String, dynamic> toJson() => {
        'cliente_id': clienteId,
        'desenvolvedor_id': desenvolvedorId,
        'tipo_servico': tipoServico,
        'descricao': descricao,
        'requisitos': requisitos,
        'prazo_desejado': prazoDesejado,
        'orcamento_estimado': orcamentoEstimado,
        'prioridade': prioridade,
      };
}

class MessageAudit {
  MessageAudit({
    required this.id,
    required this.direction,
    required this.eventName,
    required this.exchangeName,
    required this.routingKey,
    this.queueName,
    required this.payload,
    this.createdAt,
  });

  final int id;
  final String direction;
  final String eventName;
  final String exchangeName;
  final String routingKey;
  final String? queueName;
  final String payload;
  final DateTime? createdAt;

  factory MessageAudit.fromJson(Map<String, dynamic> json) => MessageAudit(
        id: json['id'] ?? 0,
        direction: json['direction'] ?? '',
        eventName: json['eventName'] ?? '',
        exchangeName: json['exchangeName'] ?? '',
        routingKey: json['routingKey'] ?? '',
        queueName: json['queueName'],
        payload: json['payload'] ?? '',
        createdAt: _date(json['createdAt']),
      );
}

class AuthSession {
  AuthSession({
    required this.token,
    required this.usuarioId,
    required this.nome,
    required this.email,
    required this.perfil,
    this.clienteId,
    this.desenvolvedorId,
  });

  final String token;
  final int usuarioId;
  final String nome;
  final String email;
  final String perfil;
  final int? clienteId;
  final int? desenvolvedorId;

  bool get isCliente => perfil == 'CLIENTE';
  bool get isDesenvolvedor => perfil == 'DESENVOLVEDOR';
  bool get isAdmin => perfil == 'ADMIN';

  factory AuthSession.fromJson(Map<String, dynamic> json) => AuthSession(
        token: json['token'] ?? '',
        usuarioId: json['usuarioId'] ?? 0,
        nome: json['nome'] ?? '',
        email: json['email'] ?? '',
        perfil: json['perfil'] ?? '',
        clienteId: json['clienteId'],
        desenvolvedorId: json['desenvolvedorId'],
      );

  Map<String, dynamic> toJson() => {
        'token': token,
        'usuarioId': usuarioId,
        'nome': nome,
        'email': email,
        'perfil': perfil,
        'clienteId': clienteId,
        'desenvolvedorId': desenvolvedorId,
      };
}

DateTime? _date(Object? value) {
  if (value == null) {
    return null;
  }
  return DateTime.tryParse(value.toString());
}
