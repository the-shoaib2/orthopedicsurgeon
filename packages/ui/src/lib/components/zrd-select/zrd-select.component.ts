import { ChangeDetectionStrategy, Component, Input, forwardRef, booleanAttribute } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';

export interface ZrdSelectOption {
  label: string;
  value: any;
  disabled?: boolean;
}

@Component({
  selector: 'zrd-select',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="flex flex-col gap-1.5 w-full">
      <label *ngIf="label" [for]="id" class="text-sm font-medium text-secondary-700">
        {{ label }}
        <span *ngIf="required" class="text-red-500">*</span>
      </label>
      
      <select
        [id]="id"
        [disabled]="disabled"
        [value]="value"
        (change)="onSelectChange($event)"
        (blur)="onBlur()"
        [class]="selectClasses"
      >
        <option *ngIf="placeholder" value="" disabled selected>{{ placeholder }}</option>
        <option *ngFor="let opt of options" [value]="opt.value" [disabled]="opt.disabled">
          {{ opt.label }}
        </option>
      </select>

      <p *ngIf="hint && !error" class="text-xs text-secondary-500">{{ hint }}</p>
      <p *ngIf="error" class="text-xs text-red-500">{{ error }}</p>
    </div>
  `,
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => ZrdSelectComponent),
      multi: true
    }
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ZrdSelectComponent implements ControlValueAccessor {
  @Input() id = `zrd-select-${Math.random().toString(36).substr(2, 9)}`;
  @Input() label?: string;
  @Input() placeholder = 'Select an option';
  @Input() options: ZrdSelectOption[] = [];
  @Input() hint?: string;
  @Input() error?: string;
  @Input({ transform: booleanAttribute }) required = false;

  value: any = '';
  disabled = false;

  onChange: any = () => {};
  onTouched: any = () => {};

  get selectClasses(): string {
    const base = 'w-full rounded-lg border border-secondary-300 bg-white px-3 py-2 text-sm text-secondary-900 transition-all duration-200 focus:border-primary-500 focus:ring-1 focus:ring-primary-500 disabled:bg-secondary-50 disabled:text-secondary-500 appearance-none';
    const errorClass = this.error ? 'border-red-500 focus:border-red-500 focus:ring-red-500' : '';
    return `${base} ${errorClass}`;
  }

  onSelectChange(event: any) {
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
