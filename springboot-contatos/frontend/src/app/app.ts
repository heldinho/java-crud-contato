import { Component, inject } from '@angular/core';
import { ContatosPage } from './features/contatos/contatos-page';
import { ToastService } from './core/toast.service';

@Component({
  selector: 'app-root',
  styleUrl: './app.css',
  templateUrl: './app.html',
  imports: [ContatosPage],
})
export class App {
  protected readonly backend = 'Spring Boot';
  protected readonly porta = '8080';
  protected readonly toast = inject(ToastService);
}
