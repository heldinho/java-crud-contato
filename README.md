# 📇 Contatos — uma "caderneta de contatos" que roda na internet

> Este projeto é um **programinha de lista de contatos**, igualzinho o app de
> contatos do seu celular. Você pode **criar**, **ver**, **alterar** e
> **apagar** contatos. Só que em vez de ficar no seu celular, ele fica num
> "servidor" (um computador ligado à internet) e qualquer pessoa com o
> endereço dele pode usar.

E tem um detalhe legal: ele foi feito **duas vezes**, com duas ferramentas
diferentes (uma do mundo **Spring Boot** e outra do mundo **Quarkus**). As duas
fazem **exatamente a mesma coisa**. Isso é de propósito: é um jeito de você
comparar os dois "Jeitos de fazer a mesma coisa" e entender como o Java funciona
por dentro.

---

## 📚 O dicionário das palavras QUE você vai ouvir

Antes de qualquer comando, let's decifrar o "gíria" do projeto. Sem isso, os
comandos abaixo parecem um feitiço.

| Palavra | O que é, em português simples | Analogia |
|---|---|---|
| **Java** | A **linguagem** em que o programa é escrito | O idioma que os computers entendem |
| **JDK** | O **kit de ferramentas** que transforma o código `.java` em algo que o computador roda | O tradutor + máquina de escrever |
| **Maven** | O programa que **baixa as bibliotecas** que o projeto usa e **organiza o build** | A lista de compras do Supermercado |
| **Maven Wrapper** (`mvnw`) | Um **script** que baixa o Maven sozinho, pra você não precisar instalar nada | O colega que vai no mercado buscar os ingredientes por você |
| **JAR** | O arquivo ZIP-ish com o programa compilado, pronto pra rodar | O lanche embalado que você leva pra escola |
| **API** | A **"comanda por mensagem"** que o programa aceita. Você pede, ele responde | O **cardápio** do restaurante: você pede pelo nome, sem entrar na cozinha |
| **HTTP** | O "protocolo de conversa" entre navegador/servidor | O idioma que o garçom e o cliente falam |
| **JSON** | O formato em que a resposta volta (parecido com um dicionário do Python) | A notinha escrita num papel quadradinho |
| **CRUD** | As **4 ações** de um sistema: **C**riar, **R** (de *Read*), **U**pdate, **D**elete | Anotar, ler, corrigir e apagar um contato |
| **Hibernate / JPA** | A biblioteca que **traduz** o Java para SQL (a linguagem dos bancos de dados) | O tradutor Java ↔ SQL |
| **H2** | Um banco de dados **temporário**, que fica **na memória** e some quando o programa desliga | Um caderninho de rascunho |
| **PostgreSQL** | Um banco de dados **de verdade**, que guarda as coisas mesmo depois de desliga | O fichário de aço do escritório |
| **Docker** | Uma forma de **empacotar** o programa com tudo que ele precisa, para rodar igual em qualquer computador | O **contêiner de carga**: leva a cozinha inteira junto |
| **Docker Compose** | Sobe **vários contêineres de uma vez** (as 2 APIs + o banco) | A **mudança** que leva a casa toda de uma vez |
| **Endpoint** | Um endereço específico que responde algo (ex.: `/api/contatos`) | O número doapartamento |
| **Teste** | Um programinha que **confere sozinho** se o código continua funcionando | Conferir a lição de casa antes de entregar |
| **Porta** | Um "número de endereço" que o computador usa para saber qual programa você quer chamar | O número do apartamento no prédio |

---

## 📦 O que tem dentro desta pasta?

```
teste-java/
│
├── 📄 README.md              ← VOCÊ ESTÁ AQUI (o manual de instruções)
├── 📄 .gitignore              ← a lista do que NÃO guardar no Git
├── 📄 .env.example            ← exemplo de configuração (pode ir pro Git)
├── 📄 docker-compose.yml      ← o "roteiro" que sobe tudo junto no Docker
│
├── 🗄️ db/init/                ← script que cria os 2 bancos no PostgreSQL
│   └── 01-create-databases.sql
│
├── 📁 springboot-contatos/    ← VERSÃO 1 (Spring Boot) — porta 8080
└── 📁 quarkus-contatos/       ← VERSÃO 2 (Quarkus)     — porta 8081
```

