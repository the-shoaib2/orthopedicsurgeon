import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, RouterModule, ActivatedRoute } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { AuthService } from '@repo/auth';

@Component({
  selector: 'app-reset-password',
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
              <mat-icon class="scale-[2]">lock_open</mat-icon>
           </div>
           <mat-card-title class="text-2xl font-medium m-0 text-center">Create New Password</mat-card-title>
           <mat-card-subtitle class="mt-2 text-sm text-slate-500 text-center px-4">
             Set a strong, secure password for your console
           </mat-card-subtitle>
        </mat-card-header>

        <mat-card-content class="px-6 pb-6">
          <form [formGroup]="resetForm" (ngSubmit)="onSubmit()" class="flex flex-col gap-4">
            <mat-form-field appearance="outline" class="w-full">
              <mat-label>Email Address (Confirm)</mat-label>
              <input matInput type="email" formControlName="email">
              <mat-error>Correct email is required</mat-error>
            </mat-form-field>

            <mat-form-field appearance="outline" class="w-full">
              <mat-label>New Password</mat-label>
              <input matInput [type]="hidePassword() ? 'password' : 'text'" formControlName="newPassword">
              <button mat-icon-button matSuffix (click)="hidePassword.set(!hidePassword())" type="button">
                <mat-icon>{{hidePassword() ? 'visibility_off' : 'visibility'}}</mat-icon>
              </button>
              <mat-error>Password must be at least 8 characters</mat-error>
            </mat-form-field>

            <button mat-flat-button color="primary"
                    [disabled]="loading() || resetForm.invalid"
                    class="w-full py-2">
              @if (!loading()) {
                <span>Reset Password</span>
              } @else {
                <mat-icon class="animate-spin">sync</mat-icon>
              }
            </button>
          </form>
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
export class ResetPasswordComponent implements OnInit {
  private fb = inject(FormBuilder);
  private auth = inject(AuthService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private snackBar = inject(MatSnackBar);

  resetForm: FormGroup = this.fb.group({
    token: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    newPassword: ['', [Validators.required, Validators.minLength(8)]]
  });

  loading = signal(false);
  hidePassword = signal(true);

  ngOnInit() {
    const token = this.route.snapshot.queryParamMap.get('token');
    if (token) {
      this.resetForm.patchValue({ token });
    } else {
      this.snackBar.open('Invalid reset link.', 'Close', { duration: 5000 });
      this.router.navigate(['/auth/login']);
    }
  }

  onSubmit() {
    if (this.resetForm.invalid) return;
    this.loading.set(true);
    
    this.auth.resetPassword(this.resetForm.value).subscribe({
      next: () => {
        this.loading.set(false);
        this.snackBar.open('Password reset successfully. Please log in.', 'Close', { 
            duration: 5000,
            panelClass: ['success-snackbar']
        });
        this.router.navigate(['/auth/login']);
      },
      error: (err) => {
        this.loading.set(false);
        this.snackBar.open(err.error?.message || 'Error resetting password.', 'Close', { duration: 5000 });
      }
    });
  }
}
