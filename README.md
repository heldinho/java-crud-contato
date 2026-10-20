# CRUD de Contatos — Spring Boot 3 e Quarkus 3

Mesmo domínio (cadastro de contatos), **duas implementações independentes** para comparação direta de idiomas, arquitetura e tooling.

| Projeto | Stack | Porta | Build |
|---|---|---|---|
| [`springboot-contatos/`](springboot-contatos) | Spring Boot 3.5.16 · Spring MVC · Spring Data JPA · Validation · H2 | `8080` | `./mvnw spring-boot:run` |
| [`quarkus-contatos/`](quarkus-contatos) | Quarkus 3.40.1 · RESTEasy Reactive · Hibernate ORM Panache · Validation · H2 | `8081` | `./mvnw quarkus:dev` |

> Portas diferentes de propósito: dá para subir as duas APIs lado a lado e chamar com o mesmo `curl`.

## Requisitos

- **JDK 17 ou superior** (ambos os projetos compilam com `maven.compiler.release=17`)
- **Nada mais**: cada projeto traz o **Maven Wrapper** (`./mvnw`), que baixa o Maven automaticamente

```bash
java -version   # deve ser 17+
```

## Como rodar no macOS

### 1. Garanta um JDK 17 ou superior

```bash
java -version                 # deve retornar 17+
/usr/libexec/java_home -V     # lista os JDKs instalados no sistema
```

Sem JDK instalado (Homebrew):

```bash
brew install openjdk@17              # instala o JDK 17 (Apple Silicon e Intel)
# ou
brew install --cask temurin@17        # OpenJDK do Adoptium
```

### 2. Aponte o `JAVA_HOME` para o JDK 17

O macOS usa `zsh` como shell padrão (Catalina ou superior). O `mvnw` lê o JDK do `JAVA_HOME` — sem isso ele pode acabar usando um Java antigo do `/usr/bin/java`.

```bash
echo 'export JAVA_HOME=$(/usr/libexec/java_home -v 17)' >> ~/.zshrc
echo 'export PATH="$JAVA_HOME/bin:$PATH"'               >> ~/.zshrc
source ~/.zshrc
```

Confira qual JDK o Maven está usando:

```bash
./mvnw -v    # deve apontar para o Java 17+
```

### 3. Dê permissão de execução ao Maven Wrapper

O bit de execução pode se perder ao clonar ou copiar o projeto entre discos/volumes:

```bash
chmod +x springboot-contatos/mvnw quarkus-contatos/mvnw
```

### 4. Rode uma das APIs

A partir da raiz do repositório, escolha **um** dos dois (não precisam rodar juntos):

```bash
cd springboot-contatos && ./mvnw spring-boot:run    # sobe em http://localhost:8080
```

```bash
cd quarkus-contatos && ./mvnw quarkus:dev            # sobe em http://localhost:8081
```

Abra o navegador direto do terminal:

```bash
open http://localhost:8080     # versão Spring Boot
open http://localhost:8081     # versão Quarkus
```

> **Primeira execução:** o wrapper baixa o Maven (~10 MB) e as dependências (~200 MB) para `~/.m2`. As execuções seguintes usam o cache e sobem em segundos.
> Para encerrar o servidor: `Ctrl + C` no terminal.

### 5. Rodar os testes

```bash
cd springboot-contatos && ./mvnw test
cd quarkus-contatos    && ./mvnw test
```

### Problemas comuns no macOS

| Sintoma | Causa | Solução |
|---|---|---|
| `zsh: command not found: java` | JDK não instalado | `brew install openjdk@17` e abra um novo terminal |
| `JAVA_HOME is not defined correctly` | `JAVA_HOME` aponta para um caminho inexistente | `export JAVA_HOME=$(/usr/libexec/java_home -v 17)` |
| `Permission denied: ./mvnw` | bit de execução perdido na cópia | `chmod +x mvnw` |
| `Address already in use` (porta 8080/8081) | outra aplicação usando a porta | `lsof -i :8080` para identificar e finalize o processo; ou mude `server.port` (Spring) / `quarkus.http.port` (Quarkus) |
| `Unsupported class file major version` | Maven compilando com Java < 17 | exporte o `JAVA_HOME` do passo 2 e valide com `./mvnw -v` |
| `Killed` durante o build | memória insuficiente | feche outros processos ou rode `./mvnw -T 1C package` |
| Compilação muito lenta em Apple Silicon | JDK x86 rodando via Rosetta | instale o JDK arm64 (`brew install openjdk@17`) |

