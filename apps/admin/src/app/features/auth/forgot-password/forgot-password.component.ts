import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { AuthService } from '@repo/auth';

@Component({
  selector: 'app-forgot-password',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatFormFieldModule,
    MatInputModule,
    MatSnackBarModule
  ],
  template: `
    <div class="min-h-screen flex items-center justify-center p-6 bg-slate-50 relative overflow-hidden">
      <div class="absolute inset-0 bg-[radial-gradient(circle_at_top,_#e2e8f0_0%,_transparent_50%)] pointer-events-none"></div>
      
      <mat-card class="w-full max-w-md relative z-10">
        <mat-card-header class="flex flex-col items-center pt-8 pb-4">
           <div class="mb-4 text-primary-600">
              <mat-icon class="scale-[2]">lock_reset</mat-icon>
           </div>
           <mat-card-title class="text-2xl font-medium m-0 text-center">Forgot Password?</mat-card-title>
           <mat-card-subtitle class="mt-2 text-sm text-slate-500 text-center px-4">
             Enter your email to receive recovery instructions
           </mat-card-subtitle>
        </mat-card-header>

        <mat-card-content class="px-6 pb-6">
          @if (!submitted()) {
            <form [formGroup]="forgotForm" (ngSubmit)="onSubmit()" class="flex flex-col gap-4">
              <mat-form-field appearance="outline" class="w-full">
                <mat-label>Email Address</mat-label>
                <input matInput type="email" formControlName="email">
                @if (forgotForm.get('email')?.hasError('required')) {
                  <mat-error>Email is required</mat-error>
                }
                @if (forgotForm.get('email')?.hasError('email')) {
                  <mat-error>Invalid email format</mat-error>
                }
              </mat-form-field>

              <button mat-flat-button color="primary" [disabled]="loading()" class="w-full py-2 mt-2">
                @if (!loading()) {
                  <span>Send Instructions</span>
                } @else {
                  <mat-icon class="animate-spin">sync</mat-icon>
                }
              </button>
            </form>
          } @else {
            <div class="text-center py-6">
              <mat-icon class="text-5xl text-primary-600 mb-4">mark_email_read</mat-icon>
              <h3 class="text-lg font-semibold text-slate-900 mb-2">Check your inbox</h3>
              <p class="text-slate-500 text-sm leading-relaxed mb-6">
                If an account exists for <strong>{{forgotForm.get('email')?.value}}</strong>, you will receive reset instructions shortly.
              </p>
              <button mat-stroked-button class="w-full" (click)="submitted.set(false)">
                Try another email
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
  `,
  styles: [`
    :host { display: block; }
  `]
})
export class ForgotPasswordComponent {
  private fb = inject(FormBuilder);
  private auth = inject(AuthService);
  private snackBar = inject(MatSnackBar);

  forgotForm: FormGroup = this.fb.group({
    email: ['', [Validators.required, Validators.email]]
  });

  loading = signal(false);
  submitted = signal(false);

  onSubmit() {
    if (this.forgotForm.invalid) return;
    this.loading.set(true);
    
    this.auth.forgotPassword(this.forgotForm.value.email).subscribe({
      next: () => {
        this.loading.set(false);
        this.submitted.set(true);
      },
      error: (err) => {
        this.loading.set(false);
        this.snackBar.open(err.error?.message || 'Error processing request.', 'Close', { duration: 5000 });
      }
    });
  }
}
