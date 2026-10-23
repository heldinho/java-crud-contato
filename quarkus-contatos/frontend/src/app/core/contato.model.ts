/** Representa um contato retornado pela API. */
export interface Contato {
  id: number;
  nome: string;
  email: string;
  telefone: string | null;
  /** Data de criação em ISO-8601 (UTC). */
  criadoEm: string;
  /** Data da última atualização em ISO-8601 (UTC). */
  atualizadoEm: string;
}

/** Payload aceito por POST/PUT /api/contatos. */
export interface ContatoRequest {
  nome: string;
  email: string;
  /** `null` quando o contato não tem telefone. */
  telefone: string | null;
}

/** Paginação retornada pela API (mesma forma em Spring Boot e Quarkus). */
export interface Pagina<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

/** Payload de erro padronizado retornado pela API. */
export interface ApiError {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  path: string;
  fieldErrors: Record<string, string>;
}
