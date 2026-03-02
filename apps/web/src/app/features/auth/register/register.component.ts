import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ZrdInputComponent, ZrdButtonComponent, ZrdCheckboxComponent, ZrdSelectComponent } from '@repo/ui';
import { AuthService } from '@repo/auth';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, RouterModule, ReactiveFormsModule, ZrdInputComponent, ZrdButtonComponent, ZrdCheckboxComponent, ZrdSelectComponent],
  template: `
    <div>
      <h2 class="text-2xl font-black text-secondary-900 mb-2">Create an account</h2>
      <p class="text-sm text-secondary-500 mb-8">Join the OrthoSync network for better bone health.</p>

      <form [formGroup]="registerForm" (ngSubmit)="onSubmit()" class="space-y-4">
        <div class="grid grid-cols-2 gap-4">
           <zrd-input label="First Name" formControlName="firstName" [required]="true"></zrd-input>
           <zrd-input label="Last Name" formControlName="lastName" [required]="true"></zrd-input>
        </div>

        <zrd-input label="Email Address" type="email" formControlName="email" [required]="true"></zrd-input>
        
        <div class="grid grid-cols-2 gap-4">
           <zrd-select label="Gender" [options]="genderOptions" formControlName="gender"></zrd-select>
           <zrd-input label="Phone" formControlName="phone" [required]="true"></zrd-input>
        </div>

        <zrd-input label="Password" type="password" formControlName="password" [required]="true"></zrd-input>

        <zrd-checkbox label="I agree to the Terms and Privacy Policy" formControlName="terms"></zrd-checkbox>

        <div *ngIf="error()" class="p-3 bg-red-50 border border-red-100 rounded-lg text-xs text-red-600 animate-in fade-in duration-300">
           {{ error() }}
        </div>

        <button zrdButton variant="primary" size="lg" class="w-full mt-4" [loading]="loading()">
          Register Now
        </button>
      </form>

      <p class="mt-8 text-sm text-secondary-500 text-center">
        Already have an account? 
        <a routerLink="/auth/login" class="font-bold text-primary-600 hover:text-primary-700">Login here</a>
      </p>
    </div>
  `
})
export class RegisterComponent {
  private fb = inject(FormBuilder);
  private auth = inject(AuthService);
  private router = inject(Router);

  registerForm: FormGroup = this.fb.group({
    firstName: ['', Validators.required],
    lastName: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    gender: ['MALE'],
    phone: ['', Validators.required],
    password: ['', [Validators.required, Validators.minLength(8)]],
    terms: [false, Validators.requiredTrue]
  });

  genderOptions = [
    { label: 'Male', value: 'MALE' },
    { label: 'Female', value: 'FEMALE' },
    { label: 'Other', value: 'OTHER' }
  ];

  loading = signal(false);
  error = signal<string | null>(null);

  onSubmit() {
    if (this.registerForm.invalid) return;
    this.loading.set(true);
    // Registration logic would go here
    setTimeout(() => {
      this.loading.set(false);
      this.router.navigate(['/auth/login']);
    }, 1500);
  }
}
