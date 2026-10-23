# quarkus-contatos — Frontend (Angular 22)

CRUD de contatos em **Angular 22** (standalone, signals, zoneless, Vitest) consumindo a API REST do backend `quarkus-contatos` (`http://localhost:8081`).

## Requisitos

- Node.js >= 24.15 (ou 22.22.3+)
- Backend `quarkus-contatos` rodando na porta **8081**

## Rodando em desenvolvimento

```bash
# terminal 1 — backend (na pasta quarkus-contatos)
./mvnw quarkus:dev

# terminal 2 — frontend
cd frontend
npm start
```

Abra <http://localhost:4200>.

O `ng serve` usa o `proxy.conf.json` para encaminhar `/api` → `http://localhost:8081`, dispensando configuração de CORS no backend.

## Scripts

| Comando           | Descrição                                  |
| ----------------- | ------------------------------------------ |
| `npm start`       | Dev server com proxy para o backend        |
| `npm run build`   | Build de produção em `dist/`               |
| `npm test`        | Testes unitários (Vitest + jsdom)          |
| `npm run watch`   | Build em watch mode (desenvolvimento)      |

## Estrutura

```
src/app/
├── core/                  # modelos, service HTTP, toasts e normalização de erros da API
├── features/contatos/     # página da lista (busca/paginação) + modal de criação/edição
└── app.ts                 # shell: cabeçalho, página e notificações
```

## Decisões de design

- **Proxy em vez de CORS**: em produção, sirva os arquivos gerados em `dist/` (index + assets) atrás de um reverse proxy (nginx, por exemplo) que encaminhe `/api` para o backend.
- **Validação espelhada**: o formulário aplica as mesmas regras do backend (nome 3–100, e-mail, telefone 8–20 caracteres) e ainda exibe os `fieldErrors` retornados pela API (inclusive o 409 de e-mail duplicado).
- **Telefone vazio vira `null`**: o backend valida `telefone` com `@Pattern`, que rejeita string vazia — por isso o formulário envia `null`.