E dentro de cada versão, a **mesma** organização (parece com a maioria dos
projetos Java do mundo real):

```
springboot-contatos/
├── 📄 pom.xml                 ← a "lista de compras" (quais bibliotecas usar)
├── 📄 mvnw                    ← o script que baixa o Maven sozinho
├── 📄 Dockerfile              ← como montar o contêiner
├── 📄 README.md
│
├── src/main/java/...          ← 🎮 O CÓDIGO DE VERDADE
│   ├── domain/                ← 📦 O "molde" do contato
│   ├── dto/                   ← 📝 Os "papelinhos" de entrada e saída
│   ├── repository/            ← 🗄️ Quem guarda e busca no banco
│   ├── service/               ← 🧠 As regras (o "cérebro" do programa)
│   ├── web/                   ← 🚪 A porta da frente (o que responde o cliente)
│   ├── error/                 ← ❌ O que fazer quando dá errado
│   └── config/                ← ⚙️ Configurações e dados de exemplo
│
├── src/main/resources/        ← 📋 Configurações + a página HTML
├── src/test/java/...          ← 🧪 Os testes
└── target/                    ← 🗑️ LIXO: código compilado (não vai pro Git)
```

> ⚠️ A pasta `target/` pode ser apagada sem dó a qualquer momento. Ela só
> contém "cópias" do que o Maven compila. É por isso que ela está no
> `.gitignore`.

---

## 🛠️ O que você precisa instalar ANTES

Boas notícias: só **uma** coisa.

### 1. O JDK 17 (ou mais novo)

O JDK é o kit que transforma o texto do código em um programa que o computador
entende. Sem ele, o `java` não existe na sua máquina.

```bash
java -version     # tem que aparecer "17" ou maior
```

**Não tem?** No macOS, o jeito mais fácil é pelo Homebrew:

```bash
brew install openjdk@17          # ou: brew install --cask temurin@17
```

### 2. (Opcional) Docker

Só se você quiser usar a parte do PostgreSQL. Sem Docker, o programa usa o
**H2** (o caderninho de rascunho) e funciona tudo igual.

```bash
docker --version
```

### 3. Nada mais!

Não precisa instalar Maven, nem Banco de Dados, nem servidor nenhum. O
`./mvnw` (o Maven Wrapper) **baixa tudo sozinho** na primeira vez.

---

## 🚀 Bora rodar! (passo a passo)

### Passo 1 — Apontar o `JAVA_HOME`

O macOS usa um tal de `zsh` como terminal. O `mvnw` precisa saber **qual**
Java usar. Se não disser, ele pode pegar um Java antigo do sistema e reclamar.

```bash
echo 'export JAVA_HOME=$(/usr/libexec/java_home -v 17)' >> ~/.zshrc
echo 'export PATH="$JAVA_HOME/bin:$PATH"'               >> ~/.zshrc
source ~/.zshrc
```

Confira se deu certo:

```bash
./mvnw -v      # tem que mostrar "Java version: 17" (ou maior)
```

> 💡 **Dica:** toda vez que você abrir um terminal novo, o `~/.zshrc` é lido
> sozinho. Por isso a gente escreve nele — assim você não precisa repetir.

### Passo 2 — Dar permissão para rodar o `./mvnw`

Quando o projeto é copiado entre pastas ou discos, o macOS às vezes "esquece"
que o arquivo pode ser executado.

```bash
chmod +x springboot-contatos/mvnw quarkus-contatos/mvnw
```

### Passo 3 — Escolher qual versão subir

São **duas opções**. Rode **uma** de cada vez (ou as duas em terminais
separados, se quiser comparar!).

