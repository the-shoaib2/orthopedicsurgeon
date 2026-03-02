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
    <nav class="sticky top-0 z-50 bg-background/80 backdrop-blur-xl border-b border-gray-100 font-sans">
      <div class="max-w-7xl mx-auto px-6 sm:px-10 lg:px-12">
        <div class="flex justify-between h-24 items-center">
          <!-- Premium Logo -->
          <div class="flex items-center gap-4 cursor-pointer group" routerLink="/">
            <div class="w-12 h-12 rounded-2xl bg-primary flex items-center justify-center shadow-lg shadow-primary/20 transform group-hover:rotate-6 transition-transform duration-500">
              <span class="text-white font-black text-2xl italic">O</span>
            </div>
            <div class="flex flex-col">
              <span class="text-xl font-black text-foreground tracking-tighter leading-none italic uppercase">OrthoSync</span>
              <span class="text-[9px] font-bold text-primary uppercase tracking-[0.3em] leading-none mt-1">Precision Care</span>
            </div>
          </div>

          <!-- Futuristic Nav -->
          <div class="hidden lg:flex items-center gap-10">
            <a routerLink="/" routerLinkActive="text-primary font-black" [routerLinkActiveOptions]="{exact: true}" class="text-sm font-bold text-foreground/60 hover:text-primary transition-all uppercase tracking-widest">Home</a>
            <a routerLink="/doctors" routerLinkActive="text-primary font-black" class="text-sm font-bold text-foreground/60 hover:text-primary transition-all uppercase tracking-widest">Specialists</a>
            <a routerLink="/hospitals" routerLinkActive="text-primary font-black" class="text-sm font-bold text-foreground/60 hover:text-primary transition-all uppercase tracking-widest">Facilities</a>
            <a routerLink="/portal" routerLinkActive="text-primary font-black" class="text-sm font-bold text-foreground/60 hover:text-primary transition-all uppercase tracking-widest">Patient Portal</a>
          </div>

          <!-- Glassmorphism Actions -->
          <div class="flex items-center gap-6">
            <ng-container *ngIf="auth.currentUser() as user; else guest">
                <zrd-dropdown [items]="userMenuItems" position="bottom-right">
                  <div trigger class="flex items-center gap-4 p-1.5 pr-4 rounded-full bg-gray-50 border border-gray-100 cursor-pointer hover:bg-white hover:shadow-premium transition-all">
                    <zrd-avatar [name]="user.firstName + ' ' + user.lastName" size="sm"></zrd-avatar>
                    <span class="text-xs font-black text-foreground italic uppercase">{{user.firstName}}</span>
                  </div>
                </zrd-dropdown>
            </ng-container>
 
            <ng-template #guest>
              <button routerLink="/auth/login" class="text-sm font-black text-foreground/40 hover:text-primary transition-all uppercase tracking-widest mr-4">Sign In</button>
              <button zrdButton routerLink="/auth/register">Get Started</button>
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
