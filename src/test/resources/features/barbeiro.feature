# language: pt

Funcionalidade: Gerenciamento de Barbeiros
  Como um usuário autenticado
  Quero gerenciar os barbeiros da barbearia
  Para organizar as comissões e atendimentos

  Contexto:
    Dado que estou autenticado como "barbeiros_user@barbearia.com" com senha "senha123" e role "ADMIN"

  Cenário: Criar um novo barbeiro com sucesso
    Quando eu crio um barbeiro com nome "Carlos Barbeiro", especialidade "Corte Degradê" e percentual de comissão 30.00
    Então o status da resposta deve ser 201
    E o campo "nome" da resposta deve ser "Carlos Barbeiro"
    E o campo "ativo" da resposta deve ser "true"

  Cenário: Criar barbeiro sem especialidade
    Quando eu crio um barbeiro com nome "Barbeiro Simples" e percentual de comissão 25.00 sem especialidade
    Então o status da resposta deve ser 201
    E o campo "nome" da resposta deve ser "Barbeiro Simples"

  Cenário: Não deve criar barbeiro sem nome
    Quando eu crio um barbeiro sem nome com percentual de comissão 20.00
    Então o status da resposta deve ser 400

  Cenário: Não deve criar barbeiro com percentual de comissão negativo
    Quando eu crio um barbeiro com nome "Inválido", especialidade "Nenhuma" e percentual de comissão -1.00
    Então o status da resposta deve ser 400

  Cenário: Não deve criar barbeiro com percentual de comissão acima de 100
    Quando eu crio um barbeiro com nome "Inválido", especialidade "Nenhuma" e percentual de comissão 101.00
    Então o status da resposta deve ser 400

  Cenário: Listar barbeiros do usuário autenticado
    Dado que existe um barbeiro criado com nome "Barbeiro Listagem", especialidade "Barba" e comissão 20.00
    Quando eu listo os barbeiros
    Então o status da resposta deve ser 200
    E a resposta deve ser uma lista

  Cenário: Desativar barbeiro com sucesso
    Dado que existe um barbeiro criado com nome "Barbeiro Desativar", especialidade "Corte" e comissão 15.00
    Quando eu desativo o barbeiro pelo ID criado
    Então o status da resposta deve ser 204
