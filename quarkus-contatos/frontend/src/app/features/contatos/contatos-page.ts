import { Component, DestroyRef, computed, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { DatePipe } from '@angular/common';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { debounceTime, distinctUntilChanged, finalize } from 'rxjs';
import { ContatoService } from '../../core/contato.service';
import { ToastService } from '../../core/toast.service';
import type { Contato, Pagina } from '../../core/contato.model';
import { extrairErroApi } from '../../core/erro-api';
import { ContatoForm } from './contato-form';

const TAMANHO_DA_PAGINA = 10;

/** Página principal: lista, busca, paginação e ações de CRUD. */
@Component({
  selector: 'app-contatos-page',
  styleUrl: './contatos-page.css',
  templateUrl: './contatos-page.html',
  imports: [ReactiveFormsModule, DatePipe, ContatoForm],
})
export class ContatosPage {
  private readonly service = inject(ContatoService);
  private readonly toasts = inject(ToastService);

  protected readonly contatos = signal<Contato[]>([]);
  protected readonly dados = signal<Pagina<Contato> | null>(null);
  protected readonly paginaAtual = signal(0);
  protected readonly carregando = signal(false);
  protected readonly erroAoCarregar = signal<string | null>(null);
  protected readonly termoBusca = signal('');

  protected readonly formAberto = signal(false);
  protected readonly contatoEmEdicao = signal<Contato | null>(null);
  protected readonly idParaExcluir = signal<number | null>(null);

  protected readonly buscaControl = new FormControl('', { nonNullable: true });

  protected readonly podeAnterior = computed(() => this.paginaAtual() > 0);
  protected readonly podeProxima = computed(() => {
    const dados = this.dados();
    return dados !== null && this.paginaAtual() < dados.totalPages - 1;
  });

  constructor() {
    this.buscaControl.valueChanges
      .pipe(debounceTime(300), distinctUntilChanged(), takeUntilDestroyed(inject(DestroyRef)))
      .subscribe((termo) => {
        this.termoBusca.set(termo);
        this.paginaAtual.set(0);
        this.carregar();
      });

    this.carregar();
  }

  protected carregar(): void {
    this.carregando.set(true);
    this.erroAoCarregar.set(null);

    this.service
      .listar({ q: this.termoBusca(), page: this.paginaAtual(), size: TAMANHO_DA_PAGINA })
      .pipe(finalize(() => this.carregando.set(false)))
      .subscribe({
        next: (dados) => {
          // Página vazia (ex.: excluir o último item da última página): volta uma página.
          if (dados.content.length === 0 && this.paginaAtual() > 0) {
            this.paginaAtual.update((pagina) => pagina - 1);
            this.carregar();
            return;
          }
          this.dados.set(dados);
          this.contatos.set(dados.content);
        },
        error: (erro: unknown) => this.erroAoCarregar.set(extrairErroApi(erro).message),
      });
  }

  protected anterior(): void {
    if (this.podeAnterior()) {
      this.paginaAtual.update((pagina) => pagina - 1);
      this.carregar();
    }
  }

  protected proxima(): void {
    if (this.podeProxima()) {
      this.paginaAtual.update((pagina) => pagina + 1);
      this.carregar();
    }
  }

  protected novo(): void {
    this.contatoEmEdicao.set(null);
    this.formAberto.set(true);
  }

  protected editar(contato: Contato): void {
    this.contatoEmEdicao.set(contato);
    this.formAberto.set(true);
  }

  protected fecharForm(): void {
    this.formAberto.set(false);
    this.contatoEmEdicao.set(null);
  }

  protected aoSalvar(contato: Contato): void {
    this.fecharForm();
    this.toasts.mostrar(`Contato "${contato.nome}" salvo com sucesso.`);
    this.carregar();
  }

  protected pedirConfirmacaoExclusao(contato: Contato): void {
    this.idParaExcluir.set(contato.id);
  }

  protected cancelarExclusao(): void {
    this.idParaExcluir.set(null);
  }

  protected confirmarExclusao(contato: Contato): void {
    this.service.remover(contato.id).subscribe({
      next: () => {
        this.idParaExcluir.set(null);
        this.toasts.mostrar(`Contato "${contato.nome}" excluído.`);
        this.carregar();
      },
      error: (erro: unknown) => {
        this.idParaExcluir.set(null);
        this.toasts.mostrar(extrairErroApi(erro).message, 'erro');
      },
    });
  }
}
