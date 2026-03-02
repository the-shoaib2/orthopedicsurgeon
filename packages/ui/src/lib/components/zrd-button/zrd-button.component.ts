import { ChangeDetectionStrategy, Component, Input, booleanAttribute } from '@angular/core';
import { CommonModule } from '@angular/common';

export type ZrdButtonVariant = 'primary' | 'secondary' | 'ghost' | 'danger' | 'outline';
export type ZrdButtonSize = 'sm' | 'md' | 'lg';

@Component({
  selector: 'zrd-button, button[zrdButton], a[zrdButton]',
  standalone: true,
  imports: [CommonModule],
  template: `
    <span class="flex items-center justify-center gap-2">
      <ng-container *ngIf="loading">
        <svg class="animate-spin h-4 w-4" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
          <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
          <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
        </svg>
      </ng-container>
      <ng-content select="[leftIcon]"></ng-content>
      <ng-content></ng-content>
      <ng-content select="[rightIcon]"></ng-content>
    </span>
  `,
  host: {
    '[class]': 'classes',
    '[attr.disabled]': '(disabled || loading) ? true : null',
    '[attr.aria-busy]': 'loading',
    '[attr.aria-disabled]': 'disabled || loading'
  },
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ZrdButtonComponent {
  @Input() variant: ZrdButtonVariant = 'primary';
  @Input() size: ZrdButtonSize = 'md';
  @Input({ transform: booleanAttribute }) loading = false;
  @Input({ transform: booleanAttribute }) disabled = false;

  get classes(): string {
    const base = 'inline-flex items-center justify-center font-medium transition-all duration-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-offset-2 disabled:opacity-50 disabled:cursor-not-allowed';
    
    const variants: Record<ZrdButtonVariant, string> = {
      primary: 'bg-primary-600 text-white hover:bg-primary-700 focus:ring-primary-500',
      secondary: 'bg-secondary-100 text-secondary-900 hover:bg-secondary-200 focus:ring-secondary-500',
      outline: 'border border-secondary-300 bg-transparent text-secondary-700 hover:bg-secondary-50 focus:ring-secondary-500',
      ghost: 'bg-transparent text-secondary-600 hover:bg-secondary-100 focus:ring-secondary-500',
      danger: 'bg-red-600 text-white hover:bg-red-700 focus:ring-red-500'
    };

    const sizes: Record<ZrdButtonSize, string> = {
      sm: 'px-3 py-1.5 text-xs',
      md: 'px-4 py-2 text-sm',
      lg: 'px-6 py-3 text-base'
    };

    return `${base} ${variants[this.variant]} ${sizes[this.size]}`;
  }
}
