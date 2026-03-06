import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';

@Component({
  selector: 'app-totp-setup',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    RouterModule,
    MatCardModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatIconModule,
    MatSnackBarModule
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
          <mat-card class="w-full border border-slate-200 shadow-xl shadow-slate-200/50 rounded-2xl">
            <mat-card-header class="flex flex-col items-center pt-8 pb-4">
              <div class="mb-4 text-primary-600">
                <mat-icon class="scale-[2]">security</mat-icon>
              </div>
              <mat-card-title class="text-2xl font-medium m-0 text-center">2FA Setup</mat-card-title>
              <mat-card-subtitle class="mt-2 text-sm text-slate-500 text-center px-4">
                Enable two-factor authentication for your account
              </mat-card-subtitle>
            </mat-card-header>

            <mat-card-content class="px-6 pb-6">
              @if (setupData) {
                <div class="flex flex-col gap-4">
                  <p class="text-sm text-slate-600 text-center">1. Scan this QR code with your authenticator app:</p>
                  <div class="flex justify-center">
                    <img [src]="setupData.qrCodeUrl" alt="QR Code" class="rounded-lg border border-slate-200 p-2 bg-slate-50">
                  </div>
                  <p class="text-sm text-slate-600 text-center">2. Or enter this secret key manually:</p>
                  <code class="block bg-slate-100 rounded px-3 py-2 text-sm text-slate-800 text-center tracking-widest font-mono">
                    {{ setupData.secretKey }}
                  </code>
                  <p class="text-sm text-slate-600 text-center">3. Enter the 6-digit verification code:</p>

                  <mat-form-field appearance="outline" class="w-full">
                    <mat-label>Verification Code</mat-label>
                    <input matInput type="text" [(ngModel)]="verificationCode" maxlength="6"
                           class="text-center tracking-[0.5em] text-lg font-bold">
                  </mat-form-field>

                  @if (setupData.backupCodes) {
                    <div class="bg-amber-50 border border-amber-200 rounded-lg p-4">
                      <p class="text-sm font-semibold text-amber-800 mb-2">⚠ Save these backup codes:</p>
                      <ul class="grid grid-cols-2 gap-1">
                        @for (code of setupData.backupCodes; track code) {
                          <li class="font-mono text-xs text-slate-700">{{ code }}</li>
                        }
                      </ul>
                    </div>
                  }

                  <button mat-flat-button color="primary" (click)="verify()" class="w-full py-2 mt-2">
                    Verify &amp; Enable 2FA
                  </button>
                </div>
              }

              @if (error) {
                <p class="text-red-600 text-sm mt-4 text-center">{{ error }}</p>
              }
              @if (success) {
                <div class="text-center py-6">
                  <mat-icon class="text-5xl text-green-600 mb-4">check_circle</mat-icon>
                  <p class="text-green-700 font-semibold">2FA has been successfully enabled!</p>
                  <button mat-flat-button color="primary" routerLink="/dashboard" class="mt-4">
                    Go to Dashboard
                  </button>
                </div>
              }
              
              <button mat-button routerLink="/auth/login" class="w-full mt-4">
                Back to Login
              </button>
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
export class TotpSetupComponent implements OnInit {
  setupData: any = null;
  verificationCode: string = '';
  error: string | null = null;
  success: boolean = false;

  constructor(
    private http: HttpClient,
    private router: Router
  ) {}

  ngOnInit() {
    this.http.post('/api/v1/auth/2fa/setup', {}).subscribe({
      next: (res) => this.setupData = res,
      error: () => this.error = 'Failed to load 2FA setup data.'
    });
  }

  verify() {
    this.http.post('/api/v1/auth/2fa/confirm-setup', { code: this.verificationCode }).subscribe({
      next: () => {
        this.success = true;
        this.error = null;
        // Automatically redirect to dashboard after a short delay
        setTimeout(() => {
          this.router.navigate(['/dashboard']);
        }, 1500);
      },
      error: () => this.error = 'Invalid code. Verification failed.'
    });
  }
}
