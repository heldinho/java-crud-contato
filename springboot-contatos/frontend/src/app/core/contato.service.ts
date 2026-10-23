import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import type { Observable } from 'rxjs';
import type { Contato, ContatoRequest, Pagina } from './contato.model';

export interface FiltroContatos {
  q?: string;
  page: number;
  size: number;
}

/** Cliente HTTP do endpoint /api/contatos. O caminho é relativo: o proxy do dev server encaminha /api. */
@Injectable({ providedIn: 'root' })
export class ContatoService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = '/api/contatos';

  listar(filtro: FiltroContatos): Observable<Pagina<Contato>> {
    let params = new HttpParams().set('page', filtro.page).set('size', filtro.size);
    const q = filtro.q?.trim();
    if (q) {
      params = params.set('q', q);
    }
    return this.http.get<Pagina<Contato>>(this.baseUrl, { params });
  }

  buscarPorId(id: number): Observable<Contato> {
    return this.http.get<Contato>(`${this.baseUrl}/${id}`);
  }

  criar(contato: ContatoRequest): Observable<Contato> {
    return this.http.post<Contato>(this.baseUrl, contato);
  }

  atualizar(id: number, contato: ContatoRequest): Observable<Contato> {
    return this.http.put<Contato>(`${this.baseUrl}/${id}`, contato);
  }

  remover(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
