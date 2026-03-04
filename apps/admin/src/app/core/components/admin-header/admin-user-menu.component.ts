import {
  Component,
  inject,
  signal,
  HostListener,
  ElementRef,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatRippleModule } from '@angular/material/core';
import { MatDividerModule } from '@angular/material/divider';
import { MatTooltipModule } from '@angular/material/tooltip';
import { AuthService } from '@repo/auth';

@Component({
  selector: 'app-admin-user-menu',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatIconModule,
    MatButtonModule,
    MatRippleModule,
    MatDividerModule,
    MatTooltipModule,
  ],
  template: `
    @if (auth.currentUser(); as user) {
      <!-- Trigger -->
      <button class="avatar-trigger" (click)="toggle()"
              [matTooltip]="user.email" matTooltipPosition="below"
              aria-label="Open account menu">
        <img [src]="avatarUrl(user)" [alt]="user.firstName" class="avatar-img" />
      </button>

      <!-- Floating Panel -->
      @if (isOpen()) {
        <div class="menu-backdrop" (click)="close()"></div>
        <div class="menu-panel" role="dialog" aria-modal="true">

          <!-- Header row -->
          <div class="panel-header">
            <span class="header-email">{{ user.email }}</span>
            <button class="close-btn" (click)="close()" aria-label="Close" matRipple>
              <mat-icon>close</mat-icon>
            </button>
          </div>

          <!-- Profile card -->
          <div class="profile-card">
            <div class="avatar-large-wrapper">
              <img [src]="avatarUrl(user)" [alt]="user.firstName" class="avatar-large" />
              <div class="camera-badge"><mat-icon>photo_camera</mat-icon></div>
            </div>
            <h2 class="greeting">Hi, {{ user.firstName }}!</h2>
            <p class="user-role">{{ getRoleLabel(user.roles) }}</p>
            <a routerLink="/settings" (click)="close()" class="manage-btn" matRipple>
              Manage Account
            </a>
          </div>

          <mat-divider></mat-divider>

          <!-- Menu items -->
          <div class="menu-items">
            <a routerLink="/settings" class="menu-item" (click)="close()" matRipple>
              <div class="menu-item-icon"><mat-icon>manage_accounts</mat-icon></div>
              <span class="menu-item-label">Account Settings</span>
            </a>
            <a routerLink="/settings/security" class="menu-item" (click)="close()" matRipple>
              <div class="menu-item-icon"><mat-icon>security</mat-icon></div>
              <span class="menu-item-label">Security</span>
            </a>
            <a routerLink="/settings/notifications" class="menu-item" (click)="close()" matRipple>
              <div class="menu-item-icon"><mat-icon>notifications_none</mat-icon></div>
              <span class="menu-item-label">Notifications</span>
            </a>
            <div class="menu-item" matRipple (click)="onHelp()">
              <div class="menu-item-icon"><mat-icon>help_outline</mat-icon></div>
              <span class="menu-item-label">Help &amp; Support</span>
            </div>
          </div>

          <mat-divider></mat-divider>

          <!-- Sign out -->
          <div class="panel-footer">
            <button class="logout-btn" (click)="onLogout()" matRipple>
              <mat-icon>logout</mat-icon>
              <span>Sign out</span>
            </button>
          </div>
        </div>
      }
    }
  `,
  styles: [`
    :host { position: relative; display: inline-block; }

    /* Trigger */
    .avatar-trigger {
      width: 34px; height: 34px;
      border-radius: 50%; border: 2px solid #e2e8f0;
      padding: 0; cursor: pointer; background: none; outline: none;
      transition: border-color 0.2s, box-shadow 0.2s;
      display: flex; align-items: center; justify-content: center;
    }
    .avatar-trigger:hover { border-color: #4285f4; box-shadow: 0 0 0 3px rgba(66,133,244,0.15); }
    .avatar-img { width: 100%; height: 100%; border-radius: 50%; object-fit: cover; display: block; }

    /* Backdrop */
    .menu-backdrop { position: fixed; inset: 0; z-index: 998; }

    /* Panel */
    .menu-panel {
      position: absolute; top: calc(100% + 10px); right: 0; width: 340px;
      background: #ffffff; border: 1px solid #e2e8f0;
      border-radius: 16px; box-shadow: 0 8px 40px rgba(0,0,0,0.12);
      z-index: 999; overflow: hidden;
      animation: panelIn 0.15s cubic-bezier(0.4,0,0.2,1);
    }
    @keyframes panelIn {
      from { opacity: 0; transform: translateY(-6px) scale(0.97); }
      to   { opacity: 1; transform: translateY(0) scale(1); }
    }

    /* Panel Header */
    .panel-header {
      display: flex; align-items: center; justify-content: space-between;
      padding: 14px 16px 10px;
    }
    .header-email { font-size: 13px; color: #475569; font-family: 'Inter', sans-serif; }
    .close-btn {
      width: 30px; height: 30px; border-radius: 50%; border: none;
      background: transparent; cursor: pointer;
      display: flex; align-items: center; justify-content: center;
      color: #94a3b8; transition: background 0.15s;
    }
    .close-btn:hover { background: #f1f5f9; }
    .close-btn mat-icon { font-size: 17px; width: 17px; height: 17px; }

    /* Profile Card */
    .profile-card {
      display: flex; flex-direction: column; align-items: center;
      padding: 6px 24px 22px;
    }
    .avatar-large-wrapper { position: relative; margin-bottom: 10px; cursor: pointer; }
    .avatar-large { width: 72px; height: 72px; border-radius: 50%; object-fit: cover; display: block; border: 3px solid #f1f5f9; }
    .camera-badge {
      position: absolute; bottom: 2px; right: 2px;
      width: 22px; height: 22px; background: #f1f5f9;
      border-radius: 50%; display: flex; align-items: center; justify-content: center;
      border: 2px solid #fff;
    }
    .camera-badge mat-icon { font-size: 12px; width: 12px; height: 12px; color: #64748b; }
    .greeting { font-size: 17px; font-weight: 600; color: #0f172a; margin: 0 0 3px; font-family: 'Inter', sans-serif; }
    .user-role { font-size: 12px; color: #64748b; margin: 0 0 14px; }
    .manage-btn {
      display: inline-block; padding: 7px 20px;
      border: 1px solid #e2e8f0; border-radius: 24px;
      font-size: 13px; color: #334155; text-decoration: none;
      font-family: 'Inter', sans-serif; transition: background 0.15s, border-color 0.15s;
    }
    .manage-btn:hover { background: #f8fafc; border-color: #4285f4; color: #4285f4; }

    /* Menu Items */
    .menu-items { padding: 6px 0; }
    .menu-item {
      display: flex; align-items: center; gap: 12px;
      padding: 10px 16px; cursor: pointer; text-decoration: none;
      color: #334155; transition: background 0.1s;
    }
    .menu-item:hover { background: #f8fafc; }
    .menu-item-icon {
      width: 34px; height: 34px; border-radius: 50%;
      background: #f1f5f9; display: flex; align-items: center;
      justify-content: center; flex-shrink: 0;
    }
    .menu-item-icon mat-icon { font-size: 17px; width: 17px; height: 17px; color: #64748b; }
    .menu-item-label { font-size: 14px; font-family: 'Inter', sans-serif; color: #334155; font-weight: 500; }

    /* Footer */
    .panel-footer { padding: 8px 16px 14px; display: flex; justify-content: center; }
    .logout-btn {
      display: flex; align-items: center; gap: 8px; padding: 8px 20px;
      border: 1px solid #e2e8f0; border-radius: 24px;
      background: transparent; cursor: pointer; color: #475569;
      font-size: 13px; font-family: 'Inter', sans-serif;
      transition: background 0.15s, border-color 0.15s, color 0.15s;
    }
    .logout-btn:hover { background: #fef2f2; border-color: #ef4444; color: #ef4444; }
    .logout-btn mat-icon { font-size: 17px; width: 17px; height: 17px; }
  `]
})
export class AdminUserMenuComponent {
  auth = inject(AuthService);
  private elRef = inject(ElementRef);

  isOpen = signal(false);
  toggle() { this.isOpen.update(v => !v); }
  close()  { this.isOpen.set(false); }

  avatarUrl(user: any): string {
    return `https://ui-avatars.com/api/?name=${encodeURIComponent(user.firstName + ' ' + user.lastName)}&background=475569&color=fff&bold=true&size=128`;
  }

  getRoleLabel(roles: string[]): string {
    if (!roles || roles.length === 0) return 'User';
    const map: Record<string, string> = {
      SUPER_ADMIN: 'Super Administrator',
      ADMIN: 'Administrator',
      STAFF: 'Staff Member',
      DOCTOR: 'Doctor',
      PATIENT: 'Patient',
    };
    return map[roles[0]] ?? roles[0];
  }

  onLogout() { this.close(); this.auth.logout(); }
  onHelp()   { this.close(); window.open('mailto:support@orthosync.com', '_blank'); }

  @HostListener('document:keydown.escape')
  onEscape() { this.close(); }
}
