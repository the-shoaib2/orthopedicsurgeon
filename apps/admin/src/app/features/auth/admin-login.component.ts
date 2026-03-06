import { Component, inject, signal, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  ReactiveFormsModule,
  FormsModule,
  FormBuilder,
  FormGroup,
  Validators
} from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { AuthService } from '@repo/auth';
import { environment } from '@env/environment';
import { interval, Subscription } from 'rxjs';
import { takeWhile } from 'rxjs/operators';

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
    <div class="min-h-screen flex flex-col lg:flex-row overflow-hidden bg-white">
      <!-- Left Side: Image -->
      <div class="hidden lg:flex lg:w-1/2 relative bg-slate-100">
        <img src="assets/images/auth-bg.png" alt="Precision Orthopedics" class="absolute inset-0 w-full h-full object-cover">
        <div class="absolute inset-0 bg-primary-900/10 backdrop-blur-[1px]"></div>
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
                   </div>

                   <button mat-flat-button color="primary" 
                           [disabled]="loading() || loginForm.invalid"
                           class="w-full py-2 mt-2">
                      @if (!loading()) {
                        <span>Continue</span>
                      } @else {
                        <mat-spinner diameter="24" class="inline-block"></mat-spinner>
                      }
                   </button>
                   
                   <div class="text-center mt-2">
                     <a routerLink="/auth/forgot-password" class="text-xs text-primary-600 hover:underline">Forgot password?</a>
                   </div>
                   
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
            } @else if (step() === 'mfa') {
              <mat-card-header class="flex flex-col items-center pt-8 pb-4">
                 <div class="mb-4 text-primary-600">
                    <mat-icon class="scale-[2]">mark_email_read</mat-icon>
                 </div>
                 <mat-card-title class="text-2xl font-medium m-0">Two-Factor Auth</mat-card-title>
                 <mat-card-subtitle class="mt-2 text-sm text-slate-500 text-center px-4">
                    Enter the 6-digit code sent to your email
                 </mat-card-subtitle>
              </mat-card-header>

              <mat-card-content class="px-6 pb-6">
                <form (ngSubmit)="onMfaSubmit()" class="flex flex-col gap-4">
                   <div class="bg-slate-50 p-4 rounded-xl border border-slate-100 mb-2">
                       <p class="text-xs text-center text-slate-500 mb-3">Time remaining:</p>
                       <div class="text-3xl font-mono font-bold text-center text-primary-600 tracking-widest">
                          {{ formatTime(timer()) }}
                       </div>
                   </div>

                   <mat-form-field appearance="outline" class="w-full">
                      <mat-label>Verification Code</mat-label>
                      <input matInput type="text" 
                             [ngModel]="otpCode()" 
                             (ngModelChange)="otpCode.set($event)"
                             name="otpCode" maxlength="6" 
                             [disabled]="timer() <= 0"
                             placeholder="000000"
                             class="text-center text-2xl tracking-[0.5em] font-bold">
                   </mat-form-field>

                   @if (timer() <= 0) {
                      <div class="text-center p-3 mb-2 bg-red-50 text-red-600 rounded-lg text-sm font-medium">
                         The verification code has expired.
                      </div>
                   }

                   <button mat-flat-button color="primary" 
                           [disabled]="loading() || otpCode().length !== 6 || timer() <= 0"
                           class="w-full py-2 mt-2">
                      @if (!loading()) {
                        <span>Verify & Access</span>
                      } @else {
                        <mat-spinner diameter="24" class="inline-block"></mat-spinner>
                      }
                   </button>
                   
                   <div class="flex flex-col items-center gap-2 mt-4">
                      <button type="button" mat-button color="primary" 
                              [disabled]="loading() || (timer() > 240)"
                              (click)="onSubmit()">
                         Resend Code {{ timer() > 240 ? '(' + (timer() - 240) + 's)' : '' }}
                      </button>
                      <button mat-button (click)="cancelMfa()" type="button">
                         Back to Login
                      </button>
                   </div>
                </form>
              </mat-card-content>
            }

            <mat-card-footer class="py-4 text-center">
               <span class="text-xs text-slate-500">Admin Console &copy; 2026</span>
            </mat-card-footer>
          </mat-card>
        </div>
      </div>
    </div>
  `,
  styles: [`
    :host { display: block; }
    ::ng-deep .success-snackbar { --mdc-snackbar-container-color: #059669; --mdc-snackbar-supporting-text-color: white; }
    ::ng-deep .error-snackbar { --mdc-snackbar-container-color: #dc2626; --mdc-snackbar-supporting-text-color: white; }
  `]
})
export class AdminLoginComponent implements OnDestroy {
  private fb = inject(FormBuilder);
  private auth = inject(AuthService);
  private router = inject(Router);
  private snackBar = inject(MatSnackBar);

  loginForm: FormGroup = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', Validators.required]
  });

  loading = signal(false);
  hidePassword = signal(true);
  step = signal<'login' | 'mfa'>('login');
  sessionToken = signal<string | null>(null);
  otpCode = signal('');
  timer = signal(300); // 5 minutes in seconds
  private timerSub?: Subscription;

  ngOnInit() {
    this.initGoogleAuth();
  }

  ngOnDestroy() {
    this.stopTimer();
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
          if (res.requiresMfa) {
            this.enterMfaStep(res.sessionToken);
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

  onSubmit() {
    if (this.loginForm.invalid) return;
    this.loading.set(true);
    
    this.auth.login(this.loginForm.value).subscribe({
      next: (res: any) => {
        this.loading.set(false);
        if (res.requiresMfa) {
          this.enterMfaStep(res.sessionToken);
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

  private enterMfaStep(token: string) {
    this.sessionToken.set(token);
    this.otpCode.set('');
    this.step.set('mfa');
    this.startTimer();
    this.snackBar.open('Verification code sent to your email.', 'Close', { duration: 4000 });
  }

  onMfaSubmit() {
    const token = this.sessionToken();
    if (!token || this.otpCode().length !== 6) return;
    this.loading.set(true);
    
    this.auth.verify2fa({ 
      sessionToken: token, 
      code: this.otpCode(),
      deviceFingerprint: this.getDeviceFingerprint()
    }).subscribe({
      next: (res: any) => {
        this.loading.set(false);
        this.stopTimer();
        this.showSuccessAndNavigate();
      },
      error: (err: any) => {
        this.loading.set(false);
        this.showError(err.error?.message || 'Invalid verification code.');
      }
    });
  }

  cancelMfa() {
    this.stopTimer();
    this.step.set('login');
    this.sessionToken.set(null);
  }

  private startTimer() {
    this.stopTimer();
    this.timer.set(300);
    this.timerSub = interval(1000)
      .pipe(takeWhile(() => this.timer() > 0))
      .subscribe(() => {
        this.timer.set(this.timer() - 1);
      });
  }

  private stopTimer() {
    if (this.timerSub) {
      this.timerSub.unsubscribe();
      this.timerSub = undefined;
    }
  }

  formatTime(seconds: number): string {
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
  }

  private getDeviceFingerprint(): string {
    return 'browser-' + window.innerWidth + 'x' + window.innerHeight;
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
