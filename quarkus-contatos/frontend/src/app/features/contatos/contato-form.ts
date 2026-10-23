import { Component, effect, inject, input, output, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ContatoService } from '../../core/contato.service';
import type { Contato, ContatoRequest } from '../../core/contato.model';
import { extrairErroApi } from '../../core/erro-api';

type Campo = 'nome' | 'email' | 'telefone';

/** Mesma regra do backend: dígitos, espaço, +, - e parênteses, de 8 a 20 caracteres. */
const TELEFONE_REGEX = /^[0-9+()\s-]{8,20}$/;

/** Formulário de criação/edição de contato (modal). */
@Component({
  selector: 'app-contato-form',
  styleUrl: './contato-form.css',
  templateUrl: './contato-form.html',
  imports: [ReactiveFormsModule],
})
export class ContatoForm {
  private readonly fb = inject(FormBuilder);
  private readonly service = inject(ContatoService);

  /** Contato em edição, ou `null` para criar um novo. */
  readonly contato = input<Contato | null>(null);
  readonly salvo = output<Contato>();
  readonly cancelado = output<void>();

  protected readonly salvando = signal(false);
  protected readonly erroGeral = signal<string | null>(null);

  protected readonly form = this.fb.nonNullable.group({
    nome: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(100)]],
    email: ['', [Validators.required, Validators.email, Validators.maxLength(150)]],
    telefone: ['', [Validators.pattern(TELEFONE_REGEX)]],
  });

  constructor() {
    // Reage à troca do input (abertura do modal) repreenchendo o formulário.
    effect(() => {
      const contato = this.contato();
      this.erroGeral.set(null);
      this.form.reset({
        nome: contato?.nome ?? '',
        email: contato?.email ?? '',
        telefone: contato?.telefone ?? '',
      });
      this.form.markAsPristine();
    });
  }

  protected get modoEdicao(): boolean {
    return this.contato() !== null;
  }

  protected salvar(): void {
    this.form.markAllAsTouched();
    if (this.form.invalid || this.salvando()) {
      return;
    }

    const valor = this.form.getRawValue();
    const telefone = valor.telefone.trim();
    const request: ContatoRequest = {
      nome: valor.nome.trim(),
      email: valor.email.trim(),
      // O backend valida telefone com @Pattern: string vazia falharia; enviamos null.
      telefone: telefone === '' ? null : telefone,
    };
    const id = this.contato()?.id;

    this.salvando.set(true);
    this.erroGeral.set(null);

    const requisicao$ =
      id === undefined ? this.service.criar(request) : this.service.atualizar(id, request);

    requisicao$.subscribe({
      next: (contato) => {
        this.salvando.set(false);
        this.salvo.emit(contato);
      },
      error: (erro: unknown) => {
        this.salvando.set(false);
        this.aplicarErrosDaApi(erro);
      },
    });
  }

  protected cancelar(): void {
    if (!this.salvando()) {
      this.cancelado.emit();
    }
  }

  /** Mensagem de erro a exibir abaixo do campo, ou `null` se não houver. */
  protected erroDe(campo: Campo): string | null {
    const control = this.form.get(campo);
    if (!control?.touched || !control.errors) {
      return null;
    }
    const erros = control.errors;
    const servidor = erros['servidor'];
    if (typeof servidor === 'string') {
      return servidor;
    }
    if (erros['required']) {
      return campo === 'nome' ? 'nome é obrigatório' : 'email é obrigatório';
    }
    if (erros['minlength'] || erros['maxlength']) {
      return 'nome deve ter entre 3 e 100 caracteres';
    }
    if (erros['email']) {
      return 'email inválido';
    }
    if (erros['pattern']) {
      return 'telefone inválido (use apenas dígitos, espaço, +, - e parênteses)';
    }
    return 'valor inválido';
  }

  /** Aplica os fieldErrors do backend aos controles; sem campo específico, mostra o alerta geral. */
  private aplicarErrosDaApi(erro: unknown): void {
    const erroApi = extrairErroApi(erro);
    let aplicouEmCampo = false;

    for (const [campo, mensagem] of Object.entries(erroApi.fieldErrors)) {
      const control = this.form.get(campo);
      if (control) {
        control.setErrors({ servidor: mensagem });
        control.markAsTouched();
        aplicouEmCampo = true;
      }
    }

    if (!aplicouEmCampo) {
      this.erroGeral.set(erroApi.message);
    }
  }
}
