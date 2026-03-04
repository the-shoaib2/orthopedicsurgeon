import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { 
  FormControl, 
  FormGroupDirective, 
  NgForm, 
  Validators, 
  FormsModule, 
  ReactiveFormsModule, 
  FormBuilder,
  FormGroup 
} from '@angular/forms';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { ErrorStateMatcher } from '@angular/material/core';
import { AuthService } from '@repo/auth';
import { ToastService } from '../../../core/services/toast.service';

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
    RouterModule, 
    ReactiveFormsModule, 
    FormsModule,
    MatInputModule, 
    MatFormFieldModule, 
    MatButtonModule, 
    MatIconModule,
    MatCardModule,
    MatProgressSpinnerModule
  ],
  template: `
    <div>
      <div class="mb-10">
         <h1 class="text-3xl font-bold tracking-tight text-slate-900 mb-2">Welcome Back</h1>
         <p class="text-slate-500 font-medium">Sign in to your patient account</p>
      </div>

      <mat-card class="!shadow-none !border-none !bg-transparent !p-0">
          <mat-card-content class="!p-0">
            <form [formGroup]="loginForm" (ngSubmit)="onSubmit()" class="flex flex-col gap-6">
              <mat-form-field appearance="outline" class="w-full">
                <mat-label>Email Address</mat-label>
                <input matInput type="email" formControlName="email" [errorStateMatcher]="matcher" autocomplete="username">
                <mat-error>
                  @if (loginForm.get('email')?.hasError('required')) { Email is required }
                  @else if (loginForm.get('email')?.hasError('email')) { Invalid email format }
                </mat-error>
              </mat-form-field>

              <div class="space-y-1">
                <mat-form-field appearance="outline" class="w-full">
                  <mat-label>Password</mat-label>
                  <input matInput [type]="hidePassword() ? 'password' : 'text'" formControlName="password" [errorStateMatcher]="matcher" autocomplete="current-password">
                  <button mat-icon-button matSuffix (click)="hidePassword.set(!hidePassword())" type="button">
                    <mat-icon>{{hidePassword() ? 'visibility_off' : 'visibility'}}</mat-icon>
                  </button>
                  @if (loginForm.get('password')?.hasError('required')) {
                    <mat-error>Password is required</mat-error>
                  }
                </mat-form-field>
                <div class="flex justify-end pr-1">
                  <a routerLink="/auth/forgot-password" class="text-xs font-semibold text-primary-600 hover:text-primary-700 transition-colors">Forgot password?</a>
                </div>
              </div>

              <button mat-flat-button color="primary" 
                      [disabled]="loading() || loginForm.invalid"
                      class="w-full h-12 rounded-xl text-base font-semibold transition-all mt-2">
                @if (!loading()) {
                  <span>Sign In</span>
                } @else {
                  <mat-spinner diameter="24" class="inline-block"></mat-spinner>
                }
              </button>
            </form>
          </mat-card-content>
      </mat-card>

      <div class="mt-12 text-center bg-slate-50 p-8 rounded-3xl border border-slate-100">
        <p class="text-sm font-medium text-slate-500 mb-4 tracking-tight">Don't have an account yet?</p>
        <button mat-stroked-button color="primary" routerLink="/auth/register" class="w-full h-12 rounded-xl font-bold border-2 hover:bg-white transition-colors">
          Create Patient Profile
        </button>
      </div>
    </div>
  `,
  styles: [`
    :host { display: block; }
  `]
})
export class LoginComponent {
  private fb = inject(FormBuilder);
  private auth = inject(AuthService);
  private router = inject(Router);
  private toast = inject(ToastService);

  loginForm: FormGroup = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', Validators.required]
  });

  loading = signal(false);
  hidePassword = signal(true);
  matcher = new MyErrorStateMatcher();

  onSubmit() {
    if (this.loginForm.invalid) return;

    this.loading.set(true);

    this.auth.login(this.loginForm.value).subscribe({
      next: () => {
        this.loading.set(false);
        this.toast.success('Welcome back! Syncing your data...');
        this.router.navigate(['/portal/dashboard']);
      },
      error: (err) => {
        this.loading.set(false);
        this.toast.error(err.error?.message || 'Invalid email or password.');
      }
    });
  }
}
