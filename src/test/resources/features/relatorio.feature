# language: pt

Funcionalidade: Geração de Relatórios
  Como um usuário autenticado
  Quero gerar relatórios de agendamentos e financeiros
  Para monitorar as operações da barbearia

  Contexto:
    Dado que estou autenticado como "relatorio_user@barbearia.com" com senha "senha123" e role "ADMIN"
    E que existe um cliente de relatório com nome "Cliente Rel", email "rel.cliente@email.com" e telefone "11933333333"
    E que existe um barbeiro de relatório com nome "Barbeiro Rel", especialidade "Corte" e comissão 20
    E que existe um serviço de relatório com nome "Corte Rel", preço 50 e duração 30 minutos

  Cenário: Gerar relatório de agendamentos sem filtros
    Dado que existe um agendamento de relatório criado para o cliente, barbeiro e serviço em "2026-11-10T10:00:00"
    Quando eu gero o relatório de agendamentos sem filtros
    Então o status da resposta deve ser 200
    E o campo "total" da resposta deve ser maior que 0

  Cenário: Gerar relatório de agendamentos com filtro de datas
    Dado que existe um agendamento de relatório criado para o cliente, barbeiro e serviço em "2026-11-11T10:00:00"
    Quando eu gero o relatório de agendamentos com dataInicio "2026-11-01" e dataFim "2026-11-30"
    Então o status da resposta deve ser 200

  Cenário: Gerar relatório de agendamentos com dataInicio maior que dataFim
    Quando eu gero o relatório de agendamentos com dataInicio "2026-12-31" e dataFim "2026-01-01"
    Então o status da resposta deve ser 422

  Cenário: Gerar relatório financeiro sem filtros
    Dado que existe um agendamento de relatório criado para o cliente, barbeiro e serviço em "2026-11-12T10:00:00"
    E que existe um pagamento de relatório para o agendamento criado com valor 50 e forma "PIX"
    Quando eu gero o relatório financeiro sem filtros
    Então o status da resposta deve ser 200
    E o campo "quantidadePagamentos" da resposta deve ser maior que 0

  Cenário: Gerar relatório financeiro com filtro de datas
    Dado que existe um agendamento de relatório criado para o cliente, barbeiro e serviço em "2026-11-13T10:00:00"
    E que existe um pagamento de relatório para o agendamento criado com valor 50 e forma "DINHEIRO"
    Quando eu gero o relatório financeiro com dataInicio "2026-11-01" e dataFim "2026-11-30"
    Então o status da resposta deve ser 200

  Cenário: Gerar relatório financeiro com dataInicio maior que dataFim
    Quando eu gero o relatório financeiro com dataInicio "2026-12-31" e dataFim "2026-01-01"
    Então o status da resposta deve ser 422

  Cenário: Relatório de agendamentos deve ter campo totalPorStatus
    Dado que existe um agendamento de relatório criado para o cliente, barbeiro e serviço em "2026-11-14T10:00:00"
    Quando eu gero o relatório de agendamentos sem filtros
    Então o status da resposta deve ser 200
    E a resposta deve conter o campo "totalPorStatus"
