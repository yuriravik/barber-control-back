# Barber Control — Backend

Backend REST API para controle financeiro e operacional de barbearias. Gerencia usuários (admins, secretárias e barbeiros), clientes, serviços, agendamentos e pagamentos com autenticação JWT e controle de acesso por papel (RBAC).

---

## Sumário

- [Tecnologias](#tecnologias)
- [Arquitetura](#arquitetura)
- [Estrutura do Projeto](#estrutura-do-projeto)
- [Banco de Dados](#banco-de-dados)
- [Segurança e Permissões](#segurança-e-permissões)
- [Endpoints da API](#endpoints-da-api)
  - [Usuários](#usuários----usuarios)
  - [Barbeiros](#barbeiros----barbeiros)
  - [Clientes](#clientes----clientes)
  - [Serviços](#serviços----servicos)
  - [Agendamentos](#agendamentos----agendamentos)
  - [Pagamentos](#pagamentos----pagamentos)
  - [Dashboard](#dashboard----dashboard)
  - [Relatórios](#relatórios----relatorios)
- [Configuração e Variáveis de Ambiente](#configuração-e-variáveis-de-ambiente)
- [Como Executar](#como-executar)
- [Testes](#testes)
- [Coleções de API](#coleções-de-api)

---

## Tecnologias

| Tecnologia | Versão | Uso |
|---|---|---|
| Java | 17 | Linguagem principal |
| Spring Boot | 3.5.11 | Framework principal |
| Spring Security | 6+ | Autenticação e autorização |
| Spring Data JPA | — | Acesso ao banco de dados |
| JJWT | 0.12.6 | Geração e validação de tokens JWT |
| PostgreSQL | — | Banco de dados em produção |
| H2 | — | Banco de dados em memória (dev/testes) |
| Flyway | — | Migrações de banco de dados |
| Lombok | — | Redução de boilerplate |
| Gradle | 8.14.4 | Build e gerenciamento de dependências |
| JUnit 5 | — | Testes unitários e de integração |
| Cucumber | 7.18.0 | Testes BDD |
| Instancio | 5.5.1 | Geração de dados para testes |
| JaCoCo | — | Cobertura de código |

---

## Arquitetura

O projeto segue uma arquitetura em camadas inspirada em **Clean Architecture**:

```
br.com.ravikyu.barbercontrol/
├── application/      → Casos de uso (services, DTOs, mappers)
│   ├── agendamento/
│   ├── barbeiro/
│   ├── cliente/
│   ├── pagamento/
│   ├── servico/
│   └── usuario/
├── domain/           → Entidades de domínio, enums e interfaces de repositório
│   ├── model/
│   └── repository/
└── infrastructure/   → Adaptadores externos (persistência, web, segurança)
    ├── persistence/  → Entidades JPA, implementações de repositório e mappers
    ├── security/     → Filtros JWT, provedores e configuração Spring Security
    └── web/          → Controllers REST e tratamento global de exceções
```

---

## Estrutura do Projeto

```
barber-control-back/
├── src/
│   ├── main/
│   │   ├── java/                          ← Código-fonte principal
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── application-dev.properties
│   │       ├── application-prod.properties
│   │       └── db/migration/              ← Scripts Flyway (V1, V2, V3)
│   └── test/
│       ├── java/                          ← Testes unitários, integração e BDD
│       └── resources/
│           └── application.properties     ← Config de testes (H2)
├── build.gradle
├── gradlew / gradlew.bat
├── barber-control.postman_collection.json
└── barber-control.insomnia_collection.json
```

---

## Banco de Dados

### Diagrama de Tabelas

```
usuarios
  ├── id            UUID  PK
  ├── email         VARCHAR  UNIQUE  NOT NULL
  ├── senha         VARCHAR  NOT NULL  (bcrypt)
  ├── role          VARCHAR  NOT NULL  (ADMIN | BARBEIRO | SECRETARIA)
  ├── admin_id      UUID  FK→ usuarios.id  (nullable)
  └── barbeiro_id   UUID  (nullable)

clientes
  ├── id            UUID  PK
  ├── nome          VARCHAR  NOT NULL
  ├── email         VARCHAR  NOT NULL
  ├── telefone      VARCHAR  (nullable)
  └── usuario_id    UUID  FK→ usuarios.id  (nullable)

barbeiros
  ├── id                    UUID  PK
  ├── nome                  VARCHAR  NOT NULL
  ├── especialidade         VARCHAR  (nullable)
  ├── percentual_comissao   NUMERIC(5,2)  NOT NULL
  ├── ativo                 BOOLEAN  NOT NULL  (default: true)
  └── usuario_id            UUID  FK→ usuarios.id  (nullable)

servicos
  ├── id                UUID  PK
  ├── nome              VARCHAR  NOT NULL
  ├── descricao         VARCHAR
  ├── preco             NUMERIC(10,2)  NOT NULL
  ├── duracao_minutos   INTEGER  (nullable)
  ├── ativo             BOOLEAN  NOT NULL  (default: true)
  └── usuario_id        UUID  FK→ usuarios.id  (nullable)

agendamentos
  ├── id                UUID  PK
  ├── cliente_id        UUID  FK→ clientes.id  NOT NULL
  ├── barbeiro_id       UUID  FK→ barbeiros.id  NOT NULL
  ├── servico_id        UUID  FK→ servicos.id  NOT NULL
  ├── data_hora_inicio  TIMESTAMP  NOT NULL
  ├── data_hora_fim     TIMESTAMP  (nullable)
  └── status            VARCHAR  NOT NULL  (AGENDADO | CANCELADO | CONCLUIDO)

pagamentos
  ├── id                UUID  PK
  ├── agendamento_id    UUID  FK→ agendamentos.id  UNIQUE  NOT NULL
  ├── valor             NUMERIC(10,2)  NOT NULL
  ├── forma_pagamento   VARCHAR  NOT NULL  (PIX | CARTAO | DINHEIRO)
  ├── status            VARCHAR  NOT NULL  (PENDENTE | PAGO | CANCELADO)
  └── pago_em           TIMESTAMP  (nullable)
```

### Enumerações

| Enum | Valores |
|---|---|
| `Role` | `ADMIN`, `BARBEIRO`, `SECRETARIA` |
| `StatusAgendamento` | `AGENDADO`, `CANCELADO`, `CONCLUIDO` |
| `StatusPagamento` | `PENDENTE`, `PAGO`, `CANCELADO` |
| `FormaPagamento` | `PIX`, `CARTAO`, `DINHEIRO` |

### Migrações Flyway

| Script | Descrição |
|---|---|
| `V1__create_initial_tables.sql` | Criação das tabelas iniciais |
| `V2__add_usuario_id_to_tables.sql` | Adiciona referências de usuário às tabelas |
| `V3__add_admin_id_to_usuarios.sql` | Adiciona referência de admin para multi-tenancy |

---

## Segurança e Permissões

### Autenticação

A autenticação é baseada em **JWT (JSON Web Token)** com algoritmo HMAC-SHA256. Todas as requisições autenticadas devem incluir o header:

```
Authorization: Bearer <token>
```

O token é obtido no endpoint `POST /usuarios/login` e tem validade padrão de **24 horas**.

### Papéis (Roles)

| Papel | Descrição |
|---|---|
| `ADMIN` | Proprietário da barbearia. Acesso completo ao sistema. |
| `SECRETARIA` | Funcionária administrativa. Gerencia clientes e agendamentos. |
| `BARBEIRO` | Profissional da barbearia. Acesso de leitura e registro de pagamentos. |

### Matriz de Permissões

| Recurso / Operação | ADMIN | SECRETARIA | BARBEIRO |
|---|:---:|:---:|:---:|
| Cadastrar usuário ADMIN | ✅ (público) | ❌ | ❌ |
| Cadastrar funcionário (BARBEIRO/SECRETARIA) | ✅ | ❌ | ❌ |
| Visualizar próprio perfil (`/me`) | ✅ | ✅ | ✅ |
| Criar / Deletar barbeiro | ✅ | ❌ | ❌ |
| Atualizar barbeiro | ✅ | ❌ | ❌ |
| Desativar barbeiro | ✅ | ❌ | ❌ |
| Listar barbeiros | ✅ | ✅ | ✅ |
| Criar / Deletar serviço | ✅ | ❌ | ❌ |
| Atualizar serviço | ✅ | ❌ | ❌ |
| Listar / Visualizar serviço | ✅ | ✅ | ✅ |
| Criar / Deletar cliente | ✅ | ✅ | ❌ |
| Atualizar cliente | ✅ | ✅ | ❌ |
| Listar / Visualizar cliente | ✅ | ✅ | ✅ |
| Criar / Deletar agendamento | ✅ | ✅ | ❌ |
| Concluir agendamento | ✅ | ✅ | ✅ (próprio) |
| Cancelar agendamento | ✅ | ✅ | ❌ |
| Listar agendamentos | ✅ (todos) | ✅ (do admin) | ✅ (próprios) |
| Registrar pagamento | ✅ | ✅ | ✅ |
| Listar / Visualizar pagamento | ✅ | ✅ | ✅ |
| Dashboard | ✅ | ✅ | ✅ |
| Relatórios de agendamentos e financeiros | ✅ | ✅ | ✅ |

> **Multi-tenancy**: cada ADMIN possui seu próprio espaço de dados. Secretárias e barbeiros vinculados a um ADMIN só acessam dados desse ADMIN.

---

## Endpoints da API

> **Base URL**: `http://localhost:8080`  
> Endpoints protegidos requerem o header `Authorization: Bearer <token>`.

---

### Usuários — `/usuarios`

#### `POST /usuarios/cadastrar` — Público
Cria um novo usuário do tipo **ADMIN**.

**Request Body**:
```json
{
  "email": "admin@barbearia.com",
  "senha": "senha123"
}
```

**Response `201 Created`**:
```json
{
  "id": "uuid",
  "email": "admin@barbearia.com",
  "role": "ADMIN",
  "adminId": null,
  "barbeiroId": null
}
```

---

#### `POST /usuarios/login` — Público
Autentica o usuário e retorna um token JWT.

**Request Body**:
```json
{
  "email": "admin@barbearia.com",
  "senha": "senha123"
}
```

**Response `200 OK`**:
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "tipo": "Bearer"
}
```

---

#### `POST /usuarios/cadastrar-funcionario` — 🔒 ADMIN
Cria um funcionário (`BARBEIRO` ou `SECRETARIA`) vinculado ao ADMIN autenticado.

**Request Body**:
```json
{
  "email": "barbeiro@barbearia.com",
  "senha": "senha123",
  "role": "BARBEIRO",
  "barbeiroId": "uuid-opcional"
}
```

**Response `201 Created`**: `UsuarioResponse`

---

#### `GET /usuarios/me` — 🔒 ADMIN, SECRETARIA, BARBEIRO
Retorna os dados do usuário autenticado.

**Response `200 OK`**:
```json
{
  "id": "uuid",
  "email": "usuario@barbearia.com",
  "role": "BARBEIRO",
  "adminId": "uuid-do-admin",
  "barbeiroId": "uuid-do-barbeiro"
}
```

---

### Barbeiros — `/barbeiros`

#### `POST /barbeiros` — 🔒 ADMIN
Cria um novo perfil de barbeiro.

**Request Body**:
```json
{
  "nome": "João Silva",
  "especialidade": "Corte e Barba",
  "percentualComissao": 40.0
}
```

**Response `201 Created`**:
```json
{
  "id": "uuid",
  "nome": "João Silva",
  "especialidade": "Corte e Barba",
  "percentualComissao": 40.0,
  "ativo": true
}
```

---

#### `GET /barbeiros` — 🔒 ADMIN, SECRETARIA, BARBEIRO
Lista todos os barbeiros do ADMIN autenticado.

**Response `200 OK`**: `List<BarbeiroResponse>`

---

#### `PATCH /barbeiros/{id}/desativar` — 🔒 ADMIN
Desativa um barbeiro (soft delete).

**Response `204 No Content`**

---

#### `PUT /barbeiros/{id}` — 🔒 ADMIN
Atualiza os dados de um barbeiro.

**Request Body**:
```json
{
  "nome": "João Silva",
  "especialidade": "Corte e Barba",
  "percentualComissao": 45.0
}
```

**Response `200 OK`**: `BarbeiroResponse`

---

### Clientes — `/clientes`

#### `POST /clientes` — 🔒 ADMIN, SECRETARIA
Cria um novo cliente.

**Request Body**:
```json
{
  "nome": "Maria Oliveira",
  "email": "maria@email.com",
  "telefone": "11999999999"
}
```

**Response `201 Created`**:
```json
{
  "id": "uuid",
  "nome": "Maria Oliveira",
  "email": "maria@email.com",
  "telefone": "11999999999"
}
```

---

#### `GET /clientes` — 🔒 ADMIN, SECRETARIA, BARBEIRO
Lista todos os clientes.

**Response `200 OK`**: `List<ClienteResponse>`

---

#### `GET /clientes/{id}` — 🔒 ADMIN, SECRETARIA, BARBEIRO
Retorna um cliente pelo ID.

**Response `200 OK`**: `ClienteResponse`

---

#### `DELETE /clientes/{id}` — 🔒 ADMIN, SECRETARIA
Remove um cliente.

**Response `204 No Content`**

---

#### `PUT /clientes/{id}` — 🔒 ADMIN, SECRETARIA
Atualiza os dados de um cliente.

**Request Body**:
```json
{
  "nome": "Maria Oliveira",
  "email": "maria@email.com",
  "telefone": "11988888888"
}
```

**Response `200 OK`**: `ClienteResponse`

---

### Serviços — `/servicos`

#### `POST /servicos` — 🔒 ADMIN
Cria um novo serviço oferecido pela barbearia.

**Request Body**:
```json
{
  "nome": "Corte Degradê",
  "descricao": "Corte moderno com máquina",
  "preco": 45.00,
  "duracaoMinutos": 45
}
```

**Response `201 Created`**:
```json
{
  "id": "uuid",
  "nome": "Corte Degradê",
  "descricao": "Corte moderno com máquina",
  "preco": 45.00,
  "duracaoMinutos": 45,
  "ativo": true
}
```

---

#### `GET /servicos` — 🔒 ADMIN, SECRETARIA, BARBEIRO
Lista todos os serviços.

**Response `200 OK`**: `List<ServicoResponse>`

---

#### `GET /servicos/{id}` — 🔒 ADMIN, SECRETARIA, BARBEIRO
Retorna um serviço pelo ID.

**Response `200 OK`**: `ServicoResponse`

---

#### `DELETE /servicos/{id}` — 🔒 ADMIN
Remove um serviço.

**Response `204 No Content`**

---

#### `PUT /servicos/{id}` — 🔒 ADMIN
Atualiza os dados de um serviço.

**Request Body**:
```json
{
  "nome": "Corte Degradê",
  "descricao": "Corte moderno com máquina",
  "preco": 50.00,
  "duracaoMinutos": 50
}
```

**Response `200 OK`**: `ServicoResponse`

---

### Agendamentos — `/agendamentos`

#### `POST /agendamentos` — 🔒 ADMIN, SECRETARIA
Cria um novo agendamento.

**Request Body**:
```json
{
  "clienteId": "uuid",
  "barbeiroId": "uuid",
  "servicoId": "uuid",
  "dataHora": "2025-06-15T10:00:00"
}
```

**Response `201 Created`**:
```json
{
  "id": "uuid",
  "cliente": { "id": "uuid", "nome": "Maria Oliveira" },
  "barbeiro": { "id": "uuid", "nome": "João Silva" },
  "servico": { "id": "uuid", "nome": "Corte Degradê" },
  "dataHoraInicio": "2025-06-15T10:00:00",
  "dataHoraFim": "2025-06-15T10:45:00",
  "status": "AGENDADO"
}
```

---

#### `GET /agendamentos` — 🔒 ADMIN, SECRETARIA, BARBEIRO
Lista agendamentos. A visibilidade depende do papel:
- **ADMIN**: todos os agendamentos de seus barbeiros
- **SECRETARIA**: agendamentos do ADMIN ao qual está vinculada
- **BARBEIRO**: apenas seus próprios agendamentos

**Response `200 OK`**: `List<AgendamentoResponse>`

---

#### `GET /agendamentos/{id}` — 🔒 ADMIN, SECRETARIA, BARBEIRO
Retorna um agendamento pelo ID.

**Response `200 OK`**: `AgendamentoResponse`

---

#### `DELETE /agendamentos/{id}` — 🔒 ADMIN, SECRETARIA
Remove um agendamento.

**Response `204 No Content`**

---

#### `PATCH /agendamentos/{id}/concluir` — 🔒 ADMIN, SECRETARIA, BARBEIRO
Marca um agendamento como concluído. O status atual deve ser `AGENDADO`. Barbeiros só podem concluir seus próprios agendamentos.

**Response `204 No Content`**

---

#### `PATCH /agendamentos/{id}/cancelar` — 🔒 ADMIN, SECRETARIA
Cancela um agendamento. O status atual deve ser `AGENDADO`.

**Response `204 No Content`**

---

### Pagamentos — `/pagamentos`

#### `POST /pagamentos` — 🔒 ADMIN, SECRETARIA, BARBEIRO
Registra um pagamento para um agendamento.

**Request Body**:
```json
{
  "agendamentoId": "uuid",
  "valor": 45.00,
  "formaPagamento": "PIX"
}
```

> Formas de pagamento aceitas: `PIX`, `CARTAO`, `DINHEIRO`

**Response `201 Created`**:
```json
{
  "id": "uuid",
  "agendamentoId": "uuid",
  "valor": 45.00,
  "formaPagamento": "PIX",
  "status": "PAGO"
}
```

---

#### `GET /pagamentos` — 🔒 ADMIN, SECRETARIA, BARBEIRO
Lista todos os pagamentos.

**Response `200 OK`**: `List<PagamentoResponse>`

---

#### `GET /pagamentos/{id}` — 🔒 ADMIN, SECRETARIA, BARBEIRO
Retorna um pagamento pelo ID.

**Response `200 OK`**: `PagamentoResponse`

---

### Dashboard — `/dashboard`

#### `GET /dashboard` — 🔒 ADMIN, SECRETARIA, BARBEIRO
Retorna um resumo do sistema com dados do usuário autenticado e os registros acessíveis (clientes, barbeiros, serviços e agendamentos).

**Response `200 OK`**:
```json
{
  "usuario": { "id": "uuid", "email": "admin@barbearia.com", "role": "ADMIN" },
  "clientes": [...],
  "barbeiros": [...],
  "servicos": [...],
  "agendamentos": [...]
}
```

---

### Relatórios — `/relatorios`

#### `GET /relatorios/agendamentos` — 🔒 ADMIN, SECRETARIA, BARBEIRO
Gera relatório de agendamentos com filtros opcionais.

**Query params** (todos opcionais):
| Parâmetro | Tipo | Descrição |
|---|---|---|
| `dataInicio` | `YYYY-MM-DD` | Data inicial do período |
| `dataFim` | `YYYY-MM-DD` | Data final do período |
| `barbeiroId` | UUID | Filtrar por barbeiro |
| `servicoId` | UUID | Filtrar por serviço |

**Response `200 OK`**: `RelatorioAgendamentoResponse`

---

#### `GET /relatorios/financeiro` — 🔒 ADMIN, SECRETARIA, BARBEIRO
Gera relatório financeiro com totais por forma de pagamento e resumo por barbeiro.

**Query params** (todos opcionais):
| Parâmetro | Tipo | Descrição |
|---|---|---|
| `dataInicio` | `YYYY-MM-DD` | Data inicial do período |
| `dataFim` | `YYYY-MM-DD` | Data final do período |

**Response `200 OK`**:
```json
{
  "totalRecebido": 1350.00,
  "quantidadePagamentos": 30,
  "totalPorFormaPagamento": { "PIX": 800.00, "CARTAO": 400.00, "DINHEIRO": 150.00 },
  "resumoPorBarbeiro": [...],
  "pagamentos": [...]
}
```

---

### Respostas de Erro

| Código HTTP | Situação |
|---|---|
| `400 Bad Request` | Dados de entrada inválidos (validação) |
| `401 Unauthorized` | Token ausente ou inválido |
| `403 Forbidden` | Sem permissão para o recurso |
| `404 Not Found` | Recurso não encontrado |
| `409 Conflict` | Conflito de agendamento (`AgendamentoException`) |
| `422 Unprocessable Entity` | Regra de negócio violada (`BusinessException`) |

---

## Configuração e Variáveis de Ambiente

### Perfis disponíveis

| Perfil | Banco de dados | Uso |
|---|---|---|
| `dev` (padrão) | H2 em memória | Desenvolvimento local |
| `prod` | PostgreSQL | Produção |

### Variáveis de ambiente (produção)

| Variável | Obrigatório | Padrão | Descrição |
|---|:---:|---|---|
| `SPRING_PROFILES_ACTIVE` | ✅ | `dev` | Perfil ativo (`dev` ou `prod`) |
| `DATABASE_URL` | ✅ | `jdbc:postgresql://localhost:5432/barbercontrol` | URL de conexão PostgreSQL |
| `DATABASE_USERNAME` | ✅ | `barbercontrol` | Usuário do banco |
| `DATABASE_PASSWORD` | ✅ | — | Senha do banco |
| `JWT_SECRET` | ✅ | — | Chave secreta JWT (mínimo 256 bits) |
| `JWT_EXPIRATION` | ❌ | `86400000` | Expiração do token em milissegundos (24h) |

---

## Como Executar

### Pré-requisitos

- **Java 17+**
- **Gradle** (ou utilize o wrapper `./gradlew` incluído no projeto)
- **PostgreSQL** (somente para perfil `prod`)

### Perfil de desenvolvimento (H2 em memória)

```bash
./gradlew bootRun
```

A aplicação iniciará em `http://localhost:8080` com banco H2 em memória.

O console H2 estará disponível em `http://localhost:8080/h2-console`:
- **JDBC URL**: `jdbc:h2:mem:barbercontrol`
- **Usuário**: `sa`
- **Senha**: *(vazia)*

### Perfil de produção (PostgreSQL)

```bash
export SPRING_PROFILES_ACTIVE=prod
export DATABASE_URL=jdbc:postgresql://localhost:5432/barbercontrol
export DATABASE_USERNAME=barbercontrol
export DATABASE_PASSWORD=sua_senha
export JWT_SECRET=sua-chave-secreta-de-pelo-menos-256-bits

./gradlew bootRun
```

### Build do projeto

```bash
# Build completo
./gradlew clean build

# Apenas compilação
./gradlew assemble
```

O JAR gerado estará em `build/libs/barbercontrol-0.0.1-SNAPSHOT.jar`.

---

## Testes

### Executar todos os testes

```bash
./gradlew test
```

### Gerar relatório de cobertura (JaCoCo)

```bash
./gradlew test jacocoTestReport
```

Relatório disponível em: `build/reports/jacoco/test/html/index.html`

### Tipos de testes

| Tipo | Tecnologia | Descrição |
|---|---|---|
| Unitários | JUnit 5 + Instancio | Testes de serviços e regras de negócio |
| Integração | Spring Boot Test + H2 | Testes de controllers com banco em memória |
| BDD | Cucumber 7 | Cenários de comportamento (`.feature` files) |

> **Nota**: os testes de integração utilizam `TestRestTemplate` e requerem locale `en_US` para evitar conflitos de parsing do Cucumber com locale `pt_BR`. Isso já está configurado no `build.gradle`.

---

## Coleções de API

O repositório inclui coleções prontas para testar a API:

- **Postman**: `barber-control.postman_collection.json`
- **Insomnia**: `barber-control.insomnia_collection.json`

Importe o arquivo na ferramenta de sua preferência e configure a variável `baseUrl` como `http://localhost:8080`.

---

## Licença

Este projeto está sob uso privado. Todos os direitos reservados.
