# language: pt

Funcionalidade: Acesso do Barbeiro aos Dados do Admin
  Como um usuário barbeiro vinculado a um admin
  Quero acessar os clientes e serviços cadastrados pelo admin
  Para realizar os atendimentos da barbearia

  Contexto:
    Dado que existe um admin cadastrado com email "admin.vinculo@barbearia.com" e senha "senha123" salvo como "adminVinculo"
    E que estou autenticado como "admin.vinculo@barbearia.com" com senha "senha123" e role "ADMIN"

  Cenário: Barbeiro deve visualizar clientes cadastrados pelo admin
    Dado que existe um cliente criado com nome "Cliente do Admin", email "clienteadmin@email.com" e telefone "11999990001"
    E que estou autenticado como barbeiro "barbeiro.acesso@barbearia.com" com senha "senha123" vinculado ao admin com id "adminVinculo"
    Quando eu listo os clientes
    Então o status da resposta deve ser 200
    E a resposta deve ser uma lista

  Cenário: Barbeiro deve visualizar serviços cadastrados pelo admin
    Dado que existe um serviço criado com nome "Corte do Admin", preço 35 e duração 30 minutos
    E que estou autenticado como barbeiro "barbeiro.servicos@barbearia.com" com senha "senha123" vinculado ao admin com id "adminVinculo"
    Quando eu listo os serviços
    Então o status da resposta deve ser 200
    E a resposta deve ser uma lista

  Cenário: Barbeiro deve visualizar apenas os agendamentos direcionados a ele
    Dado que existe um barbeiro criado com nome "Barbeiro Agend Acesso", especialidade "Corte" e comissão 20 salvo como "barbeiroPerfilAcesso"
    E que existe um cliente de agendamento com nome "Cliente Agend Barbeiro", email "agend.barbeiro.acesso@email.com" e telefone "11988880011"
    E que existe um serviço de agendamento com nome "Serviço Agend Barbeiro", preço 40 e duração 30 minutos
    E que existe um agendamento para o barbeiro "barbeiroPerfilAcesso" criado para o cliente e serviço de agendamento em "2026-12-20T10:00:00"
    E que estou autenticado como barbeiro "barbeiro.agend.acesso@barbearia.com" com senha "senha123" vinculado ao barbeiro com id "barbeiroPerfilAcesso" e admin com id "adminVinculo"
    Quando eu listo os agendamentos
    Então o status da resposta deve ser 200
    E a resposta deve ser uma lista

  Cenário: Barbeiro não deve poder criar clientes
    E que estou autenticado como barbeiro "barbeiro.restrict@barbearia.com" com senha "senha123" vinculado ao admin com id "adminVinculo"
    Quando eu crio um cliente com nome "Cliente Proibido", email "proibido@email.com" e telefone "11900000001"
    Então o status da resposta deve ser 403

  Cenário: Barbeiro não deve poder criar agendamentos
    E que estou autenticado como barbeiro "barbeiro.restrict2@barbearia.com" com senha "senha123" vinculado ao admin com id "adminVinculo"
    Quando eu tento criar um agendamento sem dados válidos
    Então o status da resposta deve ser 403

