import { ChangeDetectionStrategy, Component, Inject, InjectionToken, signal, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';

export type ZrdToastType = 'success' | 'error' | 'warning' | 'info';

export interface ZrdToastData {
  id: string;
  title: string;
  message?: string;
  type: ZrdToastType;
  duration?: number;
}

@Component({
  selector: 'zrd-toast',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div 
      class="flex items-start gap-3 p-4 bg-white border rounded-xl shadow-lg w-80 animate-in slide-in-from-right duration-300"
      [class]="borderClasses"
    >
      <div [class]="iconWrapperClasses" class="p-1.5 rounded-lg flex-shrink-0">
         <ng-container [ngSwitch]="type">
           <svg *ngSwitchCase="'success'" class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"/></svg>
           <svg *ngSwitchCase="'error'" class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/></svg>
           <svg *ngSwitchCase="'warning'" class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"/></svg>
           <svg *ngSwitchCase="'info'" class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"/></svg>
         </ng-container>
      </div>
      
      <div class="flex-1 min-w-0">
        <h4 class="text-sm font-semibold text-secondary-900 line-clamp-1">{{ title }}</h4>
        <p *ngIf="message" class="text-xs text-secondary-500 mt-0.5">{{ message }}</p>
      </div>

      <button (click)="close()" class="text-secondary-400 hover:text-secondary-600">
        <svg class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/></svg>
      </button>
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ZrdToastComponent {
  @Input() type: ZrdToastType = 'info';
  @Input() title = '';
  @Input() message?: string;
  @Output() onClose = new EventEmitter<void>();

  get borderClasses(): string {
    const types: Record<ZrdToastType, string> = {
      success: 'border-green-100',
      error: 'border-red-100',
      warning: 'border-amber-100',
      info: 'border-primary-100'
    };
    return types[this.type];
  }

  get iconWrapperClasses(): string {
    const types: Record<ZrdToastType, string> = {
      success: 'bg-green-50 text-green-600',
      error: 'bg-red-50 text-red-600',
      warning: 'bg-amber-50 text-amber-600',
      info: 'bg-primary-50 text-primary-600'
    };
    return types[this.type];
  }

  close() {
    this.onClose.emit();
  }
}
