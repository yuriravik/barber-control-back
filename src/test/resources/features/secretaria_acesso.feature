# language: pt

Funcionalidade: Acesso da Secretaria para Cadastrar Clientes e Agendamentos
  Como uma secretaria vinculada a um admin
  Quero cadastrar clientes e agendamentos
  Para organizar o atendimento da barbearia

  Contexto:
    Dado que existe um admin cadastrado com email "admin.secretaria@barbearia.com" e senha "senha123" salvo como "adminSecretaria"
    E que estou autenticado como "admin.secretaria@barbearia.com" com senha "senha123" e role "ADMIN"

  Cenário: Secretaria deve cadastrar um novo cliente com sucesso
    E que estou autenticada como secretaria "secretaria.cliente@barbearia.com" com senha "senha123" vinculada ao admin com id "adminSecretaria"
    Quando eu crio um cliente com nome "Cliente da Secretaria", email "cliente.secretaria@email.com" e telefone "11988880001"
    Então o status da resposta deve ser 201

  Cenário: Secretaria deve criar um novo agendamento com sucesso
    E que existe um cliente de agendamento com nome "Cliente Sec Agend", email "sec.agend.cliente@email.com" e telefone "11922220001"
    E que existe um barbeiro de agendamento com nome "Barbeiro Sec Agend", especialidade "Corte" e comissão 20
    E que existe um serviço de agendamento com nome "Serviço Sec Agend", preço 40 e duração 30 minutos
    E que estou autenticada como secretaria "secretaria.agend@barbearia.com" com senha "senha123" vinculada ao admin com id "adminSecretaria"
    Quando eu crio um agendamento para o cliente, barbeiro e serviço criados com data "2026-12-10T10:00:00"
    Então o status da resposta deve ser 201
    E o campo "status" da resposta deve ser "AGENDADO"

  Cenário: Secretaria deve visualizar todos os agendamentos dos barbeiros do admin
    E que existe um barbeiro de agendamento com nome "Barbeiro Sec Lista", especialidade "Corte" e comissão 20
    E que existe um cliente de agendamento com nome "Cliente Sec Lista", email "sec.lista.agend@email.com" e telefone "11900000002"
    E que existe um serviço de agendamento com nome "Serviço Sec Lista", preço 50 e duração 30 minutos
    E que existe um agendamento criado para o cliente, barbeiro e serviço em "2026-12-21T10:00:00"
    E que estou autenticada como secretaria "secretaria.lista.agend@barbearia.com" com senha "senha123" vinculada ao admin com id "adminSecretaria"
    Quando eu listo os agendamentos
    Então o status da resposta deve ser 200
    E a resposta deve ser uma lista
