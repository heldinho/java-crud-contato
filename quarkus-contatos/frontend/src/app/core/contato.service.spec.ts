import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { ContatoService } from './contato.service';
import type { Contato, ContatoRequest, Pagina } from './contato.model';

const contatoDeExemplo: Contato = {
  id: 1,
  nome: 'Maria Silva',
  email: 'maria@example.com',
  telefone: '(11) 99999-9999',
  criadoEm: '2026-01-01T12:00:00Z',
  atualizadoEm: '2026-01-01T12:00:00Z',
};

const paginaDeExemplo: Pagina<Contato> = {
  content: [contatoDeExemplo],
  page: 0,
  size: 10,
  totalElements: 1,
  totalPages: 1,
};

describe('ContatoService', () => {
  let service: ContatoService;
  let http: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(ContatoService);
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  it('listar deve enviar paginação e busca como query params', () => {
    service.listar({ q: '  maria ', page: 2, size: 20 }).subscribe((pagina) => {
      expect(pagina).toEqual(paginaDeExemplo);
    });

    const req = http.expectOne((r) => r.url === '/api/contatos' && r.params.get('q') === 'maria');
    expect(req.request.method).toBe('GET');
    expect(req.request.params.get('page')).toBe('2');
    expect(req.request.params.get('size')).toBe('20');
    req.flush(paginaDeExemplo);
  });

  it('listar deve omitir o parâmetro q quando a busca é vazia', () => {
    service.listar({ q: '   ', page: 0, size: 10 }).subscribe();

    const req = http.expectOne('/api/contatos?page=0&size=10');
    expect(req.request.params.has('q')).toBe(false);
    req.flush(paginaDeExemplo);
  });

  it('criar deve fazer POST com o payload correto', () => {
    const request: ContatoRequest = {
      nome: 'Maria Silva',
      email: 'maria@example.com',
      telefone: null,
    };

    service.criar(request).subscribe((contato) => expect(contato.id).toBe(1));

    const req = http.expectOne('/api/contatos');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(request);
    req.flush(contatoDeExemplo);
  });

  it('atualizar deve fazer PUT no recurso do id', () => {
    const request: ContatoRequest = {
      nome: 'Maria Souza',
      email: 'maria@example.com',
      telefone: null,
    };

    service.atualizar(1, request).subscribe();

    const req = http.expectOne('/api/contatos/1');
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual(request);
    req.flush({ ...contatoDeExemplo, nome: 'Maria Souza' });
  });

  it('remover deve fazer DELETE no recurso do id', () => {
    service.remover(7).subscribe();

    const req = http.expectOne('/api/contatos/7');
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });
});
