# language: pt

Funcionalidade: Gerenciamento de Usuários
  Como um usuário do sistema
  Quero me cadastrar e autenticar
  Para poder acessar as funcionalidades protegidas

  Cenário: Cadastrar um novo usuário com sucesso
    Quando eu cadastro um usuário com email "admin@barbearia.com", senha "senha123" e role "ADMIN"
    Então o status da resposta deve ser 201
    E o campo "email" da resposta deve ser "admin@barbearia.com"

  Cenário: Não deve cadastrar usuário com email já existente
    Dado que existe um usuário cadastrado com email "duplicado@barbearia.com" e senha "senha123" e role "ADMIN"
    Quando eu cadastro um usuário com email "duplicado@barbearia.com", senha "outrasenha" e role "ADMIN"
    Então o status da resposta deve ser 422

  Cenário: Não deve cadastrar usuário com email inválido
    Quando eu cadastro um usuário com email "emailinvalido", senha "senha123" e role "ADMIN"
    Então o status da resposta deve ser 400

  Cenário: Não deve cadastrar usuário com role inválida
    Quando eu cadastro um usuário com email "teste@barbearia.com", senha "senha123" e role "INVALIDA"
    Então o status da resposta deve ser 400

  Cenário: Fazer login com sucesso
    Dado que existe um usuário cadastrado com email "login@barbearia.com" e senha "senha123" e role "ADMIN"
    Quando eu faço login com email "login@barbearia.com" e senha "senha123"
    Então o status da resposta deve ser 200
    E a resposta deve conter um token JWT

  Cenário: Não deve fazer login com credenciais inválidas
    Dado que existe um usuário cadastrado com email "valido@barbearia.com" e senha "senha123" e role "ADMIN"
    Quando eu faço login com email "valido@barbearia.com" e senha "senhaerrada"
    Então o status da resposta deve ser 422

  Cenário: Não deve fazer login com usuário inexistente
    Quando eu faço login com email "inexistente@barbearia.com" e senha "senha123"
    Então o status da resposta deve ser 404

  Cenário: Cadastrar barbeiro vinculado a um admin com sucesso
    Dado que existe um admin cadastrado com email "adminsteps@barbearia.com" e senha "senha123" salvo como "admin"
    Quando eu cadastro um barbeiro com email "barbeirosteps@barbearia.com", senha "senha123" e adminId "admin"
    Então o status da resposta deve ser 201
    E o campo "role" da resposta deve ser "BARBEIRO"

  Cenário: Não deve cadastrar barbeiro sem adminId
    Quando eu cadastro um barbeiro com email "barbeiro.semadmin@barbearia.com", senha "senha123" sem adminId
    Então o status da resposta deve ser 422

  Cenário: Cadastrar secretaria vinculada a um admin com sucesso
    Dado que existe um admin cadastrado com email "admin.sec@barbearia.com" e senha "senha123" salvo como "adminSec"
    Quando eu cadastro uma secretaria com email "secretaria@barbearia.com", senha "senha123" e adminId "adminSec"
    Então o status da resposta deve ser 201
    E o campo "role" da resposta deve ser "SECRETARIA"

  Cenário: Não deve cadastrar secretaria sem adminId
    Quando eu cadastro uma secretaria com email "secretaria.semadmin@barbearia.com", senha "senha123" sem adminId
    Então o status da resposta deve ser 422
