import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { ContatosPage } from './contatos-page';
import { ContatoService } from '../../core/contato.service';
import type { Contato, Pagina } from '../../core/contato.model';

const contatos: Contato[] = [
  {
    id: 1,
    nome: 'Maria Silva',
    email: 'maria@example.com',
    telefone: '(11) 99999-9999',
    criadoEm: '2026-01-01T12:00:00Z',
    atualizadoEm: '2026-01-01T12:00:00Z',
  },
  {
    id: 2,
    nome: 'João Souza',
    email: 'joao@example.com',
    telefone: null,
    criadoEm: '2026-01-02T12:00:00Z',
    atualizadoEm: '2026-01-02T12:00:00Z',
  },
];

const paginaDeExemplo: Pagina<Contato> = {
  content: contatos,
  page: 0,
  size: 10,
  totalElements: 12,
  totalPages: 2,
};

describe('ContatosPage', () => {
  let fixture: ComponentFixture<ContatosPage>;
  let listar: ReturnType<typeof vi.fn>;
  let remover: ReturnType<typeof vi.fn>;

  beforeEach(async () => {
    listar = vi.fn(() => of(paginaDeExemplo));
    remover = vi.fn(() => of(undefined));

    await TestBed.configureTestingModule({
      imports: [ContatosPage],
      providers: [{ provide: ContatoService, useValue: { listar, remover } }],
    }).compileComponents();

    fixture = TestBed.createComponent(ContatosPage);
    await fixture.whenStable();
  });

  it('deve listar os contatos retornados pela API', () => {
    const linhas = (fixture.nativeElement as HTMLElement).querySelectorAll('tbody tr');
    expect(listar).toHaveBeenCalledOnce();
    expect(linhas).toHaveLength(2);
    expect(linhas[0].textContent).toContain('Maria Silva');
  });

  it('deve mostrar o total de contatos na paginação', () => {
    const paginacao = (fixture.nativeElement as HTMLElement).querySelector('.paginacao');
    expect(paginacao?.textContent).toContain('12 contato(s)');
  });

  it('deve carregar a próxima página ao avançar na paginação', () => {
    fixture.componentInstance['proxima']();

    expect(listar).toHaveBeenLastCalledWith({ q: '', page: 1, size: 10 });
  });

  it('deve excluir o contato e recarregar a lista após confirmação', () => {
    fixture.componentInstance['confirmarExclusao'](contatos[0]);

    expect(remover).toHaveBeenCalledWith(1);
    expect(listar).toHaveBeenCalledTimes(2);
  });
});