**Versão 1 — Spring Boot (porta 8080):**

```bash
cd springboot-contatos
./mvnw spring-boot:run
```

**Versão 2 — Quarkus (porta 8081):**

```bash
cd quarkus-contatos
./mvnw quarkus:dev
```

> ⏳ **Na primeira vez vai demorar um pouquinho** (1–3 minutos). É o Maven
> baixando o Maven e umas 200 MB de bibliotecas para a pasta `~/.m2`. Das
> próximas vezes em diante é só um instantinho.
>
> 🛑 Para **parar** o servidor: aperte `Ctrl + C` no terminal.

### Passo 4 — Abrir no navegador

Assim que aparecer algo parecido com `Started ... in X seconds`, o programa
está no ar:

```bash
open http://localhost:8080     # Spring Boot
open http://localhost:8081     # Quarkus
```

Você vai ver uma **páginazinha web com uma lista de contatos** — já com 3
contatos de exemplo (Ana, Bruno e Carla) inseridos automaticamente.

### Passo 5 — Brincar com a API

A **API** é o cardápio: você pede por comando, o programa entrega o JSON.

```bash
# 📋 Listar todos (com paginação)
curl http://localhost:8080/api/contatos

# 🔍 Buscar por nome ou e-mail (a letra "q" = "query" = "busca")
curl "http://localhost:8080/api/contatos?q=ana"

# 🧍 Ver só um contato (pelo número de identificação)
curl http://localhost:8080/api/contatos/1

# ➕ Criar um contato novo
curl -X POST http://localhost:8080/api/contatos \
  -H "Content-Type: application/json" \
  -d '{"nome":"Davi Oliveira","email":"davi@example.com","telefone":"+55 11 96666-0003"}'

# ✏️ Alterar o contato de id 1
curl -X PUT http://localhost:8080/api/contatos/1 \
  -H "Content-Type: application/json" \
  -d '{"nome":"Ana Souza Silva","email":"ana.souza@example.com","telefone":"+55 11 98888-0001"}'

# 🗑️ Apagar o contato de id 3
curl -X DELETE http://localhost:8080/api/contatos/3
```

Troque `8080` por `8081` para fazer a mesma coisa na versão Quarkus.

---

## 🌐 Páginas para abrir no navegador

| O que é | Spring Boot | Quarkus |
|---|---|---|
| A página com a lista de contatos | http://localhost:8080/ | http://localhost:8081/ |
| **Swagger UI** (documentação que testa a API sozinho) | http://localhost:8080/swagger-ui.html | http://localhost:8081/swagger-ui |
| O "desenho" da API em JSON | http://localhost:8080/v3/api-docs | http://localhost:8081/openapi |
| "Eu tô vivo?" (health check) | http://localhost:8080/actuator/health | http://localhost:8081/q/health |
| Console do banco H2 | http://localhost:8080/h2-console | — |

> 🧠 **Swagger UI** é a página mais legal para começar: ela mostra todos os
> "endpoints" (os comandos disponíveis) com um botãozinho pra você **clicar e
> testar sem digitar comando nenhum**.

---

## 📡 O contrato da API (o mesmo nas duas versões)

As duas implementações **respondem exatamente igual**. Isso é proposital — é
o que permite trocar o Spring Boot pelo Quarkus (ou vice-versa) sem quebrar
nada do lado de quem usa.

| Comando | Endereço | O que faz | Resposta boa |
|---|---|---|---|
| `GET` | `/api/contatos?page=0&size=10&q=ana` | Lista paginada; `q` filtra por nome/e-mail | `200` |
| `GET` | `/api/contatos/{id}` | Acha um contato pelo id | `200` |
| `POST` | `/api/contatos` | Cria um contato | `201` + o endereço do novo |
| `PUT` | `/api/contatos/{id}` | Altera um contato | `200` |
| `DELETE` | `/api/contatos/{id}` | Apaga um contato | `204` |

