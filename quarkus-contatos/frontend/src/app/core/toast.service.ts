import { Injectable, signal } from '@angular/core';

export type ToastTipo = 'sucesso' | 'erro' | 'info';

export interface Toast {
  id: number;
  tipo: ToastTipo;
  texto: string;
}

/** Notificações temporárias (toasts) baseadas em signals. */
@Injectable({ providedIn: 'root' })
export class ToastService {
  private readonly _toasts = signal<Toast[]>([]);
  readonly toasts = this._toasts.asReadonly();
  private proximoId = 1;

  mostrar(texto: string, tipo: ToastTipo = 'sucesso'): void {
    const id = this.proximoId++;
    this._toasts.update((atual) => [...atual, { id, tipo, texto }]);
    setTimeout(() => this.remover(id), 4000);
  }

  remover(id: number): void {
    this._toasts.update((atual) => atual.filter((toast) => toast.id !== id));
  }
}