## Subindo as duas APIs

```bash
# terminal 1
cd springboot-contatos && ./mvnw spring-boot:run

# terminal 2
cd quarkus-contatos && ./mvnw quarkus:dev
```

| Recurso | Spring Boot | Quarkus |
|---|---|---|
| Front-end (HTML/JS) | http://localhost:8080/ | http://localhost:8081/ |
| Swagger UI | http://localhost:8080/swagger-ui.html | http://localhost:8081/swagger-ui |
| OpenAPI JSON | http://localhost:8080/v3/api-docs | http://localhost:8081/openapi |
| Health | http://localhost:8080/actuator/health | http://localhost:8081/q/health |

## Docker Compose (as duas APIs + PostgreSQL)

Sobe as duas APIs e um PostgreSQL 16 em containers, com banco persistente em volume:

```bash
cp .env.example .env      # opcional: ajuste portas, credenciais e seed
docker compose up -d --build
docker compose ps
```

| Serviço | URL / Porta | Healthcheck |
|---|---|---|
| Spring Boot | http://localhost:8080 | `/actuator/health` |
| Quarkus | http://localhost:8081 | `/q/health` |
| PostgreSQL | `localhost:5432` (`contatos` / `contatos`) | `pg_isready` |

Comandos úteis:

```bash
docker compose logs -f springboot quarkus   # logs das aplicações
docker compose exec postgres psql -U contatos -d contatos_spring
docker compose down                         # para e remove containers (mantém o volume)
docker compose down -v                      # remove também o volume do Postgres
```

### Como funciona

- **Bancos isolados**: o `db/init/01-create-databases.sql` cria `contatos_spring` e `contatos_quarkus`, para que cada API possa recriar o schema sem colidir na tabela `contatos`. O script só roda na **primeira** inicialização do volume — use `down -v` para reexecutá-lo.
- **Sem mexer no código**: o `docker-compose.yml` sobrescreve o H2 em memória por variáveis de ambiente (`SPRING_DATASOURCE_*` e `QUARKUS_DATASOURCE_*`). Rodando local com `./mvnw`, o H2 continua sendo o padrão.
- **Quarkus fixa o driver em build time**: por isso o `Dockerfile` compila com `-Dquarkus.datasource.db-kind=postgresql`. Sem essa flag a imagem sobe e falha com `Driver does not support the provided URL`.
- **Imagens multi-stage**: `maven:3.9-eclipse-temurin-17` compila, `eclipse-temurin:17-jre-jammy` executa como usuário não-root (`uid 1001`). O JVM roda como PID 1 e recebe o `SIGTERM` do `docker stop` (shutdown gracioso).
- **Sem Flyway**: o schema é criado pelo próprio Hibernate (`ddl-auto=update` / `database.generation=update`). Em produção, versione o schema com Flyway/Liquibase.

## Contrato da API (idêntico nas duas implementações)

| Método | Rota | Descrição | Sucesso |
|---|---|---|---|
| `GET` | `/api/contatos?page=0&size=10&q=ana` | Lista paginada; `q` filtra por nome/e-mail | `200` |
| `GET` | `/api/contatos/{id}` | Busca por id | `200` |
| `POST` | `/api/contatos` | Cria contato | `201` + `Location` |
| `PUT` | `/api/contatos/{id}` | Atualiza contato | `200` |
| `DELETE` | `/api/contatos/{id}` | Remove contato | `204` |

Payload de criação/atualização:

```json
{ "nome": "Ana Souza", "email": "ana@example.com", "telefone": "+55 11 98888-0001" }
```

Erros seguem o mesmo formato nas duas versões:

