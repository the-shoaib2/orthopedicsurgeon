import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { ZrdInputComponent, ZrdButtonComponent, ZrdCardComponent } from '@repo/ui';
import { AuthService } from '@repo/auth';

@Component({
  selector: 'app-admin-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, ZrdInputComponent, ZrdButtonComponent, ZrdCardComponent],
  template: `
    <div class="min-h-screen bg-secondary-900 flex items-center justify-center p-6">
       <div class="w-full max-w-md animate-in fade-in zoom-in-95 duration-500">
          <div class="flex items-center gap-3 justify-center mb-10">
             <div class="w-12 h-12 rounded-2xl bg-primary-600 flex items-center justify-center shadow-xl shadow-primary-500/20">
                <span class="text-white font-bold text-2xl">O</span>
             </div>
             <h1 class="text-2xl font-black text-white tracking-tight text-center uppercase">Enterprise Admin</h1>
          </div>

          <zrd-card class="bg-white/95 backdrop-blur-xl border-none shadow-2xl">
              <h2 class="text-xl font-bold text-secondary-900 mb-2">Secure Workplace Access</h2>
              <p class="text-sm text-secondary-500 mb-8">Enter administrative credentials to continue.</p>

              <form [formGroup]="loginForm" (ngSubmit)="onSubmit()" class="space-y-6">
                 <zrd-input label="Email" formControlName="email" type="email" placeholder="admin@orthosync.com"></zrd-input>
                 <zrd-input label="Password" formControlName="password" type="password" placeholder="••••••••"></zrd-input>

                 <div *ngIf="error()" class="p-4 bg-red-50 border border-red-100 rounded-xl text-xs text-red-600">
                    {{ error() }}
                 </div>

                 <button zrdButton variant="primary" size="lg" class="w-full" [loading]="loading()">
                    Authenticate Console
                 </button>
              </form>
          </zrd-card>

          <p class="mt-8 text-center text-secondary-500 text-xs uppercase tracking-widest font-bold">
            Authorized Personnel Only
          </p>
       </div>
    </div>
  `
})
export class AdminLoginComponent {
  private fb = inject(FormBuilder);
  private auth = inject(AuthService);
  private router = inject(Router);

  loginForm: FormGroup = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', Validators.required]
  });

  loading = signal(false);
  error = signal<string | null>(null);

  onSubmit() {
    if (this.loginForm.invalid) return;
    this.loading.set(true);
    this.auth.login(this.loginForm.value).subscribe({
      next: () => {
        this.loading.set(false);
        this.router.navigate(['/dashboard']);
      },
      error: (err) => {
        this.loading.set(false);
        this.error.set(err.error?.message || 'Access Denied: Invalid administrative credentials.');
      }
    });
  }
}
