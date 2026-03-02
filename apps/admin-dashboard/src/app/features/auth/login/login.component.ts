import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '@repo/auth';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <div class="login-container">
      <form [formGroup]="loginForm" (ngSubmit)="onSubmit()">
        <h2>Admin Login</h2>
        <div class="form-group">
          <label>Email</label>
          <input type="email" formControlName="email" />
        </div>
        <div class="form-group">
          <label>Password</label>
          <input type="password" formControlName="password" />
        </div>
        <button type="submit" [disabled]="auth.loading()">
          {{ auth.loading() ? 'Logging in...' : 'Login' }}
        </button>
        <div *ngIf="auth.error()" class="error">{{ auth.error() }}</div>
      </form>
    </div>
  `,
  styles: [`
    .login-container { display: flex; justify-content: center; align-items: center; height: 100vh; }
    form { padding: 2rem; border: 1px solid #ccc; border-radius: 8px; width: 300px; }
    .form-group { margin-bottom: 1rem; }
    input { width: 100%; padding: 0.5rem; }
    .error { color: red; margin-top: 1rem; }
  `]
})
export class LoginComponent {
  private fb = inject(FormBuilder);
  public auth = inject(AuthService);
  private router = inject(Router);

  loginForm = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', Validators.required]
  });

  onSubmit() {
    if (this.loginForm.valid) {
      this.auth.login(this.loginForm.value).subscribe({
        next: () => this.router.navigate(['/dashboard']),
        error: () => {} // Error handled by AuthService signal
      });
    }
  }
}
