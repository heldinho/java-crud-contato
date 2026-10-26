import { Directive } from '@angular/core';

/**
 * Card shadcn/ui: chrome de superfície (borda, fundo, sombra, raio).
 * Uso: `<section uiCard class="p-4">…</section>` — padding via classe estática.
 */
@Directive({
  selector: '[uiCard]',
  host: { class: 'rounded-xl border bg-card text-card-foreground shadow-sm' },
})
export class UiCardDirective {}
