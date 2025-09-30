# Organização Mottu (Java / Spring Boot)

Aplicação backend (com UI simples em Thymeleaf) para gestão de Usuários, Funcionários, Motos e Endereços da Organização Mottu.  
O objetivo principal é organizar e disponibilizar de forma consistente os dados que futuramente serão integrados com outras soluções (C# / Front‑end / IoT).

---

## Sumário

1. Visão Geral  
2. Principais Funcionalidades  
3. Stack Tecnológica  
4. Arquitetura & Modelagem  
5. Endpoints REST (Usuários)  
6. Interface Web (Thymeleaf)  
7. Exemplos de JSON (Funcionário e Usuário)  
8. Requisitos  
9. Como Executar o Projeto  
10. Configuração de Banco (Oracle)  
11. Cache (Spring Cache)  
12. Tratamento de Erros e Validações  
13. Problemas Comuns (FAQ)  
14. Docker (Build / Run)  
15. Estrutura de Pastas (Resumo)  
16. Contribuindo  
17. Desenvolvedores  
18. Licença  

---

## 1. Visão Geral

Este repositório (DockerJava) contém a aplicação Java Spring Boot que se conecta a um banco Oracle para realizar operações de CRUD sobre entidades principais:

- `Usuario`
- `Funcionario`
- `Endereco`
- `Moto`

Inclui:
- API REST (JSON)
- Interface Web simples para cadastro/edição/listagem (Thymeleaf + CSS custom)
- Validação de entrada com Bean Validation
- HATEOAS (em alguns endpoints)
- Cache de consultas para otimizar leituras (ex.: lista de usuários)

---

## 2. Principais Funcionalidades

| Recurso | Funcionalidades |
|---------|-----------------|
| Usuários | Criar, listar, buscar por CPF, editar (UI), excluir |
| Funcionários | Criar, listar, (demais operações podem ser expandidas) |
| Moto | Associada a Usuário (OneToOne ou ManyToOne, conforme regra de negócio adotada) |
| Endereço | Associado a Usuário e/ou Funcionário (pode ser compartilhado – cuidado com *cascade remove*) |
| UI | Formulário responsivo + listagem com ações (Editar / Excluir) |
| Validação | Regex para CPF, placa, NIV, motor, senha, etc. |
| Cache | `@Cacheable` para `findAll` e `findById` de Usuário |
| Feedback | Mensagens flash na UI para sucesso em criar/atualizar/deletar |
| Tratamento de Integridade | Retorno amigável quando ocorrer ORA-02292 (FK) |

---

## 3. Stack Tecnológica

- Java 17+ (confirme a versão usada no seu ambiente)
- Spring Boot (Web, Data JPA, Validation, Thymeleaf, Cache)
- Oracle Database (jdbc driver ojdbc11)
- HikariCP
- Maven
- Lombok
- Docker (Dockerfile incluso)
- Thymeleaf para UI
- HTML/CSS (custom, dark/light com `prefers-color-scheme`)

---

## 4. Arquitetura & Modelagem (Visão Simplificada)

Entidades principais:

```
Usuario (CD_CPF PK)
 ├─ Endereco (NR_CEP FK) [OneToOne - sem cascade REMOVE se Endereco for compartilhado]
 └─ Moto (CD_PLACA FK)   [OneToOne; cuidado com CascadeType.ALL se houver compartilhamento]
 
Funcionario (ID_FUNCIONARIO PK)
 └─ Endereco (NR_CEP FK) [ManyToOne]
```

Observações de modelagem:
- Se **Moto** puder ser usada por vários usuários, mude o relacionamento para `@ManyToOne`.
- Evite `cascade = CascadeType.ALL` em relacionamentos onde a entidade dependente é compartilhada (gera ORA-02292 ao deletar).
- Utilize `LocalDate` em vez de `java.util.Date` onde possível (facilita conversões).

---

## 5. Endpoints REST (Usuários)

Base: `http://localhost:8080`

| Método | Endpoint | Descrição | Corpo |
|--------|----------|-----------|-------|
| GET    | `/usuarios` | Retorna recurso HATEOAS com links | - |
| GET    | `/usuarios/todos` | Lista todos os usuários (pode ser cacheado) | - |
| GET    | `/usuarios/{cpf}` | Busca usuário por CPF | - |
| POST   | `/usuarios/cadastro` | Cria novo usuário | `UsuarioDTO` |
| DELETE | `/usuarios/{cpf}` | Exclui usuário (se não houver restrições FK) | - |
| PUT / POST* | `/usuarios/{cpf}` ou rota custom de atualização (se implementada) | Atualiza dados | `UsuarioDTO` |

(*) Atualização via UI usa rota: `/usuarios/ui/{cpf}/atualizar` (POST).

**Status comuns:**
- 201 Created (cadastro)
- 200 OK (consulta / delete bem-sucedido)
- 400 Bad Request (validação)
- 404 Not Found (CPF inexistente)
- 409 Conflict (violação de integridade – sugerido em tratamento de delete)
- 500 Internal Server Error (exceção não tratada / conversões)

---

## 6. Interface Web (Thymeleaf)

| Página | Rota | Função |
|--------|------|--------|
| Lista de Usuários | `/usuarios/ui` | Listagem + ações |
| Novo Usuário | `/usuarios/ui/novo` | Form de criação |
| Editar Usuário | `/usuarios/ui/{cpf}/editar` | Form preenchido |
| Salvar (create) | `POST /usuarios/ui` | Processa formulário |
| Atualizar | `POST /usuarios/ui/{cpf}/atualizar` | Processa edição |
| Deletar (fallback) | `POST /usuarios/ui/{cpf}/deletar` | Exclusão via form |
| Deletar (AJAX) | `DELETE /usuarios/{cpf}` | Exclusão via fetch JS |

Feedback visual:
- Mensagens de sucesso exibidas como barra verde (somem após timeout JS).
- Mensagens de erro de validação exibidas abaixo dos campos.

---

## 7. Exemplos de JSON

### 7.1. Funcionário (Cadastro)

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

### 7.2. Usuário (Cadastro com Endereço e Moto)

```json
{
  "cpf": "12345678909",
  "endereco": {
    "cep": 20140702,
    "pais": "Brasil",
    "estado": "SP",
    "cidade": "Rio de Janeiro",
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

### Validações Importantes

| Campo | Regra |
|-------|-------|
| CPF | 11 dígitos (aceita com ou sem pontuação em alguns DTOs) |
| Placa | Formato antigo `AAA9999` ou Mercosul `AAA9A99` |
| NIV | 17 caracteres sem I, O, Q |
| Motor | 2–3 letras + 4–8 dígitos |
| Renavam | 7 a 11 dígitos (>= 1.000.000) |
| Data | Formato ISO `yyyy-MM-dd` |
| Senha (Funcionário) | Mín. 6 chars, contém maiúscula, minúscula e número |

---

## 8. Requisitos

- Java (JDK 17 ou superior)
- Maven 3.8+
- Docker (opcional, para empacotamento)
- Banco Oracle acessível
- Postman / Insomnia (testes) – opcional
- Git

---

## 9. Como Executar o Projeto

```bash
# 1. Clonar
git clone https://github.com/Challenge-Mottu-2025/DockerJava.git
cd DockerJava

# 2. (Opcional) Configurar variáveis de ambiente para credenciais
export ORACLE_USER=...
export ORACLE_PASS=...

# 3. Ajustar application.properties (ou application-local.properties)
# 4. Build
mvn clean package

# 5. Rodar
mvn spring-boot:run
# ou
java -jar target/*.jar
```

Aplicação por padrão: `http://localhost:8080`

---

## 10. Configuração de Banco (Oracle)

Exemplo de `application.properties` (adicione / ajuste conforme necessário):

```properties
spring.datasource.url=jdbc:oracle:thin:@//HOST:PORT/SERVICE
spring.datasource.username=${ORACLE_USER}
spring.datasource.password=${ORACLE_PASS}
spring.datasource.driver-class-name=oracle.jdbc.OracleDriver

spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# Cache
spring.cache.type=simple

# Thymeleaf (opcional)
spring.thymeleaf.cache=false
```

> Atenção: `ddl-auto=none` assume que o schema já existe. Se quiser gerar, use `update` (não recomendado em produção).

---

## 11. Cache (Spring Cache)

Implementação básica em `UsuarioCachingService`:

- `@Cacheable("usuariosFindAll")` para lista
- `@Cacheable(value = "usuariosFindById", key = "#cpf")` para item
- Invalidação manual adicionada via `@CacheEvict(allEntries = true)` em método `limparCache()`, chamado após create/update/delete.

Se perceber dados defasados, certifique-se de que:
- `@EnableCaching` está presente na classe principal
- Chamadas de invalidação estão sendo feitas após mutações

---

## 12. Tratamento de Erros e Validações

| Situação | Código | Observação |
|----------|--------|------------|
| CPF não encontrado | 404 | Mensagem textual |
| Violação FK (ORA-02292) | 409 (recomendado) | Quando implementado try/catch em delete |
| Validação Bean (ex.: regex) | 400 | Lista de mensagens / primeira mensagem |
| Exceção não tratada | 500 | Ajustar handler global futuramente |

Sugestão futura: criar `@ControllerAdvice` para padronizar payload de erros.

---

## 13. Problemas Comuns (FAQ)

| Problema | Causa | Solução |
|----------|-------|---------|
| ORA-02292 ao deletar Usuário | Cascade tentando remover Moto/Endereco compartilhado | Remover CascadeType.REMOVE / ALL onde não exclusivo |
| Data não aparecendo no form | Conversão `java.sql.Date.toInstant()` (Unsupported) | Usar `LocalDate` ou `sqlDate.toLocalDate()` |
| Mensagem verde sempre exibida | `msg` injetada vazia no model | Remover `@ModelAttribute("msg")` no GET e condicionar no template |
| Placa inválida | Regex não casou | Ver formato (Antigo vs Mercosul) |
| Cache não atualiza | Falta de eviction | Chamar `limparCache()` ou desativar cache em dev |

---

## 14. Docker

### Build da imagem

(Se existir um `Dockerfile` – ajustar comandos conforme o conteúdo real.)

```bash
mvn clean package -DskipTests
docker build -t mottu-app:latest .
```

### Executar

```bash
docker run -e ORACLE_USER=... -e ORACLE_PASS=... -p 8080:8080 mottu-app:latest
```

Se precisar montar um arquivo de configuração diferente:

```bash
docker run -v $(pwd)/config/application.properties:/app/config/application.properties \
  -e SPRING_CONFIG_LOCATION=classpath:/application.properties,file:/app/config/application.properties \
  -p 8080:8080 mottu-app:latest
```

---

## 15. Estrutura de Pastas (Resumo)

```
src/
 └─ main
     ├─ java/br/com/fiap/mottu
     │   ├─ controllers       # REST + UI (Thymeleaf)
     │   ├─ dto               # DTO / Form objects
     │   ├─ models            # Entidades JPA
     │   ├─ repositories      # Spring Data JPA
     │   ├─ service           # Serviços (cache, regras)
     │   └─ MottuApplication  # Classe principal (@SpringBootApplication)
     └─ resources
         ├─ templates/usuarios # list.html / form.html
         ├─ static/css         # Arquivos CSS custom
         ├─ static/js          # JS (ex.: list.js para deletar via fetch)
         └─ application.properties
```

---

## 16. Contribuindo

1. Crie uma branch: `git checkout -b feature/nome-da-feature`
2. Faça commits claros
3. Rode testes (quando existirem) e lint
4. Abra Pull Request descrevendo mudanças
5. Aguarde revisão

Boas práticas futuras:
- Adicionar testes unitários / integração
- Adicionar Docker Compose (Oracle XE + app)
- Implementar autenticação / segurança
- Padronizar respostas de erro (JSON consistente)

---

## 17. Desenvolvedores

| Nome | Contato |
|------|---------|
| João Broggine | [LinkedIn](https://www.linkedin.com/in/joaobroggine/) • joaovitorbrogginelopes@gmail.com |
| João Vitor Cândido | [LinkedIn](https://www.linkedin.com/in/jvictor0507/) |

---

## 18. Licença

(Defina a licença do projeto. Ex.: MIT, Apache 2.0 ou “Uso acadêmico interno”.)

Exemplo:

```
Este projeto é de uso educacional e interno para o Challenge Mottu 2025.  
Defina aqui a licença apropriada antes de uso comercial.
```

---

## Referências

- [Spring Boot](https://spring.io/projects/spring-boot)
- [Thymeleaf](https://www.thymeleaf.org/)
- [Bean Validation (Jakarta)](https://beanvalidation.org/)
- [Oracle JDBC Driver](https://www.oracle.com/database/technologies/appdev/jdbc.html)

---

> Dúvidas ou sugestões? Abra uma issue ou entre em contato com os desenvolvedores listados.
