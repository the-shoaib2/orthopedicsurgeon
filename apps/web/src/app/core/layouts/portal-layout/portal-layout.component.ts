import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ZrdSidebarComponent, ZrdNavItem, ZrdAvatarComponent, ZrdDropdownComponent } from '@repo/ui';
import { AuthService } from '@repo/auth';

@Component({
  selector: 'app-portal-layout',
  standalone: true,
  imports: [CommonModule, RouterModule, ZrdSidebarComponent, ZrdAvatarComponent, ZrdDropdownComponent],
  template: `
    <div class="flex h-screen bg-secondary-50 overflow-hidden">
      <!-- Sidebar -->
      <zrd-sidebar [items]="navItems" [collapsed]="sidebarCollapsed()"></zrd-sidebar>

      <!-- Main Content -->
      <div class="flex-1 flex flex-col min-w-0 overflow-hidden">
        <!-- Header -->
        <header class="h-16 bg-white border-b border-secondary-200 flex items-center justify-between px-8 shrink-0">
          <div class="flex items-center gap-4">
            <h2 class="text-sm font-bold text-secondary-500 uppercase tracking-widest">Patient Portal</h2>
          </div>

          <div class="flex items-center gap-6">
            <!-- Notifications -->
            <button class="relative p-2 text-secondary-400 hover:text-primary-600 transition-colors">
              <i class="pi pi-bell text-xl"></i>
              <span class="absolute top-1.5 right-1.5 w-2 h-2 bg-red-500 rounded-full border-2 border-white"></span>
            </button>

            <!-- User Menu -->
            <ng-container *ngIf="auth.currentUser() as user">
              <zrd-dropdown [items]="userMenuItems">
                <div trigger class="flex items-center gap-3 cursor-pointer p-1.5 rounded-xl hover:bg-secondary-50 transition-all">
                  <div class="text-right hidden sm:block">
                    <p class="text-sm font-bold text-secondary-900">{{ user.firstName }} {{ user.lastName }}</p>
                    <p class="text-[10px] text-secondary-400 font-medium uppercase tracking-tighter">Verified Patient</p>
                  </div>
                  <zrd-avatar [name]="user.firstName + ' ' + user.lastName" size="sm" border></zrd-avatar>
                </div>
              </zrd-dropdown>
            </ng-container>
          </div>
        </header>

        <!-- Content Area -->
        <main class="flex-1 overflow-y-auto p-8 custom-scrollbar">
          <div class="max-w-6xl mx-auto">
            <router-outlet></router-outlet>
          </div>
        </main>
      </div>
    </div>
  `,
  styles: [`
    .custom-scrollbar::-webkit-scrollbar { width: 6px; }
    .custom-scrollbar::-webkit-scrollbar-thumb { background: #e2e8f0; border-radius: 10px; }
    .custom-scrollbar::-webkit-scrollbar-track { background: transparent; }
  `]
})
export class PortalLayoutComponent {
  auth = inject(AuthService);
  sidebarCollapsed = signal(false);

  navItems: ZrdNavItem[] = [
    { label: 'Dashboard', icon: 'pi pi-th-large', route: '/portal/dashboard' },
    { label: 'My Appointments', icon: 'pi pi-calendar', route: '/portal/appointments' },
    { label: 'Medical History', icon: 'pi pi-file-medical', route: '/portal/history', children: [
        { label: 'Report History', route: '/portal/history/reports' },
        { label: 'Prescriptions', route: '/portal/history/prescriptions' }
    ]},
    { label: 'Payments', icon: 'pi pi-wallet', route: '/portal/payments' },
    { label: 'Settings', icon: 'pi pi-cog', route: '/portal/settings' },
  ];

  userMenuItems = [
    { label: 'Back to Site', icon: 'pi pi-external-link', action: () => window.location.href = '/' },
    { label: 'Profile Settings', icon: 'pi pi-user', action: () => {} },
    { label: 'Logout', icon: 'pi pi-power-off', danger: true, action: () => this.auth.logout() }
  ];
}
