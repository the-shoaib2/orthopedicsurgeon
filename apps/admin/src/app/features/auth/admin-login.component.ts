import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  ReactiveFormsModule,
  FormsModule,
  FormBuilder,
  FormGroup,
  Validators,
  AbstractControl,
  ValidationErrors
} from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDividerModule } from '@angular/material/divider';
import { AuthService } from '@repo/auth';
import { environment } from '../../../environments/environment';

declare var google: any;

@Component({
  selector: 'app-admin-login',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FormsModule,
    RouterModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatFormFieldModule,
    MatInputModule,
    MatProgressSpinnerModule,
    MatSnackBarModule
  ],
  template: `
    <div class="min-h-screen flex items-center justify-center p-6 bg-slate-50 relative overflow-hidden">
      <div class="absolute inset-0 bg-[radial-gradient(circle_at_top,_#e2e8f0_0%,_transparent_50%)] pointer-events-none"></div>
      
      <mat-card class="w-full max-w-md relative z-10">
        @if (step() === 'login') {
          <mat-card-header class="flex flex-col items-center pt-8 pb-4">
             <div class="mb-4 text-primary-600">
                <mat-icon class="scale-[2]">admin_panel_settings</mat-icon>
             </div>
             <mat-card-title class="text-2xl font-medium m-0 text-center">Welcome Back</mat-card-title>
             <mat-card-subtitle class="mt-2 text-sm text-slate-500 text-center">Sign in to your administrative console</mat-card-subtitle>
          </mat-card-header>

          <mat-card-content class="px-6 pb-6">
            <form [formGroup]="loginForm" (ngSubmit)="onSubmit()" class="flex flex-col gap-4">
               <mat-form-field appearance="outline" class="w-full">
                  <mat-label>Email Address</mat-label>
                  <input matInput type="email" formControlName="email">
                  <mat-error>
                      @if (loginForm.get('email')?.hasError('required')) { Email is required }
                      @else if (loginForm.get('email')?.hasError('email')) { Invalid email format }
                  </mat-error>
               </mat-form-field>

               <div class="flex flex-col gap-1">
                  <mat-form-field appearance="outline" class="w-full">
                      <mat-label>Password</mat-label>
                      <input matInput [type]="hidePassword() ? 'password' : 'text'" formControlName="password">
                      <button mat-icon-button matSuffix (click)="hidePassword.set(!hidePassword())" type="button">
                        <mat-icon>{{hidePassword() ? 'visibility_off' : 'visibility'}}</mat-icon>
                      </button>
                      @if (loginForm.get('password')?.hasError('required')) {
                        <mat-error>Password is required</mat-error>
                      }
                  </mat-form-field>
                  <div class="flex justify-end">
                     <a routerLink="/auth/forgot-password" class="text-sm font-medium text-primary-600 hover:underline">
                        Forgot password?
                     </a>
                  </div>
               </div>

               <button mat-flat-button color="primary" 
                       [disabled]="loading()"
                       class="w-full py-2 mt-2">
                  @if (!loading()) {
                    <span>Continue</span>
                  } @else {
                    <mat-spinner diameter="24" class="inline-block"></mat-spinner>
                  }
               </button>
               
               <div class="relative my-2">
                  <div class="absolute inset-0 flex items-center"><div class="w-full border-t border-slate-100"></div></div>
                  <div class="relative flex justify-center text-xs uppercase"><span class="bg-white px-2 text-slate-400 font-bold tracking-wider">Or continue with</span></div>
               </div>

               <button type="button" mat-stroked-button (click)="onGoogleLogin()" 
                       [disabled]="loading()"
                       class="w-full py-2">
                  <div class="flex items-center justify-center gap-3">
                     <svg class="w-5 h-5" viewBox="0 0 24 24">
                        <path d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z" fill="#4285F4"/>
                        <path d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z" fill="#34A853"/>
                        <path d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l3.66-2.84z" fill="#FBBC05"/>
                        <path d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z" fill="#EA4335"/>
                     </svg>
                     Sign in with Google
                  </div>
               </button>
            </form>
          </mat-card-content>
        } @else if (step() === '2fa') {
          <mat-card-header class="flex flex-col items-center pt-8 pb-4">
             <div class="mb-4 text-primary-600">
                <mat-icon class="scale-[2]">security</mat-icon>
             </div>
             <mat-card-title class="text-2xl font-medium m-0">Two-Factor Auth</mat-card-title>
             <mat-card-subtitle class="mt-2 text-sm text-slate-500 text-center px-4">Enter the 6-digit code from your authenticator app</mat-card-subtitle>
          </mat-card-header>

          <mat-card-content class="px-6 pb-6">
            <form (ngSubmit)="on2faSubmit()" class="flex flex-col gap-4">
               <mat-form-field appearance="outline" class="w-full">
                  <mat-label>Verification Code</mat-label>
                  <input matInput type="text" 
                         [ngModel]="totpCode()" 
                         (ngModelChange)="totpCode.set($event)"
                         name="totpCode" maxlength="6" 
                         class="text-center text-2xl tracking-[0.5em] font-bold">
               </mat-form-field>

               <button mat-flat-button color="primary" 
                       [disabled]="loading() || totpCode().length !== 6"
                       class="w-full py-2 mt-2">
                  @if (!loading()) {
                    <span>Verify & Access</span>
                  } @else {
                    <mat-spinner diameter="24" class="inline-block"></mat-spinner>
                  }
               </button>
               
               <p class="text-center text-xs text-slate-400 mt-4">
                  Having trouble? <a href="#" class="text-primary-600 font-bold hover:underline">Contact Security</a>
               </p>
               
               <button mat-button (click)="step.set('login')" type="button" class="mt-2">
                  Back to Login
               </button>
            </form>
          </mat-card-content>
        }

        <mat-card-footer class="py-4 text-center">
           <span class="text-xs text-slate-500">Admin Console &copy; 2026</span>
        </mat-card-footer>
      </mat-card>
    </div>
  `,
  styles: [`
    :host { display: block; }
    ::ng-deep .success-snackbar { --mdc-snackbar-container-color: #059669; --mdc-snackbar-supporting-text-color: white; }
    ::ng-deep .error-snackbar { --mdc-snackbar-container-color: #dc2626; --mdc-snackbar-supporting-text-color: white; }
  `]
})
export class AdminLoginComponent {
  private fb = inject(FormBuilder);
  private auth = inject(AuthService);
  private router = inject(Router);
  private snackBar = inject(MatSnackBar);

