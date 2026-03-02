import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-totp-setup',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="setup-container">
      <h2>Two-Factor Authentication Setup</h2>
      <div *ngIf="setupData">
        <p>1. Scan this QR code with your authenticator app (e.g., Google Authenticator, Authy):</p>
        <img [src]="setupData.qrCodeUrl" alt="QR Code" />
        <p>2. Or enter this secret key manually: <code>{{ setupData.secretKey }}</code></p>
        <p>3. Enter the 6-digit code to verify and enable 2FA:</p>
        <input type="text" [(ngModel)]="verificationCode" maxlength="6" />
        <button (click)="verify()">Verify & Enable</button>
        
        <div *ngIf="setupData.backupCodes" class="backup-codes">
          <p><strong>IMPORTANT:</strong> Save these backup codes in a safe place. They will not be shown again:</p>
          <ul>
            <li *ngFor="let code of setupData.backupCodes">{{ code }}</li>
          </ul>
        </div>
      </div>
      <div *ngIf="error" class="error">{{ error }}</div>
      <div *ngIf="success" class="success">2FA has been successfully enabled!</div>
    </div>
  `,
  styles: [`
    .setup-container { max-width: 500px; margin: 2rem auto; padding: 2rem; border: 1px solid #ddd; }
    img { display: block; margin: 1rem auto; }
    .backup-codes { background: #f9f9f9; padding: 1rem; margin-top: 1rem; border-left: 4px solid #ffcc00; }
    .error { color: red; }
    .success { color: green; }
  `]
})
export class TotpSetupComponent implements OnInit {
  setupData: any = null;
  verificationCode: string = '';
  error: string | null = null;
  success: boolean = false;

  constructor(private http: HttpClient) {}

  ngOnInit() {
    this.http.post('/api/v1/auth/2fa/setup', {}).subscribe({
      next: (res) => this.setupData = res,
      error: (err) => this.error = 'Failed to load 2FA setup data.'
    });
  }

  verify() {
    this.http.post('/api/v1/auth/2fa/confirm-setup', this.verificationCode).subscribe({
      next: () => {
        this.success = true;
        this.error = null;
      },
      error: () => this.error = 'Invalid code. Verification failed.'
    });
  }
}
