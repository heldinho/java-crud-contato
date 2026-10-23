import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { ContatoForm } from './contato-form';
import type { Contato } from '../../core/contato.model';

const contatoDeExemplo: Contato = {
  id: 1,
  nome: 'Maria Silva',
  email: 'maria@example.com',
  telefone: null,
  criadoEm: '2026-01-01T12:00:00Z',
  atualizadoEm: '2026-01-01T12:00:00Z',
};

describe('ContatoForm', () => {
  let fixture: ComponentFixture<ContatoForm>;
  let http: HttpTestingController;

  beforeEach(async () => {
    TestBed.configureTestingModule({
      imports: [ContatoForm],
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    await TestBed.compileComponents();
    http = TestBed.inject(HttpTestingController);
    fixture = TestBed.createComponent(ContatoForm);
  });

  afterEach(() => http.verify());

  it('não deve enviar requisição com formulário inválido', () => {
    fixture.componentInstance['salvar']();
    http.expectNone((r) => r.url === '/api/contatos');
  });

  it('deve criar contato enviando telefone nulo quando o campo está vazio', async () => {
    const component = fixture.componentInstance;
    const emitidos: Contato[] = [];
    component.salvo.subscribe((contato) => emitidos.push(contato));

    component['form'].setValue({
      nome: 'Maria Silva',
      email: 'maria@example.com',
      telefone: '',
    });
    component['salvar']();

    const req = http.expectOne('/api/contatos');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({
      nome: 'Maria Silva',
      email: 'maria@example.com',
      telefone: null,
    });
    req.flush(contatoDeExemplo);
    await fixture.whenStable();

    expect(emitidos).toEqual([contatoDeExemplo]);
  });

  it('deve preencher o formulário ao receber um contato em edição', async () => {
    fixture.componentRef.setInput('contato', contatoDeExemplo);
    await fixture.whenStable();

    expect(fixture.componentInstance['form'].getRawValue()).toEqual({
      nome: 'Maria Silva',
      email: 'maria@example.com',
      telefone: '',
    });
  });

  it('deve aplicar os fieldErrors da API ao campo correspondente', () => {
    const component = fixture.componentInstance;
    component['form'].setValue({
      nome: 'Maria Silva',
      email: 'maria@example.com',
      telefone: '',
    });
    component['salvar']();

    http.expectOne('/api/contatos').flush(
      {
        timestamp: '2026-01-01T12:00:00Z',
        status: 400,
        error: 'Bad Request',
        message: 'dados inválidos',
        path: '/api/contatos',
        fieldErrors: { email: 'email inválido' },
      },
      { status: 400, statusText: 'Bad Request' },
    );

    expect(component['erroDe']('email')).toBe('email inválido');
  });

  it('deve mostrar erro geral para falhas sem fieldErrors', () => {
    const component = fixture.componentInstance;
    component['form'].setValue({
      nome: 'Maria Silva',
      email: 'maria@example.com',
      telefone: '',
    });
    component['salvar']();

    http.expectOne('/api/contatos').flush(
      {
        timestamp: '2026-01-01T12:00:00Z',
        status: 500,
        error: 'Internal Server Error',
        message: 'erro interno inesperado',
        path: '/api/contatos',
        fieldErrors: {},
      },
      { status: 500, statusText: 'Server Error' },
    );

    expect(component['erroGeral']()).toBe('erro interno inesperado');
  });
});
