# language: pt

Funcionalidade: Gerenciamento de Agendamentos
  Como um usuário autenticado
  Quero gerenciar os agendamentos da barbearia
  Para organizar o atendimento dos clientes

  Contexto:
    Dado que estou autenticado como "agendamentos_user@barbearia.com" com senha "senha123" e role "ADMIN"
    E que existe um cliente de agendamento com nome "Cliente Agend", email "agend.cliente@email.com" e telefone "11911111111"
    E que existe um barbeiro de agendamento com nome "Barbeiro Agend", especialidade "Corte" e comissão 20
    E que existe um serviço de agendamento com nome "Serviço Agend", preço 40 e duração 30 minutos

  Cenário: Criar um novo agendamento com sucesso
    Quando eu crio um agendamento para o cliente, barbeiro e serviço criados com data "2026-12-01T10:00:00"
    Então o status da resposta deve ser 201
    E o campo "status" da resposta deve ser "AGENDADO"

  Cenário: Listar agendamentos com sucesso
    Dado que existe um agendamento criado para o cliente, barbeiro e serviço em "2026-12-02T11:00:00"
    Quando eu listo os agendamentos
    Então o status da resposta deve ser 200
    E a resposta deve ser uma lista

  Cenário: Buscar agendamento por ID com sucesso
    Dado que existe um agendamento criado para o cliente, barbeiro e serviço em "2026-12-03T14:00:00"
    Quando eu busco o agendamento pelo ID criado
    Então o status da resposta deve ser 200
    E o campo "status" da resposta deve ser "AGENDADO"

  Cenário: Não deve encontrar agendamento com ID inexistente
    Quando eu busco o agendamento com ID "00000000-0000-0000-0000-000000000099"
    Então o status da resposta deve ser 404

  Cenário: Deletar agendamento com sucesso
    Dado que existe um agendamento criado para o cliente, barbeiro e serviço em "2026-12-04T09:00:00"
    Quando eu deleto o agendamento pelo ID criado
    Então o status da resposta deve ser 204

  Cenário: Não deve criar agendamento sem cliente
    Quando eu crio um agendamento sem clienteId, com o barbeiro e serviço criados e data "2026-12-05T10:00:00"
    Então o status da resposta deve ser 400

  Cenário: Não deve criar agendamento com serviço inexistente
    Quando eu crio um agendamento para o cliente e barbeiro criados com servicoId "00000000-0000-0000-0000-000000000099" e data "2026-12-06T10:00:00"
    Então o status da resposta deve ser 404
