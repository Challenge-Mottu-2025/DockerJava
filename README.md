# Organização Mottu – Backend Java (Spring Boot)

> Plataforma backend para gestão de Usuários, Funcionários, Motos e Endereços, com autenticação (Spring Security), versionamento de schema (Flyway), UI simples (Thymeleaf) e integração Oracle 19c.

<p align="center">
  <img src="https://img.shields.io/badge/Java-21-orange.svg" />
  <img src="https://img.shields.io/badge/Spring%20Boot-3.4.5-6DB33F.svg" />
  <img src="https://img.shields.io/badge/Database-Oracle%2019c-red.svg" />
  <img src="https://img.shields.io/badge/Flyway-10.x-aa2222.svg" />
  <img src="https://img.shields.io/badge/Security-Spring%20Security-blue.svg" />
  <img src="https://img.shields.io/badge/Build-Maven-044A53.svg" />
  <img src="https://img.shields.io/badge/License-Educational-lightgrey.svg" />
</p>

---

## Sumário

1. [Visão Geral](#1-visão-geral)  
2. [Principais Funcionalidades](#2-principais-funcionalidades)  
3. [Stack Tecnológica](#3-stack-tecnológica)  
4. [Arquitetura & Domínio](#4-arquitetura--domínio)  
5. [Autenticação & Autorização](#5-autenticação--autorização)  
6. [Fluxo de Login (UI Padrão Spring)](#6-fluxo-de-login-ui-padrão-spring)  
7. [Controle de Versões de Banco (Flyway)](#7-controle-de-versões-de-banco-flyway)  
8. [Seeds & Dados Iniciais](#8-seeds--dados-iniciais)  
9. [Endpoints REST Principais](#9-endpoints-rest-principais)  
10. [Interface Web (Thymeleaf)](#10-interface-web-thymeleaf)  
11. [Exemplos de Payload (JSON)](#11-exemplos-de-payload-json)  
12. [Variáveis de Ambiente & Configuração](#12-variáveis-de-ambiente--configuração)  
13. [Execução (Dev / Produção / Docker)](#13-execução-dev--produção--docker)  
14. [Cache](#14-cache)  
15. [Boas Práticas de Validação & Regras de Negócio](#15-boas-práticas-de-validação--regras-de-negócio)  
16. [Tratamento de Erros](#16-tratamento-de-erros)  
17. [FAQ (Problemas Comuns)](#17-faq-problemas-comuns)  
18. [Estrutura de Pastas](#18-estrutura-de-pastas)  
19. [Roadmap / Próximas Evoluções](#19-roadmap--próximas-evoluções)  
20. [Contribuindo](#20-contribuindo)  
21. [Desenvolvedores](#21-desenvolvedores)  
22. [Licença](#22-licença)  
23. [Referências](#23-referências)

---

## 1. Visão Geral

Backend responsável por consolidar dados da organização Mottu e servir de base integradora com outros componentes (C#, IoT, front web externo).  
Inclui autenticação baseada em Funcionários, autorização por roles e schema versionado via Flyway.

Entidades centrais:
- `Usuario`
- `Funcionario`
- `Moto`
- `Endereco`
- `Role` (segurança)

---

## 2. Principais Funcionalidades

| Categoria        | Recursos |
|------------------|----------|
| Usuários         | CRUD + busca por CPF + UI de listagem/edição |
| Funcionários     | Autenticação, associação a roles, seed inicial |
| Segurança        | Spring Security + BCrypt + restrição por ROLE |
| Motos            | Vínculo com Usuário (ajustável p/ regras futuras) |
| Endereços        | Compartilháveis — cuidado com cascades destrutivos |
| Versionamento DB | Flyway (migrações incrementais + conversões de tipo) |
| UI               | Thymeleaf minimalista / HTML5 / CSS custom |
| Cache            | Spring Cache para listas e consultas repetidas |
| Validação        | Bean Validation + Regex específicas (CPF, placa, NIV etc.) |
| Observabilidade  | Logs estruturados, (futuro: actuator ampliado) |

---

## 3. Stack Tecnológica

| Tecnologia | Uso |
|------------|-----|
| Java 21 | Plataforma principal |
| Spring Boot 3.4.x | Starter (Web, Security, Data JPA, Validation, Thymeleaf, Cache) |
| Oracle 19c | Banco de dados |
| Flyway | Versionamento de schema e seeds idempotentes |
| HikariCP | Pool de conexões |
| Lombok | Redução de boilerplate |
| Maven | Build / dependency management |
| Docker | Empacotamento |
| BCryptPasswordEncoder | Hash de senhas |
| Thymeleaf | UI server-side |

---

## 4. Arquitetura & Domínio

```
Usuario (CD_CPF PK) ─┬─ Endereco (NR_CEP FK)
                     └─ Moto (CD_PLACA FK)

Funcionario (ID_FUNCIONARIO PK) ─┬─ Endereco (NR_CEP FK)
                                 └─ * Roles (ManyToMany via T_MT_FUNCIONARIO_ROLE)
```

Principais diretrizes:
- CPF tratado como `String` (preserva zeros) – migrações converteram de NUMBER → VARCHAR2.
- Scripts Flyway convertem colunas sensíveis antes de habilitar `ddl-auto=validate`.
- Seeds escritos em PL/SQL idempotente (verificam existência antes de inserir).
- Evitar `CascadeType.REMOVE` onde um recurso pode ser referenciado por múltiplas entidades.

---

## 5. Autenticação & Autorização

- Login baseado em `Funcionario` (campo CPF utilizado como `username`).
- Senhas armazenadas com BCrypt (60 chars).
- Roles padrão semeadas:
  - `ROLE_ADMIN`
  - `ROLE_USER`
- Usuário “administrador” inicial recebe ambas as roles.

Exemplo de restrição (padrão):
- `/usuarios/ui/**` → `ROLE_ADMIN` ou `ROLE_USER`
- `/usuarios/**` (REST sensível) → apenas `ROLE_ADMIN`
- `/funcionarios/**` → apenas `ROLE_ADMIN`

---

## 6. Fluxo de Login (UI Padrão Spring)

1. Usuário não autenticado acessa rota protegida → redirecionado para `/login`.
2. Form padrão envia `POST /login` com campos `username` (CPF) e `password`.
3. Sucesso → redireciona para `/usuarios/ui` (configurável).
4. Logout em `/logout` limpa sessão e redireciona para `/login?logout`.

> Se quiser página custom, adicionar template `login.html` e substituir `.formLogin()` sem usar `.loginPage("/login")` ou fornecendo a view.

---

## 7. Controle de Versões de Banco (Flyway)

| Versão | Descrição (resumo) | Categoria |
|--------|---------------------|-----------|
| V1 | Criação inicial de tabelas base | Schema |
| V2 | Ajustes / constraints adicionais (ex.) | Schema |
| V3 | Outras dependências / normalizações | Schema |
| V4 | Seed inicial (endereços, moto, usuário, roles, funcionários) idempotente | Seed |
| V5 | Adição de colunas de auditoria (ex.) | Evolução |
| V6 | Conversão FUNCIONARIO.CD_CPF NUMBER → VARCHAR2 | Refactor |
| V7 | Conversão MOTO.CD_CPF NUMBER → VARCHAR2 | Refactor |
| V8 | Conversão USUARIO.CD_CPF NUMBER → VARCHAR2 | Refactor |
| V9+ | (Planejado) Índices, CHECK de CPF, normalizações extras | Planejado |

Boas práticas mantidas:
- Nunca editar migrações aplicadas (criar novas).
- Scripts de conversão usam coluna temporária + cópia + rename para evitar ORA-01439.
- Seeds são idempotentes (usam `SELECT COUNT(*)` antes de inserir).

---

## 8. Seeds & Dados Iniciais

Funcionário administrativo inicial (DEV):
- CPF: `99999999999`
- Senha default (se redefinida): **(definir localmente — nunca expor em produção)**

Para resetar senha via SQL (exemplo):
```sql
UPDATE T_MT_FUNCIONARIO
   SET CD_SENHA = '<HASH_BCRYPT_NOVO>'
 WHERE CD_CPF = '99999999999';
COMMIT;
```

Gerar hash:
```java
System.out.println(new BCryptPasswordEncoder().encode("NovaSenha123"));
```

> Recomenda-se criar migration de reset apenas em ambientes controlados (não commitar senhas reais).

---

## 9. Endpoints REST Principais

Base: `http://localhost:8080`

| Método | Endpoint | Descrição | Auth |
|--------|----------|-----------|------|
| GET | `/usuarios` | HATEOAS root / usuários | ROLE_USER |
| GET | `/usuarios/todos` | Lista usuários | ROLE_USER |
| GET | `/usuarios/{cpf}` | Detalhes | ROLE_USER |
| POST | `/usuarios/cadastro` | Cria usuário | ROLE_ADMIN |
| DELETE | `/usuarios/{cpf}` | Remove usuário | ROLE_ADMIN |
| (UI) POST | `/usuarios/ui/{cpf}/atualizar` | Atualização via form | ROLE_USER / ADMIN |

> Ajustar conforme ampliação de domínio (ex.: motos, funcionários).

---

## 10. Interface Web (Thymeleaf)

| Página | Rota |
|--------|------|
| Listagem de Usuários | `/usuarios/ui` |
| Novo Usuário | `/usuarios/ui/novo` |
| Editar Usuário | `/usuarios/ui/{cpf}/editar` |
| Ações de exclusão | via botão / fetch DELETE |

Feedback:
- Mensagens flash para sucesso/erro.
- Validações inline exibidas abaixo dos campos.

---

## 11. Exemplos de Payload (JSON)

### Funcionário (Cadastro)
```json
{
  "nome": "Linus Torvald",
  "cpf": "62242321222",
  "senha": "Linux71",
  "endereco": {
    "cep": 20140702,
    "pais": "Brasil",
    "estado": "SP",
    "cidade": "São Paulo",
    "bairro": "Centro",
    "numero": 50,
    "logradouro": "Av. Principal",
    "complemento": "Apto 101"
  }
}
```

### Usuário (Cadastro)
```json
{
  "cpf": "12345678909",
  "endereco": {
    "cep": 20140702,
    "pais": "Brasil",
    "estado": "SP",
    "cidade": "São Paulo",
    "bairro": "Centro",
    "numero": 100,
    "logradouro": "Av. Principal",
    "complemento": "Ap 101"
  },
  "placa": {
    "placa": "ABC1234",
    "cpf": "12345678909",
    "niv": "9BWZZZ377VT004251",
    "motor": "CG123456",
    "renavam": 12345678,
    "fipe": 9200
  },
  "dataNascimento": "1990-05-20",
  "nome": "João da Silva"
}
```

### Regras de Validação (Resumo)

| Campo | Regra |
|-------|-------|
| CPF | 11 dígitos |
| Placa | Antiga ou Mercosul |
| NIV | 17 chars sem I,O,Q |
| Motor | Prefixo letras + dígitos |
| Renavam | 7–11 dígitos |
| Senha | Mín. 6 chars + maiúscula + minúscula + número |
| Data Nascimento | ISO `yyyy-MM-dd` |

---

## 12. Variáveis de Ambiente & Configuração

Sugestão de `application.properties` base:

```properties
spring.datasource.url=jdbc:oracle:thin:@oracle.fiap.com.br:1521:ORCL
spring.datasource.username=${DB_USER:SEU_USUARIO}
spring.datasource.password=${DB_PASS:CHANGE_ME}
spring.datasource.driver-class-name=oracle.jdbc.OracleDriver

spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration

spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=false

spring.thymeleaf.cache=false
spring.cache.type=simple

logging.level.org.springframework.security=INFO
```

Profile dev (`application-dev.properties`):
```properties
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.orm.jdbc.bind=TRACE
```

Rodar com:
```
--spring.profiles.active=dev
```

---

## 13. Execução (Dev / Produção / Docker)

### Local
```bash
git clone https://github.com/Challenge-Mottu-2025/DockerJava.git
cd DockerJava
mvn clean package
mvn spring-boot:run
```

### Docker (exemplo)
```bash
mvn clean package -DskipTests
docker build -t mottu-app:latest .
docker run -e DB_USER=... -e DB_PASS=... -p 8080:8080 mottu-app:latest
```

> Caso queira Oracle XE local, considere compor com Docker Compose.

---

## 14. Cache

Anotações `@Cacheable` aplicadas em consultas de usuários.  
Invalidar após mutações (create/update/delete) com `@CacheEvict(allEntries=true)`.  
Se perceber inconsistências em dev → desabilitar cache ou limpar.

---

## 15. Boas Práticas de Validação & Regras de Negócio

- Normalizar CPF removendo caracteres não numéricos antes de persistir.
- Evitar `CascadeType.ALL` em relacionamentos reutilizáveis.
- Usar `@Transactional` em serviços que fazem múltiplas operações relacionadas.
- Sempre validar input externo (DTO separado da entidade).

---

## 16. Tratamento de Erros

| Situação | Código Sugerido | Observação |
|----------|-----------------|------------|
| Recurso não encontrado | 404 | Ex.: CPF inexistente |
| Violação de integridade | 409 | Ex.: ORA-02292 ao deletar |
| Validação Bean | 400 | Lista de erros |
| Falha de autenticação | 401 | Login inválido |
| Acesso negado | 403 | Sem role suficiente |
| Exceção não tratada | 500 | Capturar futuramente em `@ControllerAdvice` |

> Futuro: criar padrão de resposta JSON (`timestamp`, `path`, `message`, `errors`).

---

## 17. FAQ (Problemas Comuns)

| Problema | Causa | Solução |
|----------|-------|---------|
| “Bad credentials” | Hash não corresponde à senha digitada | Regenerar hash BCrypt e atualizar |
| ORA-01439 em migração | ALTER tipo direto com dados existentes | Usar estratégia coluna temporária (já aplicada) |
| 404 em `/login` | `.loginPage("/login")` sem template | Remover `loginPage()` ou criar `login.html` |
| ORA-02292 ao deletar | Cascade inadequado | Ajustar mapeamentos |
| CPF truncado | Coluna NUMBER | Converter para VARCHAR2 (feito em V6–V8) |
| Página Whitelabel erro | Sem template `/error` | Criar error.html |

---

## 18. Estrutura de Pastas

```
src/main/java/br/com/fiap/mottu
 ├─ security/            # SecurityConfig, UserDetailsService, etc.
 ├─ models/              # Entidades JPA
 ├─ repositories/        # Interfaces Spring Data
 ├─ service/             # Regras / orquestrações
 ├─ controllers/         # REST + UI (Thymeleaf)
 ├─ dto/                 # Transfer Objects / formulários
 └─ MottuApplication.java
src/main/resources
 ├─ db/migration         # Scripts Flyway (V1__... Vn__...)
 ├─ templates            # Views Thymeleaf
 ├─ static               # CSS / JS
 └─ application.properties
```

---

## 19. Roadmap / Próximas Evoluções

| Item | Prioridade | Status |
|------|------------|--------|
| Página custom de login | Média | Pendente |
| Testes unitários / integração | Alta | Pendente |
| Padronizar payload de erro | Média | Pendente |
| Auditoria (triggers + colunas) | Média | Parcial |
| Docker Compose (Oracle XE) | Média | Pendente |
| Índices adicionais (CPF / placa) | Alta | Planejado |
| Observabilidade (Actuator + métricas) | Média | Pendente |
| Rate limiting / proteções extras | Baixa | Futuro |

---

## 20. Contribuindo

1. Criar branch: `git checkout -b feature/nome`
2. Implementar / testar
3. Padronizar mensagens de commit
4. Abrir Pull Request descrevendo motivação e mudanças
5. Aguardar revisão

> Recomenda-se adicionar testes conforme novas regras de negócio forem surgindo.

---

## 21. Desenvolvedores

| Nome | Contato |
|------|---------|
| João Broggine | [LinkedIn](https://www.linkedin.com/in/joaobroggine/) • joaovitorbrogginelopes@gmail.com |
| João Vitor Cândido | [LinkedIn](https://www.linkedin.com/in/jvictor0507/) |

---

## 22. Licença

Projeto de uso educacional para o Challenge Mottu 2025.  
Definir licença formal (MIT / Apache 2.0 / interna) antes de uso comercial.

```
Este software é distribuído "como está", sem garantias explícitas ou implícitas.
```

---

## 23. Referências

- [Spring Boot](https://spring.io/projects/spring-boot)  
- [Spring Security](https://spring.io/projects/spring-security)  
- [Flyway](https://flywaydb.org/)  
- [Thymeleaf](https://www.thymeleaf.org/)  
- [Bean Validation (Jakarta)](https://beanvalidation.org/)  
- [Oracle JDBC Driver](https://www.oracle.com/database/technologies/appdev/jdbc.html)  

---

> Dúvidas, sugestões ou melhorias: abra uma issue ou entre em contato com os desenvolvedores
