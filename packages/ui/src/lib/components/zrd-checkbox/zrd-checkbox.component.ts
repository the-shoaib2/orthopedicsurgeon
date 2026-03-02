import { ChangeDetectionStrategy, Component, Input, forwardRef, booleanAttribute } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';

@Component({
  selector: 'zrd-checkbox',
  standalone: true,
  imports: [CommonModule],
  template: `
    <label [for]="id" class="inline-flex items-center cursor-pointer group">
      <div class="relative flex items-center">
        <input
          type="checkbox"
          [id]="id"
          [disabled]="disabled"
          [checked]="value"
          (change)="onCheckboxChange($event)"
          (blur)="onBlur()"
          class="peer sr-only"
        />
        <div 
          class="w-5 h-5 rounded border border-secondary-300 bg-white transition-all duration-200 peer-checked:bg-primary-600 peer-checked:border-primary-600 peer-focus:ring-2 peer-focus: ring-primary-500/20 group-hover:border-primary-400 disabled:opacity-50 disabled:bg-secondary-50"
        ></div>
        <svg 
          class="absolute w-3.5 h-3.5 text-white left-0.75 top-0.75 pointer-events-none opacity-0 transition-opacity duration-200 peer-checked:opacity-100" 
          fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="3"
        >
          <path stroke-linecap="round" stroke-linejoin="round" d="M5 13l4 4L19 7" />
        </svg>
      </div>
      <span *ngIf="label" class="ml-2.5 text-sm text-secondary-700 group-hover:text-secondary-900 transition-colors">
        {{ label }}
        <span *ngIf="required" class="text-red-500">*</span>
      </span>
    </label>
  `,
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => ZrdCheckboxComponent),
      multi: true
    }
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ZrdCheckboxComponent implements ControlValueAccessor {
  @Input() id = `zrd-checkbox-${Math.random().toString(36).substr(2, 9)}`;
  @Input() label?: string;
  @Input({ transform: booleanAttribute }) required = false;

  value = false;
  disabled = false;

  onChange: any = () => {};
  onTouched: any = () => {};

  onCheckboxChange(event: any) {
    const val = event.target.checked;
    this.value = val;
    this.onChange(val);
  }

  onBlur() {
    this.onTouched();
  }

  writeValue(value: any): void {
    this.value = !!value;
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
