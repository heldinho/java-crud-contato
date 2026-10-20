# springboot-contatos

CRUD de cadastro de contatos com **Spring Boot 3.5.16** (Java 17, Maven Wrapper, H2 em memória).

## Como rodar

> macOS (JDK 17+, `JAVA_HOME`, `chmod +x mvnw`): passo a passo no [README da raiz](../README.md#como-rodar-no-macos).

```bash
./mvnw spring-boot:run
```

- Front-end: http://localhost:8080/
- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI: http://localhost:8080/v3/api-docs
- Health: http://localhost:8080/actuator/health
- Console H2: http://localhost:8080/h2-console (JDBC URL `jdbc:h2:mem:contatos`, user `sa`, sem senha)

## Testes

```bash
./mvnw test          # unitários + integração (17 testes)
./mvnw clean package # gera target/springboot-contatos-1.0.0.jar
java -jar target/springboot-contatos-1.0.0.jar
```

| Classe | Tipo | O que cobre |
|---|---|---|
| `ContatoServiceTest` | unitário (Mockito) | normalização de e-mail, e-mail duplicado, atualização, remoção, contato inexistente |
| `ContatoRepositoryIT` | integração (`@DataJpaTest`) | busca ignorando caixa, restrição única de e-mail, query de busca |
| `ContatoControllerIT` | integração (`@SpringBootTest` + MockMvc) | 201/400/404/409/204, payload de erro, filtro `q` |

> O Surefire foi configurado para também executar classes `*IT` (por padrão ele ignora esse sufixo).

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
curl -s http://localhost:8080/api/contatos
curl -s "http://localhost:8080/api/contatos?q=ana"

curl -i -X POST http://localhost:8080/api/contatos \
  -H 'Content-Type: application/json' \
  -d '{"nome":"Ana Souza","email":"ana@example.com","telefone":"+55 11 98888-0001"}'

curl -i -X PUT http://localhost:8080/api/contatos/1 \
  -H 'Content-Type: application/json' \
  -d '{"nome":"Ana Souza Alterada","email":"ana@example.com"}'

curl -i -X DELETE http://localhost:8080/api/contatos/1
```

## Estrutura

```
src/main/java/com/example/contatos
├── ContatosApplication.java        # @SpringBootApplication
├── config
│   ├── OpenApiConfig.java          # metadados do OpenAPI
│   └── SeedContatos.java           # CommandLineRunner (dados de exemplo)
├── domain/Contato.java             # entidade JPA
├── dto
│   ├── ApiError.java               # payload de erro padronizado
│   ├── ContatoRequest.java         # record + Bean Validation
│   ├── ContatoResponse.java
│   └── PaginaResponse.java
├── error
│   ├── ContatoNaoEncontradoException.java
│   ├── EmailDuplicadoException.java
│   └── GlobalExceptionHandler.java # @RestControllerAdvice
├── repository/ContatoRepository.java
├── service/ContatoService.java     # @Service + @Transactional
└── web/ContatoController.java      # @RestController
```

## Configuração (`application.properties`)

| Propriedade | Valor | Descrição |
|---|---|---|
| `server.port` | `8080` | porta HTTP |
| `spring.datasource.url` | `jdbc:h2:mem:contatos` | banco em memória |
| `spring.jpa.hibernate.ddl-auto` | `update` | cria/atualiza o schema |
| `spring.jpa.open-in-view` | `false` | evita lazy loading fora da transação |
| `app.seed.enabled` | `true` | insere 3 contatos de exemplo no start (`false` nos testes) |

## Notas de implementação

- E-mail é normalizado para minúsculas e o campo tem **restrição única** no banco → duplicidade vira `409`.
- Datas usam `Instant` (UTC) e são preenchidas em `@PrePersist`/`@PreUpdate`.
- Erros de validação saem no formato `ApiError` com `fieldErrors` por campo.
- A camada web não fala com o repositório: sempre passa pelo serviço.
