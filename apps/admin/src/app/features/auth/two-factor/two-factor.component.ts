import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

@Component({
  selector: 'app-two-factor',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterModule,
    MatCardModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatIconModule,
    MatProgressSpinnerModule
  ],
  template: `
    <div class="min-h-screen flex flex-col lg:flex-row overflow-hidden bg-white">
      <!-- Left Side: Image -->
      <div class="hidden lg:flex lg:w-1/2 relative bg-slate-100">
        <img src="assets/images/auth-bg.png" alt="Precision Orthopedics" class="absolute inset-0 w-full h-full object-cover">
        <div class="absolute inset-0 bg-primary-900/10 backdrop-blur-[1px]"></div>
        <!-- Blurry End Transition -->
        <div class="absolute inset-y-0 right-0 w-32 bg-gradient-to-r from-transparent to-white pointer-events-none"></div>
        <div class="absolute inset-0 flex flex-col justify-end p-16 text-white bg-gradient-to-t from-slate-900/60 to-transparent">
          <h1 class="text-5xl font-bold mb-4 tracking-tight">Precision Console</h1>
          <p class="text-xl opacity-90 max-w-lg leading-relaxed font-light">
            Advanced Management Interface for Orthopedic Surgeons & Clinical Excellence.
          </p>
        </div>
      </div>

      <!-- Right Side: Form Content -->
      <div class="flex-1 flex items-center justify-center p-8 sm:p-16 bg-white overflow-y-auto">
        <div class="w-full max-w-md">
          <mat-card class="w-full border-none shadow-none">
            <mat-card-header class="flex flex-col items-center pt-8 pb-4">
              <div class="mb-4 text-primary-600">
                <mat-icon class="scale-[2]">lock</mat-icon>
              </div>
              <mat-card-title class="text-2xl font-medium m-0 text-center">2FA Verification</mat-card-title>
              <mat-card-subtitle class="mt-2 text-sm text-slate-500 text-center px-4">
                Enter the 6-digit code from your authenticator app
              </mat-card-subtitle>
            </mat-card-header>

            <mat-card-content class="px-6 pb-6">
              <form [formGroup]="twoFactorForm" (ngSubmit)="onSubmit()" class="flex flex-col gap-4">
                <mat-form-field appearance="outline" class="w-full">
                  <mat-label>Verification Code</mat-label>
                  <input matInput type="text" formControlName="totpCode" maxlength="6"
                         class="text-center tracking-[0.5em] text-lg font-bold">
                  @if (twoFactorForm.get('totpCode')?.hasError('pattern')) {
                    <mat-error>Must be a 6-digit number</mat-error>
                  }
                </mat-form-field>

                @if (error) {
                  <p class="text-red-600 text-sm -mt-2 text-center">{{ error }}</p>
                }

                <button mat-flat-button color="primary" type="submit"
                        [disabled]="twoFactorForm.invalid || loading"
                        class="w-full py-2 mt-2">
                  @if (loading) {
                    <mat-spinner diameter="24" class="inline-block"></mat-spinner>
                  } @else {
                    Verify & Access
                  }
                </button>
                
                <button mat-button routerLink="/auth/login" class="w-full mt-4">
                  Back to Login
                </button>
              </form>
            </mat-card-content>

            <mat-card-footer class="py-4 text-center">
              <span class="text-xs text-slate-500">Admin Console &copy; 2026</span>
            </mat-card-footer>
          </mat-card>
        </div>
      </div>
    </div>
  `,
  styles: [`:host { display: block; }`]
})
export class TwoFactorComponent {
  twoFactorForm: FormGroup;
  error: string | null = null;
  tempToken: string | null = null;
  loading = false;

  constructor(
    private fb: FormBuilder,
    private http: HttpClient,
    private route: ActivatedRoute,
    private router: Router
  ) {
    this.twoFactorForm = this.fb.group({
      totpCode: ['', [Validators.required, Validators.pattern('^[0-9]{6}$')]]
    });
    this.tempToken = this.route.snapshot.queryParamMap.get('tempToken');
  }

  onSubmit() {
    if (this.twoFactorForm.valid && this.tempToken) {
      this.loading = true;
      this.http.post('/api/v1/auth/2fa/verify', {
        tempToken: this.tempToken,
        totpCode: this.twoFactorForm.value.totpCode
      }).subscribe({
        next: () => {
          this.loading = false;
          this.router.navigate(['/dashboard']);
        },
        error: () => {
          this.loading = false;
          this.error = 'Invalid code. Please try again.';
        }
      });
    }
  }
}
