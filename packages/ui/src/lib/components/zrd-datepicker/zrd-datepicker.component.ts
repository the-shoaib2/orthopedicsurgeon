import { ChangeDetectionStrategy, Component, Input, forwardRef, booleanAttribute } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';

@Component({
  selector: 'zrd-datepicker',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="flex flex-col gap-1.5 w-full">
      <label *ngIf="label" [for]="id" class="text-sm font-medium text-secondary-700">
        {{ label }}
        <span *ngIf="required" class="text-red-500">*</span>
      </label>
      
      <div class="relative flex items-center">
        <div class="absolute left-3 text-secondary-400">
          <svg class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
          </svg>
        </div>
        
        <input
          [id]="id"
          type="date"
          [disabled]="disabled"
          [value]="value"
          [min]="minDate"
          [max]="maxDate"
          (input)="onInputChange($event)"
          (blur)="onBlur()"
          class="w-full rounded-lg border border-secondary-300 bg-white pl-10 pr-3 py-2 text-sm text-secondary-900 placeholder-secondary-400 transition-all duration-200 focus:border-primary-500 focus:ring-1 focus:ring-primary-500 disabled:bg-secondary-50 disabled:text-secondary-500"
        />
      </div>

      <p *ngIf="error" class="text-xs text-red-500">{{ error }}</p>
    </div>
  `,
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => ZrdDatePickerComponent),
      multi: true
    }
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ZrdDatePickerComponent implements ControlValueAccessor {
  @Input() id = `zrd-datepicker-${Math.random().toString(36).substr(2, 9)}`;
  @Input() label?: string;
  @Input() error?: string;
  @Input() minDate?: string;
  @Input() maxDate?: string;
  @Input({ transform: booleanAttribute }) required = false;

  value: string = '';
  disabled = false;

  onChange: any = () => {};
  onTouched: any = () => {};

  onInputChange(event: any) {
    const val = event.target.value;
    this.value = val;
    this.onChange(val);
  }

  onBlur() {
    this.onTouched();
  }

  writeValue(value: any): void {
    this.value = value || '';
  }

  registerOnChange(fn: any): void {
    this.onChange = fn;
  }

  registerOnTouched(fn: any): void {
    this.onTouched = fn;
  }

  setDisabledState(isDisabled: boolean): void {
    this.disabled = isDisabled;
  }
}