```json
{
  "timestamp": "2026-01-01T12:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "dados inválidos",
  "path": "/api/contatos",
  "fieldErrors": { "email": "email inválido" }
}
```

Códigos: `400` (validação/parâmetros), `404` (não encontrado), `409` (e-mail já cadastrado).

## Comparativo: como cada framework resolve o mesmo problema

| Camada | Spring Boot 3 | Quarkus 3 |
|---|---|---|
| HTTP | `@RestController` + `@RequestMapping` (Spring MVC) | `@Path`/`@GET`/`@POST` (RESTEasy Reactive) |
| Resposta | `ResponseEntity<T>` | `jakarta.ws.rs.core.Response` |
| Repositório | `interface ContatoRepository extends JpaRepository` | `class ContatoRepository implements PanacheRepository` |
| Transação | `@Transactional(readOnly = true)` no serviço | `@Transactional` (Jakarta TX) |
| Bean/escopo | `@Service`, `@Component` | `@ApplicationScoped` |
| Validação | `spring-boot-starter-validation` (`@Valid` + `@RestControllerAdvice`) | `quarkus-hibernate-validator` (`@Valid` + `ExceptionMapper`) |
| Tratamento de erro | `@RestControllerAdvice` | `@Provider ExceptionMapper<T>` |
| Configuração | `application.properties` + `@ConditionalOnProperty` | `application.properties` + `@ConfigMapping` tipado |
| Startup / seed | `CommandLineRunner` | `@Observes StartupEvent` |
| Testes de API | `@SpringBootTest` + MockMvc | `@QuarkusTest` + REST Assured |
| Testes unitários | JUnit 5 + Mockito (`@ExtendWith(MockitoExtension)`) | JUnit 5 + Mockito (`@InjectMock`) |
| OpenAPI | `springdoc-openapi` | `quarkus-smallrye-openapi` |

### Diferenças de comportamento que valem lembrar

1. **Estáticos no Quarkus** ficam em `src/main/resources/META-INF/resources/`; no Spring ficam em `src/main/resources/static/`.
2. **`/api/contatos/abc`** (id não numérico): Spring devolve `400`, Quarkus devolve `404` (conversão de `@PathParam`).
3. **Ponto de partida do build**: Spring sobe com Tomcat embarcado; Quarkus sobe em modo dev com *live reload* e Quarkus Dev UI (`/q/dev-ui`).
4. **Produção**: o Quarkus gera executável nativo com `native` profile; o Spring gera jar executável. Nenhum dos dois está configurado para produção aqui.

## Testes

```bash
cd springboot-contatos && ./mvnw test   # 17 testes
cd quarkus-contatos    && ./mvnw test   # 17 testes
```

Cobertura: camada de serviço (regras de negócio, normalização de e-mail, conflito de duplicidade), repositório (busca, paginação, restrição única) e API HTTP (status codes, payload de erro, 404/409).

## Banco de dados

Ambos usam **H2 em memória** para desenvolvimento (sem instalação de banco). Para apontar para outro banco, troque a URL em `application.properties` e o driver:

- Spring Boot: `spring.datasource.url` + `spring.datasource.driver-class-name`
- Quarkus: `quarkus.datasource.jdbc.url` + `quarkus.datasource.db-kind` (e adicione a extensão, ex.: `quarkus-jdbc-postgresql`)

O driver do PostgreSQL já está declarado nos dois `pom.xml` (`org.postgresql:postgresql` e `quarkus-jdbc-postgresql`), então o [Docker Compose](#docker-compose-as-duas-apis--postgresql) sobe com persistência real sem alterar código. Lembre que no Quarkus o `db-kind` é resolvido em **build time**: para rodar a imagem com PostgreSQL, compile com `-Dquarkus.datasource.db-kind=postgresql`.

## Melhorias possíveis

- Persistência de auditoria (`@CreatedBy`) e soft delete
- Filtros por intervalo de datas e ordenação configurável
- `Flyway`/`Liquibase` para versionar o schema
- Autenticação/autorização (Spring Security vs Quarkus Security)
- Testes de contrato compartilhados (REST Assured + WireMock) para garantir paridade entre as duas implementações
