import { Component, inject } from '@angular/core';
import {
  LucideCircleCheck,
  LucideCircleX,
  LucideContact,
  LucideInfo,
  LucideX,
} from '@lucide/angular';
import { ContatosPage } from './features/contatos/contatos-page';
import { ToastService } from './core/toast.service';
import { UiButtonDirective } from './ui/button/button';

@Component({
  selector: 'app-root',
  styleUrl: './app.css',
  templateUrl: './app.html',
  imports: [
    ContatosPage,
    UiButtonDirective,
    LucideCircleCheck,
    LucideCircleX,
    LucideContact,
    LucideInfo,
    LucideX,
  ],
})
export class App {
  protected readonly backend = 'Quarkus';
  protected readonly porta = '8081';
  protected readonly toast = inject(ToastService);
}
