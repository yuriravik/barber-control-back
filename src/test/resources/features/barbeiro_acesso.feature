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
