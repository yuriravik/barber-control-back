# language: pt

Funcionalidade: Gerenciamento de Pagamentos
  Como um usuário autenticado
  Quero registrar e consultar pagamentos dos agendamentos
  Para controlar o financeiro da barbearia

  Contexto:
    Dado que estou autenticado como "pagamentos_user@barbearia.com" com senha "senha123" e role "ADMIN"
    E que existe um cliente de pagamento com nome "Cliente Pgto", email "pgto.cliente@email.com" e telefone "11922222222"
    E que existe um barbeiro de pagamento com nome "Barbeiro Pgto", especialidade "Barba" e comissão 25.00
    E que existe um serviço de pagamento com nome "Serviço Pgto", preço 60.00 e duração 45 minutos

  Cenário: Registrar um pagamento com sucesso via PIX
    Dado que existe um agendamento de pagamento criado para o cliente, barbeiro e serviço em "2026-12-10T10:00:00"
    Quando eu registro um pagamento para o agendamento criado com valor 60.00 e forma de pagamento "PIX"
    Então o status da resposta deve ser 201
    E o campo "formaPagamento" da resposta deve ser "PIX"
    E o campo "status" da resposta deve ser "PAGO"

  Cenário: Registrar um pagamento com cartão
    Dado que existe um agendamento de pagamento criado para o cliente, barbeiro e serviço em "2026-12-11T11:00:00"
    Quando eu registro um pagamento para o agendamento criado com valor 60.00 e forma de pagamento "CARTAO"
    Então o status da resposta deve ser 201
    E o campo "formaPagamento" da resposta deve ser "CARTAO"

  Cenário: Registrar um pagamento em dinheiro
    Dado que existe um agendamento de pagamento criado para o cliente, barbeiro e serviço em "2026-12-12T09:00:00"
    Quando eu registro um pagamento para o agendamento criado com valor 60.00 e forma de pagamento "DINHEIRO"
    Então o status da resposta deve ser 201
    E o campo "formaPagamento" da resposta deve ser "DINHEIRO"

  Cenário: Não deve registrar pagamento duplicado para o mesmo agendamento
    Dado que existe um agendamento de pagamento criado para o cliente, barbeiro e serviço em "2026-12-13T10:00:00"
    E que já existe um pagamento registrado para o agendamento criado com valor 60.00 e forma "PIX"
    Quando eu registro um pagamento para o agendamento criado com valor 60.00 e forma de pagamento "PIX"
    Então o status da resposta deve ser 409

  Cenário: Não deve registrar pagamento para agendamento inexistente
    Quando eu registro um pagamento para o agendamentoId "00000000-0000-0000-0000-000000000099" com valor 60.00 e forma de pagamento "PIX"
    Então o status da resposta deve ser 404

  Cenário: Listar pagamentos com sucesso
    Dado que existe um agendamento de pagamento criado para o cliente, barbeiro e serviço em "2026-12-14T10:00:00"
    E que já existe um pagamento registrado para o agendamento criado com valor 60.00 e forma "DINHEIRO"
    Quando eu listo os pagamentos
    Então o status da resposta deve ser 200
    E a resposta deve ser uma lista

  Cenário: Buscar pagamento por ID com sucesso
    Dado que existe um agendamento de pagamento criado para o cliente, barbeiro e serviço em "2026-12-15T10:00:00"
    E que já existe um pagamento registrado para o agendamento criado com valor 60.00 e forma "CARTAO"
    Quando eu busco o pagamento pelo ID criado
    Então o status da resposta deve ser 200
    E o campo "formaPagamento" da resposta deve ser "CARTAO"

  Cenário: Não deve encontrar pagamento com ID inexistente
    Quando eu busco o pagamento com ID "00000000-0000-0000-0000-000000000099"
    Então o status da resposta deve ser 404