**O que você envia para criar/alterar:**

```json
{
  "nome": "Ana Souza",
  "email": "ana@example.com",
  "telefone": "+55 11 98888-0001"
}
```

**O que você recebe de volta:**

```json
{
  "id": 1,
  "nome": "Ana Souza",
  "email": "ana@example.com",
  "telefone": "+55 11 98888-0001",
  "criadoEm": "2026-01-01T12:00:00Z",
  "atualizadoEm": "2026-01-01T12:00:00Z"
}
```

**E quando dá errado?** O programa sempre responde no **mesmíssimo formato**, com
uma explicação do que aconteceu:

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

| Código | Quer dizer |
|---|---|
| `400` | Você mandou algo errado (faltou o nome, o e-mail não é e-mail...) |
| `404` | Não achei esse contato |
| `409` | Já existe outro contato com esse e-mail |

---

## 🏗️ Como o código funciona por dentro (a parte legal)

Vamos entrar num **restaurante** e ver onde cada arquivo trabalha:

```
Navegador / curl
      │
      ▼
┌─────────────────────────────┐
│  web/  (ContatoController)  │  🚪 O GARÇOM
│  Recebe o pedido, devolve    │  Sabe o caminho da cozinha,
│  a resposta. Não cozinha.    │  mas NÃO cozinha.
└──────────────┬──────────────┘
               │
               ▼
┌─────────────────────────────┐
│  service/ (ContatoService)   │  🧠 O CHEFE DE COZINHA
│  A regras do negócio:        │  "E-mail não pode repetir",
│  e-mail único, normaliza,    │  "normaliza o e-mail pra minúsculo",
│  pagina os resultados.       │  decide o que vai pro prato.
└──────────────┬──────────────┘
               │
               ▼
┌─────────────────────────────┐
│  repository/ (ContatoRepo)   │  🗄️ O DESPENSÁRIO
│  Guarda, busca, filtra,      │  Só fala com o banco de dados.
│  pagina. Nada de regra.      │  (e nunca inventa regra nova)
└──────────────┬──────────────┘
               │
               ▼
         Banco de dados
      (H2 ou PostgreSQL)
```

E tem mais duas peças:

- **📦 `domain/Contato.java`** — o **molde**. Define como é um contato: nome,
  e-mail, telefone, e as datas de criação/alteração. Também diz pro banco
  que o **e-mail é único** (não pode ter dois contatos com o mesmo e-mail).
- **📝 `dto/`** — os **papelinhos**. `ContatoRequest` é o papel que o cliente
  preenche; `ContatoResponse` é o papel que volta pro cliente. `PaginaResponse`
  embrulha tudo isso junto com a informação de "tem mais páginas?".
- **❌ `error/`** — o **"quando dá errado"**. Cada problema tem uma exceção
  própria e um "tradutor" que transforma ela em JSON bonito (Spring usa o
  `GlobalExceptionHandler`, Quarkus usa os `ExceptionMapper`).

### As regras do "Contato" (o que o programa não deixa você fazer)

- `nome`: obrigatório, de **3 a 100** caracteres
- `email`: obrigatório, tem que ser um **e-mail de verdade**, máximo 150
- `telefone`: **opcional**, só números, espaço, `+`, `-` e parênteses (8 a 20)
- `email`: **não pode repetir** — se repetir, vem `409 Conflict`

---

## 🧪 Rodando os testes

**Teste** é um programinha que roda sozinho e diz "o código ainda funciona?".
É como a lista de verificação antes de entregar o trabalho.

```bash
cd springboot-contatos && ./mvnw test    # 17 testes
cd quarkus-contatos    && ./mvnw test    # 17 testes
```

No fim, você deve ver algo assim:

