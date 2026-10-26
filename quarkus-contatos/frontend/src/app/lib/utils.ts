import { clsx, type ClassValue } from 'clsx';
import { twMerge } from 'tailwind-merge';

/**
 * Mescla classes Tailwind evitando conflitos (mesma utilidade `cn` do shadcn/ui).
 * Útil para compor classes condicionais sem perder a ordem de precedência.
 */
export function cn(...inputs: ClassValue[]): string {
  return twMerge(clsx(inputs));
}
