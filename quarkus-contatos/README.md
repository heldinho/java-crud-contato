# quarkus-contatos

CRUD de cadastro de contatos com **Quarkus 3.40.1** (Java 17, Maven Wrapper, H2 em memória).

## Como rodar

> macOS (JDK 17+, `JAVA_HOME`, `chmod +x mvnw`): passo a passo no [README da raiz](../README.md#como-rodar-no-macos).

```bash
./mvnw quarkus:dev        # modo dev com live reload e Dev UI em /q/dev-ui
```

ou em modo "produção" local:

```bash
./mvnw clean package
java -jar target/quarkus-app/quarkus-run.jar
```

- Front-end: http://localhost:8081/
- Swagger UI: http://localhost:8081/swagger-ui
- OpenAPI: http://localhost:8081/openapi
- Health: http://localhost:8081/q/health
- Dev UI (modo dev): http://localhost:8081/q/dev-ui

> A porta `8081` foi escolhida para rodar lado a lado com a versão Spring Boot.

## Testes

```bash
./mvnw test          # 17 testes (@QuarkusTest)
```

| Classe | Tipo | O que cobre |
|---|---|---|
| `ContatoServiceTest` | unitário (`@InjectMock`) | normalização de e-mail, e-mail duplicado, busca e remoção |
| `ContatoRepositoryTest` | integração (`@QuarkusTest`) | busca por nome/e-mail, paginação, restrição única |
| `ContatoResourceTest` | integração (`@QuarkusTest` + REST Assured) | 201/400/404/409/204, payload de erro, filtro `q`, paginação inválida |

> **Nota sobre transações em teste:** as requisições HTTP do REST Assured são processadas em outra thread e, portanto, em outra transação. Por isso os dados de teste são gravados com `QuarkusTransaction.requiringNew()` (transação já confirmada) e a base é limpa em `@BeforeEach`.

## Endpoints

| Método | Rota | Resposta |
|---|---|---|
| `GET` | `/api/contatos?page=0&size=10&q=ana` | `200` lista paginada |
| `GET` | `/api/contatos/{id}` | `200` / `404` |
| `POST` | `/api/contatos` | `201` + header `Location` / `400` / `409` |
| `PUT` | `/api/contatos/{id}` | `200` / `400` / `404` / `409` |
| `DELETE` | `/api/contatos/{id}` | `204` / `404` |

### Exemplos

```bash
curl -s http://localhost:8081/api/contatos
curl -s "http://localhost:8081/api/contatos?q=ana"

curl -i -X POST http://localhost:8081/api/contatos \
  -H 'Content-Type: application/json' \
  -d '{"nome":"Ana Souza","email":"ana@example.com","telefone":"+55 11 98888-0001"}'

curl -i -X PUT http://localhost:8081/api/contatos/1 \
  -H 'Content-Type: application/json' \
  -d '{"nome":"Ana Souza Alterada","email":"ana@example.com"}'

curl -i -X DELETE http://localhost:8081/api/contatos/1
```

## Estrutura

```
src/main/java/com/example/contatos
├── ContatosApplication.java        # @Singleton + @OpenAPIDefinition
├── config
│   ├── AppConfig.java              # @ConfigMapping tipado (app.seed.enabled)
│   └── SeedContatos.java           # @Observes StartupEvent
├── domain/Contato.java             # entidade JPA
├── dto
│   ├── ApiError.java
│   ├── ContatoRequest.java         # record + Bean Validation
│   ├── ContatoResponse.java
│   └── PaginaResponse.java
├── error
│   ├── ApiErrors.java              # fábrica do payload de erro
│   ├── ContatoNaoEncontradoMapper.java    # @Provider ExceptionMapper -> 404
│   ├── EmailDuplicadoMapper.java           # -> 409
│   ├── ErroGenericoMapper.java             # -> 500
│   ├── JsonInvalidoMapper.java             # -> 400
│   ├── RequisicaoInvalidaMapper.java       # -> 400
│   ├── ValidacaoMapper.java                # ConstraintViolation -> 400
│   └── *Exception.java
├── repository/ContatoRepository.java # PanacheRepository + queries HQL
├── service/ContatoService.java       # @ApplicationScoped + @Transactional
└── web/ContatoResource.java          # @Path("/api/contatos")
```

## Configuração (`application.properties`)

| Propriedade | Valor | Descrição |
|---|---|---|
| `quarkus.http.port` | `8081` | porta HTTP |
| `quarkus.datasource.jdbc.url` | `jdbc:h2:mem:contatos` | banco em memória (definir a URL desliga o Dev Services/Docker) |
| `quarkus.hibernate-orm.database.generation` | `drop-and-create` | recria o schema a cada start |
| `app.seed.enabled` | `true` | insere 3 contatos de exemplo no start (`false` nos testes) |
| `quarkus.swagger-ui.always-include` | `true` | UI do Swagger também fora do modo dev |

## Notas de implementação

- Repositório no padrão *repository* (`PanacheRepository`), com as queries HQL encapsuladas nele — espelha o `JpaRepository` do Spring.
- `PanacheQuery.page(Page.of(page, size))` + `count()`/`pageCount()` para montar a paginação (na API 3.x `Page` não é mais genérica).
- `@Transactional` (Jakarta Transactions) em cada método de escrita do serviço.
- Configuração tipada com `@ConfigMapping` em vez de `@Value`.
- E-mail normalizado para minúsculas e com restrição única no banco → duplicidade vira `409`.
- Recurso REST em `src/main/resources/META-INF/resources/` (convenção Quarkus para estáticos).

## Extras possíveis

- `./mvnw package -Dnative` gera executável nativo (requer GraalVM/Mandrel)
- `quarkus-smallrye-openapi` já está habilitado; dá para adicionar `quarkus-smallrye-jwt` para autenticação