```
[INFO] Tests run: 17, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

São 3 tipos de teste em cada projeto:

| Tipo | O que ele confere | Onde está |
|---|---|---|
| **Unitário** | As regras do negócio (e-mail duplicado, normalização, paginação) | `service/ContatoServiceTest.java` |
| **Repositório** | Se o banco realmente salva, busca e filtra | `repository/ContatoRepositoryIT.java` |
| **Integração (API)** | Se a API responde os códigos certos (`200`, `404`, `409`) | `web/ContatoControllerIT.java` |

---

## 🐳 Rodando com Docker + PostgreSQL (o "modo profissional")

Até agora a gente usou o **H2** (caderninho de rascunho): os dados somem
quando você desliga o programa. O **Docker Compose** sobe tudo de uma vez, com
**PostgreSQL** de verdade (os dados ficam salvos mesmo depois de desliga).

```bash
# 1. Copiar o arquivo de exemplo (ele guarda senha e porta)
cp .env.example .env

# 2. Subir tudo (Docker baixa as imagens na primeira vez — demora um pouco)
docker compose up -d --build

# 3. Ver o que está de pé
docker compose ps
```

| Serviço | Endereço | Como saber se está vivo |
|---|---|---|
| Spring Boot | http://localhost:8080 | http://localhost:8080/actuator/health |
| Quarkus | http://localhost:8081 | http://localhost:8081/q/health |
| PostgreSQL | `localhost:5432` (user `contatos`, senha `contatos`) | `pg_isready` |

**Comandos úteis:**

```bash
docker compose logs -f springboot quarkus   # ver os logs ao vivo
docker compose exec postgres psql -U contatos -d contatos_spring   # mexer no banco
docker compose down                          # desligar (mantém os dados)
docker compose down -v                       # desligar e APAGAR os dados
```

### Como isso funciona por dentro?

- 🗄️ **Dois bancos separados**: o `db/init/01-create-databases.sql` cria
  `contatos_spring` e `contatos_quarkus`. Assim cada API pode recriar suas
  tabelas sem briga com a outra. Esse script só roda na **primeira** vez que o
  volume é criado — se quiser rodar de novo, use `docker compose down -v`.
- 🔧 **Sem mexer no código**: o `docker-compose.yml` troca o H2 pelo PostgreSQL
  por meio de **variáveis de ambiente** (`SPRING_DATASOURCE_*` e
  `QUARKUS_DATASOURCE_*`). Rodando local com `./mvnw`, o H2 continua.
- ⚠️ **O Quarkus decide o driver em "tempo de build"**: por isso o `Dockerfile`
  compila com `-Dquarkus.datasource.db-kind=postgresql`. Sem essa flag, a
  imagem sobe e quebra com `Driver does not support the provided URL`.
- 📦 **Imagens em duas etapas**: a primeira imagem (`maven`) só **compila**; a
  segunda (`eclipse-temurin`) só **executa** e roda como usuário sem permissão
  de administrador — mais seguro.
- 🗂️ **Sem Flyway por enquanto**: o banco é criado pelo próprio Hibernate
  (`ddl-auto=update` / `database.generation=update`). Em produção, o certo
  seria versionar o schema com Flyway ou Liquibase.

---

## ⚖️ Spring Boot vs Quarkus: as diferenças

As duas versões fazem a mesma coisa. Mas **por dentro**, cada framework tem
seu jeito de falar as coisas:

| Camada | Spring Boot 3 | Quarkus 3 |
|---|---|---|
| HTTP | `@RestController` + `@RequestMapping` | `@Path` / `@GET` / `@POST` |
| Resposta | `ResponseEntity<T>` | `jakarta.ws.rs.core.Response` |
| Repositório | `interface ContatoRepository extends JpaRepository` | `class ContatoRepository implements PanacheRepository` |
| Transação | `@Transactional(readOnly = true)` | `@Transactional` (Jakarta TX) |
| "Classe gerenciada" | `@Service`, `@Component` | `@ApplicationScoped` |
| Validação | `@Valid` + `@RestControllerAdvice` | `@Valid` + `ExceptionMapper` |
| Erros | `@RestControllerAdvice` | `@Provider ExceptionMapper<T>` |
| Configuração | `application.properties` | `application.properties` + `@ConfigMapping` tipado |
| Dados de exemplo no start | `CommandLineRunner` | `@Observes StartupEvent` |
| Teste de API | `@SpringBootTest` + MockMvc | `@QuarkusTest` + REST Assured |
| Documentação | `springdoc-openapi` | `quarkus-smallrye-openapi` |

**Detalhes que ajudam a não se perder:**

1. 📁 Os **arquivos estáticos** (o HTML) ficam em lugares diferentes:
   `src/main/resources/static/` no Spring e
   `src/main/resources/META-INF/resources/` no Quarkus.
2. 🔢 `GET /api/contatos/abc` (id que não é número): o Spring devolve `400`,
   o Quarkus devolve `404`.
3. 🔥 **Modo de desenvolvimento**: o Spring sobe com o Tomcat por dentro; o
   Quarkus tem *live reload* (salva o arquivo → recarrega sozinho) e a
   **Dev UI** em `/q/dev-ui`.
4. 🚀 **Produção**: o Quarkus consegue gerar um **executável nativo** (um
   programa de verdade, sem Java por baixo); o Spring gera um `.jar` que roda
   com Java. Aqui no projeto, **nenhum dos dois** está configurado para
   produção.

---

## 🩺 Se der errado (as datas)

| O que aparece | Por quê | Como resolver |
|---|---|---|
| `zsh: command not found: java` | Não tem JDK instalado | `brew install openjdk@17` e abra um terminal novo |
| `JAVA_HOME is not defined correctly` | O `JAVA_HOME` aponta para um lugar que não existe | `export JAVA_HOME=$(/usr/libexec/java_home -v 17)` |
| `Permission denied: ./mvnw` | O macOS perdeu a permissão de executar | `chmod +x mvnw` |
| `Address already in use` (8080/8081) | Já tem um programa usando essa porta | `lsof -i :8080` para achar e fechar, ou mude a porta no `application.properties` |
| `Unsupported class file major version` | O Maven está compilando com Java antigo | Refaça o Passo 1 e confira com `./mvnw -v` |
| `Killed` durante o build | Memória insuficiente | Feche outros programas ou rode `./mvnw -T 1C package` |
| Compilação muito lenta (Apple Silicon) | O Java está rodando em modo "emulado" (Rosetta) | Instale o JDK nativo: `brew install openjdk@17` |

---

## 🔑 Endereços para colar no navegador

```
Spring Boot ................ http://localhost:8080
Quarkus .................... http://localhost:8081

