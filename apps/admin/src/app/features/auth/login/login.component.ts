import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { 
  FormControl, 
  FormGroupDirective, 
  NgForm, 
  Validators, 
  FormsModule, 
  ReactiveFormsModule, 
  FormBuilder 
} from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { ErrorStateMatcher } from '@angular/material/core';
import { AuthService } from '@repo/auth';
import { TranslateModule } from '@ngx-translate/core';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { firstValueFrom } from 'rxjs';

/** Error when invalid control is dirty, touched, or submitted. */
export class MyErrorStateMatcher implements ErrorStateMatcher {
  isErrorState(control: FormControl | null, form: FormGroupDirective | NgForm | null): boolean {
    const isSubmitted = form && form.submitted;
    return !!(control && control.invalid && (control.dirty || control.touched || isSubmitted));
  }
}

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    RouterModule,
    TranslateModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatSnackBarModule
  ],
  template: `
    <div class="min-h-screen flex items-center justify-center p-6 bg-white relative overflow-hidden">
      <mat-card class="w-full max-w-md relative z-10">
        <mat-card-header class="flex flex-col items-center pt-8 pb-4">
          <div class="mb-4 text-primary-600">
            <mat-icon class="scale-[2]">admin_panel_settings</mat-icon>
          </div>
          <mat-card-title class="text-2xl font-medium m-0 text-center">
            Precision Admin
          </mat-card-title>
          <mat-card-subtitle class="mt-2 text-sm text-slate-500 text-center">
            Enter your credentials to access the console
          </mat-card-subtitle>
        </mat-card-header>

        <mat-card-content class="px-6 pb-6">
          <form [formGroup]="loginForm" (ngSubmit)="onSubmit()" class="flex flex-col gap-4">
            <mat-form-field appearance="outline" class="w-full">
              <mat-label>Email Address</mat-label>
              <input matInput formControlName="email" type="email" [errorStateMatcher]="matcher" autocomplete="username">
              @if (loginForm.get('email')?.hasError('required')) {
                <mat-error>Email is required</mat-error>
              }
              @if (loginForm.get('email')?.hasError('email')) {
                <mat-error>Enter a valid email</mat-error>
              }
            </mat-form-field>

            <mat-form-field appearance="outline" class="w-full">
              <mat-label>Password</mat-label>
              <input matInput [type]="hidePassword() ? 'password' : 'text'" formControlName="password" [errorStateMatcher]="matcher" autocomplete="current-password">
              <button mat-icon-button matSuffix (click)="hidePassword.set(!hidePassword())" type="button" [attr.aria-label]="'Hide password'" [attr.aria-pressed]="hidePassword()">
                <mat-icon>{{hidePassword() ? 'visibility_off' : 'visibility'}}</mat-icon>
              </button>
              @if (loginForm.get('password')?.hasError('required')) {
                <mat-error>Password is required</mat-error>
              }
            </mat-form-field>

            <div class="flex justify-end -mt-2">
              <a routerLink="/auth/forgot-password" class="text-sm font-medium text-primary-600 hover:underline">
                Forgot password?
              </a>
            </div>

            <button mat-flat-button color="primary" 
                    class="w-full py-2" 
                    [disabled]="loginForm.invalid || loading()">
              @if (loading()) {
                <mat-spinner diameter="24" class="inline-block"></mat-spinner>
              } @else {
                Sign In
              }
            </button>
          </form>
        </mat-card-content>

        <mat-card-footer class="py-4 text-center">
           <span class="text-xs text-slate-500">Admin Console &copy; 2026</span>
        </mat-card-footer>
      </mat-card>
    </div>
  `
  })
export class LoginComponent {
  private fb = inject(FormBuilder);
  private auth = inject(AuthService);
  private router = inject(Router);
  private snackBar = inject(MatSnackBar);

  loading = signal(false);
  hidePassword = signal(true);
  matcher = new MyErrorStateMatcher();

  loginForm = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(6)]]
  });

  async onSubmit() {
    if (this.loginForm.valid) {
      this.loading.set(true);
      try {
        const { email, password } = this.loginForm.getRawValue();
        const response = await firstValueFrom(this.auth.login({ email, password }));
        if (response && response.accessToken) {
          this.snackBar.open('Logged in successfully', 'Close', { duration: 3000, panelClass: ['success-snackbar'] });
          this.router.navigate(['/dashboard']);
        } else {
          this.snackBar.open('Invalid credentials', 'Close', { duration: 5000, panelClass: ['error-snackbar'] });
        }
      } catch (error) {
        this.snackBar.open('An error occurred during login', 'Close', { duration: 5000, panelClass: ['error-snackbar'] });
      } finally {
        this.loading.set(false);
      }
    }
  }
}
