import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { TranslateModule } from '@ngx-translate/core';

@Component({
  selector: 'app-logout-confirm-dialog',
  standalone: true,
  imports: [CommonModule, MatDialogModule, MatButtonModule, MatIconModule, TranslateModule],
  template: `
    <div class="p-6">
      <div class="flex items-center gap-4 mb-4 text-warn">
        <mat-icon class="scale-125">logout</mat-icon>
        <h2 class="text-xl font-bold m-0">{{ 'COMMON.LOGOUT_CONFIRM_TITLE' | translate }}</h2>
      </div>
      
      <p class="text-slate-600 mb-8">
        {{ 'COMMON.LOGOUT_CONFIRM_MESSAGE' | translate }}
      </p>

      <div class="flex justify-end gap-3">
        <button mat-button (click)="dialogRef.close(false)">
          {{ 'COMMON.CANCEL' | translate }}
        </button>
        <button mat-flat-button color="warn" (click)="dialogRef.close(true)">
          {{ 'COMMON.LOGOUT' | translate }}
        </button>
      </div>
    </div>
  `
})
export class LogoutConfirmDialogComponent {
  constructor(public dialogRef: MatDialogRef<LogoutConfirmDialogComponent>) {}
}