Swagger UI (Spring) ........ http://localhost:8080/swagger-ui.html
Swagger UI (Quarkus) ....... http://localhost:8081/swagger-ui
Health (Spring) ............ http://localhost:8080/actuator/health
Health (Quarkus) ........... http://localhost:8081/q/health
```

---

## 📖 Resumão final

1. Instale o **JDK 17** e aponte o `JAVA_HOME`.
2. `chmod +x` nos dois `mvnw`.
3. Rode **uma** das duas versões: `cd springboot-contatos && ./mvnw spring-boot:run`
   ou `cd quarkus-contatos && ./mvnw quarkus:dev`.
4. Abra o navegador e separe no Swagger UI.
5. Quer o PostgreSQL? `cp .env.example .env && docker compose up -d --build`.
6. `./mvnw test` em cada pasta antes de dizer que terminou.

**O que você aprendeu?** Que um projeto Java no mundo real é sempre a mesma
coisa montada com peças diferentes: uma **porta da frente** (web), um
**cérebro** (service), um **despensário** (repository), um **molde**
(domain) e uma **caixa de teste** (test). Trocar o framework é trocar as
peças — a **ideia** continua a mesma.

---

<div align="center">

**Feito com 🧠, ☕ e Java 17.**

Spring Boot 3.5.16 · Quarkus 3.40.1 · Java 17 · Maven 3.9.16

</div>
