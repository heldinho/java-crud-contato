import { Directive, computed, input } from '@angular/core';
import { cva, type VariantProps } from 'class-variance-authority';
import { cn } from '../../lib/utils';

/**
 * Variants do botão no estilo shadcn/ui, resolvidos com Tailwind v4.
 * Uso: `<button uiButton variant="secondary" size="sm">Texto</button>`
 */
export const buttonVariants = cva(
  'inline-flex cursor-pointer items-center justify-center gap-2 whitespace-nowrap rounded-md text-sm font-medium transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring/50 focus-visible:ring-offset-2 focus-visible:ring-offset-background disabled:pointer-events-none disabled:opacity-50 [&_svg]:pointer-events-none [&_svg]:size-4 [&_svg]:shrink-0',
  {
    variants: {
      variant: {
        default: 'bg-primary text-primary-foreground shadow-xs hover:bg-primary/90',
        destructive: 'bg-destructive text-destructive-foreground shadow-xs hover:bg-destructive/90',
        outline:
          'border border-input bg-background shadow-xs hover:bg-accent hover:text-accent-foreground',
        secondary: 'bg-secondary text-secondary-foreground shadow-xs hover:bg-secondary/80',
        ghost: 'hover:bg-accent hover:text-accent-foreground',
        link: 'text-primary underline-offset-4 hover:underline',
      },
      size: {
        default: 'h-9 px-4 py-2',
        sm: 'h-8 rounded-md px-3',
        lg: 'h-10 rounded-md px-6',
        icon: 'h-9 w-9',
      },
    },
    defaultVariants: {
      variant: 'default',
      size: 'default',
    },
  },
);

type ButtonProps = VariantProps<typeof buttonVariants>;
export type ButtonVariant = NonNullable<ButtonProps['variant']>;
export type ButtonSize = NonNullable<ButtonProps['size']>;

/**
 * Diretiva que aplica as classes do botão shadcn/ui em qualquer `<button>` ou `<a>`.
 * `extraClasses` aceita classes avulsas (ex.: `text-destructive`) que têm prioridade
 * sobre as do variant graças ao `cn`/`tailwind-merge`.
 */
@Directive({
  selector: 'button[uiButton], a[uiButton]',
  host: { '[class]': 'classes()' },
})
export class UiButtonDirective {
  readonly variant = input<ButtonVariant>('default');
  readonly size = input<ButtonSize>('default');
  readonly extraClasses = input('');

  readonly classes = computed(() =>
    cn(buttonVariants({ variant: this.variant(), size: this.size() }), this.extraClasses()),
  );
}
