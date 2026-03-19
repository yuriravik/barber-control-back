# language: pt

Funcionalidade: Gerenciamento de Serviços
  Como um usuário autenticado
  Quero gerenciar os serviços oferecidos pela barbearia
  Para controlar preços e durações

  Contexto:
    Dado que estou autenticado como "servicos_user@barbearia.com" com senha "senha123" e role "ADMIN"

  Cenário: Criar um novo serviço com sucesso
    Quando eu crio um serviço com nome "Corte Simples", descrição "Corte de cabelo simples", preço 35 e duração 30 minutos
    Então o status da resposta deve ser 201
    E o campo "nome" da resposta deve ser "Corte Simples"
    E o campo "ativo" da resposta deve ser "true"

  Cenário: Criar serviço sem descrição
    Quando eu crio um serviço com nome "Serviço Básico", preço 20 e duração 20 minutos sem descrição
    Então o status da resposta deve ser 201
    E o campo "nome" da resposta deve ser "Serviço Básico"
    E o campo "ativo" da resposta deve ser "true"

  Cenário: Não deve criar serviço sem nome
    Quando eu crio um serviço sem nome com preço 30 e duração 30 minutos
    Então o status da resposta deve ser 400

  Cenário: Não deve criar serviço com preço zero
    Quando eu crio um serviço com nome "Grátis", descrição "Teste", preço 0 e duração 30 minutos
    Então o status da resposta deve ser 400

  Cenário: Não deve criar serviço com duração zero
    Quando eu crio um serviço com nome "Teste", descrição "Teste", preço 30 e duração 0 minutos
    Então o status da resposta deve ser 400

  Cenário: Listar serviços do usuário autenticado
    Dado que existe um serviço criado com nome "Serviço Listagem", preço 45 e duração 45 minutos
    Quando eu listo os serviços
    Então o status da resposta deve ser 200
    E a resposta deve ser uma lista

  Cenário: Buscar serviço por ID com sucesso
    Dado que existe um serviço criado com nome "Serviço Busca", preço 50 e duração 60 minutos
    Quando eu busco o serviço pelo ID criado
    Então o status da resposta deve ser 200
    E o campo "nome" da resposta deve ser "Serviço Busca"

  Cenário: Não deve encontrar serviço com ID inexistente
    Quando eu busco o serviço com ID "00000000-0000-0000-0000-000000000099"
    Então o status da resposta deve ser 404

  Cenário: Deletar serviço com sucesso
    Dado que existe um serviço criado com nome "Serviço Delete", preço 25 e duração 15 minutos
    Quando eu deleto o serviço pelo ID criado
    Então o status da resposta deve ser 204
