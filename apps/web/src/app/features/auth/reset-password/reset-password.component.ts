import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
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
import { Router, RouterModule, ActivatedRoute } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
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
  selector: 'app-reset-password',
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
    MatProgressSpinnerModule
  ],
  template: `
    <div class="h-full flex flex-col justify-center relative z-10">
      <div class="mb-8">
        <h1 class="text-3xl font-extrabold tracking-tight text-slate-900 mb-2">Reset Password</h1>
        <p class="text-slate-500 font-medium text-sm">Please enter a new, secure password for your account.</p>
      </div>

      <mat-card class="!shadow-none !border-none !bg-transparent !p-0">
        <mat-card-content class="!p-0">
          <form [formGroup]="resetForm" (ngSubmit)="onSubmit()" class="flex flex-col gap-6">
            
            <mat-form-field appearance="outline" class="w-full">
              <mat-label>Email Address</mat-label>
              <input matInput type="email" formControlName="email" [errorStateMatcher]="matcher">
              <mat-error>Correct email is required</mat-error>
            </mat-form-field>

            <mat-form-field appearance="outline" class="w-full">
              <mat-label>New Password</mat-label>
              <input matInput [type]="hidePassword() ? 'password' : 'text'" formControlName="newPassword" [errorStateMatcher]="matcher">
              <button mat-icon-button matSuffix (click)="hidePassword.set(!hidePassword())" type="button">
                <mat-icon>{{hidePassword() ? 'visibility_off' : 'visibility'}}</mat-icon>
              </button>
              <mat-error>Minimum 8 characters required</mat-error>
            </mat-form-field>

            <button mat-flat-button color="primary" 
                    [disabled]="loading() || resetForm.invalid"
                    class="w-full h-12 rounded-xl text-base font-bold shadow-lg shadow-primary-500/20">
              @if (!loading()) {
                <span>Update Password</span>
              } @else {
                <mat-spinner diameter="24" class="inline-block"></mat-spinner>
              }
            </button>
          </form>
        </mat-card-content>
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
  private toast = inject(ToastService);

  resetForm: FormGroup = this.fb.group({
    token: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    newPassword: ['', [Validators.required, Validators.minLength(8)]]
  });

  loading = signal(false);
  hidePassword = signal(true);
  matcher = new MyErrorStateMatcher();

  ngOnInit() {
    const token = this.route.snapshot.queryParamMap.get('token');
    if (token) {
      this.resetForm.patchValue({ token });
    } else {
      this.toast.error('Invalid or missing reset token.');
      this.router.navigate(['/auth/login']);
    }
  }

  onSubmit() {
    if (this.resetForm.invalid) return;
    this.loading.set(true);
    
    this.auth.resetPassword(this.resetForm.value).subscribe({
      next: () => {
        this.loading.set(false);
        this.toast.success('Password updated successfully!');
        this.router.navigate(['/auth/login']);
      },
      error: (err) => {
        this.loading.set(false);
        this.toast.error(err.error?.message || 'Failed to reset password.');
      }
    });
  }
}
