import { HttpErrorResponse } from '@angular/common/http';
import type { ApiError } from './contato.model';

/** Erro já normalizado, pronto para exibição ao usuário. */
export interface ErroApi {
  status: number;
  message: string;
  fieldErrors: Record<string, string>;
}

/**
 * Converte qualquer erro do HttpClient no formato do `ApiError` dos backends.
 * O 409 (e-mail duplicado) vem sem `fieldErrors`; atribuímos a mensagem ao campo "email".
 */
export function extrairErroApi(erro: unknown): ErroApi {
  if (!(erro instanceof HttpErrorResponse)) {
    return { status: 0, message: 'erro inesperado', fieldErrors: {} };
  }

  const body = erro.error;
  if (body !== null && typeof body === 'object' && 'message' in body) {
    const api = body as Partial<ApiError>;
    const fieldErrors: Record<string, string> = { ...(api.fieldErrors ?? {}) };
    if (erro.status === 409 && !fieldErrors['email']) {
      fieldErrors['email'] = api.message ?? 'e-mail já cadastrado';
    }
    return {
      status: erro.status,
      message: api.message ?? mensagemPadrao(erro.status),
      fieldErrors,
    };
  }

  // Resposta sem corpo (ex.: erro do proxy em dev, timeout, rede fora do ar).
  return { status: erro.status, message: mensagemPadrao(erro.status), fieldErrors: {} };
}

function mensagemPadrao(status: number): string {
  switch (status) {
    case 0:
      return 'não foi possível falar com o servidor';
    case 400:
      return 'dados inválidos';
    case 404:
      return 'contato não encontrado';
    case 409:
      return 'e-mail já cadastrado';
    default:
      return status >= 500 ? 'erro interno no servidor' : 'falha na requisição';
  }
}
