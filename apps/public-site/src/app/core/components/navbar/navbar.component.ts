import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ZrdButtonComponent, ZrdDropdownComponent, ZrdAvatarComponent } from '@repo/ui';
import { AuthService } from '@repo/auth';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterModule, ZrdButtonComponent, ZrdDropdownComponent, ZrdAvatarComponent],
  template: `
    <nav class="bg-white border-b border-secondary-100 sticky top-0 z-50 backdrop-blur-md bg-white/90">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex justify-between h-20 items-center">
          <!-- Logo -->
          <div class="flex items-center gap-2 cursor-pointer" routerLink="/">
            <div class="w-10 h-10 rounded-xl bg-primary-600 flex items-center justify-center">
              <span class="text-white font-bold text-xl">O</span>
            </div>
            <span class="text-xl font-bold text-secondary-900 tracking-tight">ORTHO<span class="text-primary-600">SYNC</span></span>
          </div>

          <!-- Desktop Nav -->
          <div class="hidden md:flex items-center gap-8">
            <a routerLink="/" routerLinkActive="text-primary-600" [routerLinkActiveOptions]="{exact: true}" class="text-sm font-semibold text-secondary-600 hover:text-primary-600 transition-colors">Home</a>
            <a routerLink="/doctors" routerLinkActive="text-primary-600" class="text-sm font-semibold text-secondary-600 hover:text-primary-600 transition-colors">Find Doctors</a>
            <a routerLink="/hospitals" routerLinkActive="text-primary-600" class="text-sm font-semibold text-secondary-600 hover:text-primary-600 transition-colors">Hospitals</a>
            <a routerLink="/telemedicine" routerLinkActive="text-primary-600" class="text-sm font-semibold text-secondary-600 hover:text-primary-600 transition-colors">Telemedicine</a>
          </div>

          <!-- Actions -->
          <div class="flex items-center gap-4">
            <ng-container *ngIf="auth.currentUser() as user; else guest">
              <zrd-dropdown [items]="userMenuItems">
                <div trigger class="flex items-center gap-2 cursor-pointer p-1 rounded-full hover:bg-secondary-50 transition-all">
                  <zrd-avatar [name]="user.firstName + ' ' + user.lastName" size="sm"></zrd-avatar>
                  <i class="pi pi-chevron-down text-[10px] text-secondary-400"></i>
                </div>
              </zrd-dropdown>
            </ng-container>

            <ng-template #guest>
              <button zrdButton variant="ghost" routerLink="/auth/login">Login</button>
              <button zrdButton variant="primary" routerLink="/auth/register">Get Started</button>
            </ng-template>
          </div>
        </div>
      </div>
    </nav>
  `
})
export class NavbarComponent {
  auth = inject(AuthService);
  
  userMenuItems = [
    { label: 'My Dashboard', icon: 'pi pi-th-large', action: () => window.location.href = 'http://localhost:4200' },
    { label: 'My Appointments', icon: 'pi pi-calendar', action: () => window.location.href = 'http://localhost:4200/appointments' },
    { label: 'Profile Settings', icon: 'pi pi-user', action: () => window.location.href = 'http://localhost:4200/settings' },
    { label: 'Logout', icon: 'pi pi-power-off', danger: true, action: () => this.auth.logout() }
  ];
}
