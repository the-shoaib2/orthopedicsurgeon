import { ChangeDetectionStrategy, Component, Input, forwardRef, booleanAttribute } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';

@Component({
  selector: 'zrd-toggle',
  standalone: true,
  imports: [CommonModule],
  template: `
    <label [for]="id" class="inline-flex items-center cursor-pointer group">
      <div class="relative">
        <input
          type="checkbox"
          [id]="id"
          [disabled]="disabled"
          [checked]="value"
          (change)="onToggleChange($event)"
          (blur)="onBlur()"
          class="peer sr-only"
        />
        <div 
          class="w-11 h-6 bg-secondary-200 rounded-full peer transition-all duration-200 peer-checked:bg-primary-600 peer-focus:ring-4 peer-focus:ring-primary-500/20 disabled:opacity-50"
        ></div>
        <div 
          class="absolute left-1 top-1 bg-white w-4 h-4 rounded-full transition-all duration-200 peer-checked:translate-x-5"
        ></div>
      </div>
      <span *ngIf="label" class="ml-3 text-sm font-medium text-secondary-700 group-hover:text-secondary-900 transition-colors">
        {{ label }}
      </span>
    </label>
  `,
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => ZrdToggleComponent),
      multi: true
    }
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ZrdToggleComponent implements ControlValueAccessor {
  @Input() id = `zrd-toggle-${Math.random().toString(36).substr(2, 9)}`;
  @Input() label?: string;

  value = false;
  disabled = false;

  onChange: any = () => {};
  onTouched: any = () => {};

  onToggleChange(event: any) {
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
