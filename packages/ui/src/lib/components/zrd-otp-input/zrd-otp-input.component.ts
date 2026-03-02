import { ChangeDetectionStrategy, Component, ElementRef, EventEmitter, Input, Output, QueryList, ViewChildren, forwardRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';

@Component({
  selector: 'zrd-otp-input',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="flex gap-2 justify-center">
      <input
        *ngFor="let digit of [].constructor(length); let i = index"
        #otpInput
        type="text"
        inputmode="numeric"
        maxlength="1"
        [disabled]="disabled"
        (input)="onInput($event, i)"
        (keydown)="onKeyDown($event, i)"
        (paste)="onPaste($event)"
        class="w-12 h-14 text-center text-xl font-bold bg-white border-2 border-secondary-200 rounded-xl focus:border-primary-600 focus:ring-4 focus:ring-primary-600/10 transition-all outline-none disabled:bg-secondary-50 disabled:text-secondary-400"
      />
    </div>
  `,
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => ZrdOtpInputComponent),
      multi: true
    }
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ZrdOtpInputComponent implements ControlValueAccessor {
  @Input() length = 6;
  @Output() completed = new EventEmitter<string>();

  @ViewChildren('otpInput') inputs!: QueryList<ElementRef>;

  otpValues: string[] = [];
  disabled = false;

  onChange: any = () => {};
  onTouched: any = () => {};

  ngOnInit() {
    this.otpValues = new Array(this.length).fill('');
  }

  onInput(event: any, index: number) {
    const value = event.target.value;
    if (!/^\d$/.test(value)) {
      event.target.value = '';
      return;
    }

    this.otpValues[index] = value;
    this.updateValue();

    if (value && index < this.length - 1) {
      this.inputs.toArray()[index + 1].nativeElement.focus();
    }
  }

  onKeyDown(event: KeyboardEvent, index: number) {
    if (event.key === 'Backspace' && !this.otpValues[index] && index > 0) {
      this.inputs.toArray()[index - 1].nativeElement.focus();
    }
  }

  onPaste(event: ClipboardEvent) {
    event.preventDefault();
    const pasteData = event.clipboardData?.getData('text').slice(0, this.length);
    if (!pasteData || !/^\d+$/.test(pasteData)) return;

    const digits = pasteData.split('');
    digits.forEach((digit, i) => {
      if (i < this.length) {
        this.otpValues[i] = digit;
        const input = this.inputs.toArray()[i].nativeElement;
        input.value = digit;
      }
    });

    this.updateValue();
    const lastIndex = Math.min(digits.length, this.length - 1);
    this.inputs.toArray()[lastIndex].nativeElement.focus();
  }

  private updateValue() {
    const code = this.otpValues.join('');
    this.onChange(code);
    if (code.length === this.length) {
      this.completed.emit(code);
    }
  }

  writeValue(value: any): void {
    if (value && typeof value === 'string') {
      this.otpValues = value.padEnd(this.length, ' ').split('').slice(0, this.length);
    }
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
