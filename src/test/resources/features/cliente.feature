# language: pt

Funcionalidade: Gerenciamento de Clientes
  Como um usuário autenticado
  Quero gerenciar meus clientes
  Para organizar os atendimentos da barbearia

  Contexto:
    Dado que estou autenticado como "clientes_user@barbearia.com" com senha "senha123" e role "ADMIN"

  Cenário: Criar um novo cliente com sucesso
    Quando eu crio um cliente com nome "João Silva", email "joao@email.com" e telefone "11999999999"
    Então o status da resposta deve ser 201
    E o campo "nome" da resposta deve ser "João Silva"
    E o campo "email" da resposta deve ser "joao@email.com"

  Cenário: Não deve criar cliente sem nome
    Quando eu crio um cliente sem nome, com email "sem.nome@email.com" e telefone "11999999999"
    Então o status da resposta deve ser 400

  Cenário: Não deve criar cliente com email inválido
    Quando eu crio um cliente com nome "Maria", email "emailinvalido" e telefone "11999999999"
    Então o status da resposta deve ser 400

  Cenário: Listar clientes do usuário autenticado
    Dado que existe um cliente criado com nome "Cliente Listado", email "listado@email.com" e telefone "11988888888"
    Quando eu listo os clientes
    Então o status da resposta deve ser 200
    E a resposta deve ser uma lista

  Cenário: Buscar cliente por ID com sucesso
    Dado que existe um cliente criado com nome "Cliente Busca", email "busca@email.com" e telefone "11977777777"
    Quando eu busco o cliente pelo ID criado
    Então o status da resposta deve ser 200
    E o campo "nome" da resposta deve ser "Cliente Busca"

  Cenário: Não deve encontrar cliente com ID inexistente
    Quando eu busco o cliente com ID "00000000-0000-0000-0000-000000000099"
    Então o status da resposta deve ser 404

  Cenário: Deletar cliente com sucesso
    Dado que existe um cliente criado com nome "Cliente Delete", email "delete@email.com" e telefone "11966666666"
    Quando eu deleto o cliente pelo ID criado
    Então o status da resposta deve ser 204
