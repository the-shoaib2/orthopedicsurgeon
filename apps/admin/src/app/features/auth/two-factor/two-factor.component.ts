import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { ActivatedRoute, Router } from '@angular/router';
import { Store } from '@ngrx/store';

@Component({
  selector: 'app-two-factor',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <div class="login-container">
      <form [formGroup]="twoFactorForm" (ngSubmit)="onSubmit()">
        <h2>2FA Verification</h2>
        <p>Enter the 6-digit code from your authenticator app</p>
        <div class="form-group">
          <input type="text" formControlName="totpCode" maxlength="6" />
        </div>
        <button type="submit">Verify</button>
        <div *ngIf="error" class="error">{{ error }}</div>
      </form>
    </div>
  `,
  styles: [`
    .login-container { display: flex; justify-content: center; align-items: center; height: 100vh; }
    form { padding: 2rem; border: 1px solid #ccc; border-radius: 8px; width: 300px; }
    input { width: 100%; padding: 0.5rem; text-align: center; font-size: 1.5rem; letter-spacing: 0.5rem; }
    .error { color: red; margin-top: 1rem; }
  `]
})
export class TwoFactorComponent {
  twoFactorForm: FormGroup;
  error: string | null = null;
  tempToken: string | null = null;

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
      this.http.post('/api/v1/auth/2fa/verify', {
        tempToken: this.tempToken,
        totpCode: this.twoFactorForm.value.totpCode
      }).subscribe({
        next: (res: any) => {
          // Success: Store token and redirect
          this.router.navigate(['/dashboard']);
        },
        error: (err) => this.error = 'Invalid code. Please try again.'
      });
    }
  }
}