  ngOnInit() {
    this.initGoogleAuth();
  }

  private initGoogleAuth() {
    if (typeof google === 'undefined') {
      setTimeout(() => this.initGoogleAuth(), 500);
      return;
    }

    google.accounts.id.initialize({
      client_id: environment.googleClientId,
      callback: (response: any) => this.handleGoogleResponse(response)
    });
  }

  private handleGoogleResponse(response: any) {
    if (response.credential) {
      this.loading.set(true);
      this.auth.googleLogin(response.credential).subscribe({
        next: (res: any) => {
          this.loading.set(false);
          if (res.requiresTwoFactor) {
            this.tempToken.set(res.tempToken);
            this.step.set('2fa');
          } else {
            this.showSuccessAndNavigate();
          }
        },
        error: (err) => {
          this.loading.set(false);
          this.showError(err.error?.message || 'Google login failed.');
        }
      });
    }
  }

  loginForm: FormGroup = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', Validators.required]
  });

  loading = signal(false);
  hidePassword = signal(true);
  step = signal<'login' | '2fa'>('login');
  tempToken = signal<string | null>(null);
  totpCode = signal('');

  onSubmit() {
    if (this.loginForm.invalid) return;
    this.loading.set(true);
    
    this.auth.login(this.loginForm.value).subscribe({
      next: (res: any) => {
        this.loading.set(false);
        if (res.requiresTwoFactor) {
          this.tempToken.set(res.tempToken);
          this.step.set('2fa');
        } else {
          this.showSuccessAndNavigate();
        }
      },
      error: (err) => {
        this.loading.set(false);
        this.showError(err.error?.message || 'Invalid email or password.');
      }
    });
  }

  on2faSubmit() {
    const token = this.tempToken();
    if (!token || this.totpCode().length !== 6) return;
    this.loading.set(true);
    
    this.auth.verify2fa({ tempToken: token, totpCode: this.totpCode() }).subscribe({
      next: () => {
        this.loading.set(false);
        this.showSuccessAndNavigate();
      },
      error: (err: any) => {
        this.loading.set(false);
        this.showError(err.error?.message || 'Invalid 2FA code.');
      }
    });
  }

  onGoogleLogin() {
    google.accounts.id.prompt();
  }

  private showSuccessAndNavigate() {
    this.snackBar.open('Access Granted: Welcome to Precision Console', 'Close', { 
      duration: 3000,
      panelClass: ['success-snackbar']
    });
    this.router.navigate(['/dashboard']);
  }

  private showError(message: string) {
    this.snackBar.open(message, 'Close', { 
      duration: 5000,
      panelClass: ['error-snackbar']
    });
  }
}
