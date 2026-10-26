import { Directive, computed, input } from '@angular/core';
import { cn } from '../../lib/utils';

const inputBaseClasses =
  'flex h-11 w-full rounded-lg border border-input bg-card px-3.5 text-base text-foreground shadow-xs outline-none transition-colors placeholder:text-muted-foreground focus-visible:border-ring focus-visible:ring-[3px] focus-visible:ring-ring/30 aria-invalid:border-destructive aria-invalid:ring-destructive/25 disabled:cursor-not-allowed disabled:opacity-50';

/**
 * Diretiva que aplica o estilo do input shadcn/ui em qualquer `<input>`.
 * A validade do campo (`aria-invalid`) é refletida na borda automaticamente.
 * Uso: `<input uiInput type="email" formControlName="email" />`
 */
@Directive({
  selector: '[uiInput]',
  host: { '[class]': 'classes()' },
})
export class UiInputDirective {
  readonly extraClasses = input('');

  readonly classes = computed(() => cn(inputBaseClasses, this.extraClasses()));
}
