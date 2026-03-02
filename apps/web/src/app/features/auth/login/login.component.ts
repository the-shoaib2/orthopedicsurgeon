import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ZrdInputComponent, ZrdButtonComponent, ZrdCheckboxComponent } from '@repo/ui';
import { AuthService } from '@repo/auth';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, RouterModule, ReactiveFormsModule, ZrdInputComponent, ZrdButtonComponent, ZrdCheckboxComponent],
  template: `
    <div>
      <h2 class="text-2xl font-black text-secondary-900 mb-2">Sign in to your account</h2>
      <p class="text-sm text-secondary-500 mb-8">Enter your credentials to access your patient portal.</p>

      <form [formGroup]="loginForm" (ngSubmit)="onSubmit()" class="space-y-6">
        <zrd-input 
          label="Email Address" 
          type="email" 
          placeholder="name@example.com" 
          formControlName="email"
          [required]="true"
        ></zrd-input>

        <div class="space-y-1">
          <zrd-input 
            label="Password" 
            type="password" 
            placeholder="••••••••" 
            formControlName="password"
            [required]="true"
          ></zrd-input>
          <div class="flex justify-end">
            <a routerLink="/auth/forgot-password" class="text-xs font-bold text-primary-600 hover:text-primary-700">Forgot password?</a>
          </div>
        </div>

        <zrd-checkbox label="Remember me for 30 days" formControlName="rememberMe"></zrd-checkbox>

        <div *ngIf="error()" class="p-3 bg-red-50 border border-red-100 rounded-lg text-xs text-red-600 animate-in fade-in duration-300">
           {{ error() }}
        </div>

        <button zrdButton variant="primary" size="lg" class="w-full" [loading]="loading()">
          Sign In
        </button>
      </form>

      <p class="mt-8 text-sm text-secondary-500 text-center">
        Don't have an account? 
        <a routerLink="/auth/register" class="font-bold text-primary-600 hover:text-primary-700">Explore and Register</a>
      </p>
    </div>
  `
})
export class LoginComponent {
  private fb = inject(FormBuilder);
  private auth = inject(AuthService);
  private router = inject(Router);

  loginForm: FormGroup = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', Validators.required],
    rememberMe: [false]
  });

  loading = signal(false);
  error = signal<string | null>(null);

  onSubmit() {
    if (this.loginForm.invalid) return;

    this.loading.set(true);
    this.error.set(null);

    this.auth.login(this.loginForm.value).subscribe({
      next: () => {
        this.loading.set(false);
        this.router.navigate(['/portal/dashboard']);
      },
      error: (err) => {
        this.loading.set(false);
        this.error.set(err.error?.message || 'Invalid email or password. Please try again.');
      }
    });
  }
}
