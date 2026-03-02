import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ZrdSidebarComponent, ZrdNavItem, ZrdAvatarComponent, ZrdDropdownComponent, ZrdButtonComponent } from '@repo/ui';
import { AuthService } from '@repo/auth';

@Component({
  selector: 'app-admin-layout',
  standalone: true,
  imports: [CommonModule, RouterModule, ZrdSidebarComponent, ZrdAvatarComponent, ZrdDropdownComponent, ZrdButtonComponent],
  template: `
    <div class="flex h-screen bg-secondary-900 overflow-hidden">
      <!-- Sidebar -->
      <zrd-sidebar [items]="navItems" [collapsed]="sidebarCollapsed()" class="shadow-2xl"></zrd-sidebar>

      <!-- Main Content -->
      <div class="flex-1 flex flex-col min-w-0 overflow-hidden bg-white rounded-l-[40px] my-3 mr-3 shadow-2xl relative">
        <!-- Header -->
        <header class="h-20 flex items-center justify-between px-10 shrink-0">
          <div class="flex items-center gap-4">
             <button (click)="sidebarCollapsed.set(!sidebarCollapsed())" class="p-2 hover:bg-secondary-50 rounded-xl transition-all">
                <i class="pi pi-bars text-secondary-400"></i>
             </button>
             <h2 class="text-xl font-black text-secondary-900 tracking-tight">Enterprise Console</h2>
          </div>

          <div class="flex items-center gap-6">
            <!-- Global Search -->
            <div class="hidden md:flex items-center bg-secondary-50 px-4 py-2 rounded-xl border border-secondary-100 w-80">
               <i class="pi pi-search text-secondary-400 mr-3"></i>
               <input type="text" placeholder="Search patients, doctors..." class="bg-transparent border-none text-sm outline-none w-full" />
               <span class="text-[10px] font-bold text-secondary-300 bg-white border border-secondary-200 px-1.5 py-0.5 rounded shadow-sm">⌘K</span>
            </div>

            <!-- Notifications -->
            <button class="relative p-2.5 bg-secondary-50 text-secondary-400 rounded-xl hover:text-primary-600 transition-all border border-secondary-100">
              <i class="pi pi-bell"></i>
              <span class="absolute top-2.5 right-2.5 w-2 h-2 bg-red-500 rounded-full border-2 border-white"></span>
            </button>

            <!-- User -->
            <ng-container *ngIf="auth.currentUser() as user">
              <zrd-dropdown [items]="userMenuItems">
                <div trigger class="flex items-center gap-3 cursor-pointer p-1 rounded-xl hover:bg-secondary-50 transition-all">
                  <zrd-avatar [name]="user.firstName + ' ' + user.lastName" size="sm"></zrd-avatar>
                </div>
              </zrd-dropdown>
            </ng-container>
          </div>
        </header>

        <!-- Content Area -->
        <main class="flex-1 overflow-y-auto p-10 custom-scrollbar scroll-smooth">
          <div class="max-w-7xl mx-auto">
            <router-outlet></router-outlet>
          </div>
        </main>
      </div>
    </div>
  `,
  styles: [`
    .custom-scrollbar::-webkit-scrollbar { width: 6px; }
    .custom-scrollbar::-webkit-scrollbar-thumb { background: #cbd5e1; border-radius: 10px; }
    .custom-scrollbar::-webkit-scrollbar-track { background: transparent; }
  `]
})
export class AdminLayoutComponent {
  auth = inject(AuthService);
  sidebarCollapsed = signal(false);

  navItems: ZrdNavItem[] = [
    { label: 'Overview', icon: 'pi pi-chart-line', route: '/dashboard' },
    { label: 'Organization', icon: 'pi pi-building', route: '/hospitals' },
    { label: 'Medical Staff', icon: 'pi pi-users', route: '/doctors' },
    { label: 'Patient Registry', icon: 'pi pi-id-card', route: '/patients' },
    { label: 'Appointments', icon: 'pi pi-calendar', route: '/appointments' },
    { label: 'Medical Records', icon: 'pi pi-file-medical', route: '/records', children: [
        { label: 'Prescriptions', route: '/records/prescriptions' },
        { label: 'Lab Reports', route: '/records/reports' }
    ]},
    { label: 'Financials', icon: 'pi pi-wallet', route: '/finance' },
    { label: 'Audit Systems', icon: 'pi pi-shield', route: '/audit' },
    { label: 'Platform Settings', icon: 'pi pi-cog', route: '/settings' },
  ];

  userMenuItems = [
    { label: 'Control Panel', icon: 'pi pi-cog', action: () => {} },
    { label: 'Switch Context', icon: 'pi pi-refresh', action: () => window.location.href = 'http://localhost:4201' },
    { label: 'Logout', icon: 'pi pi-power-off', danger: true, action: () => this.auth.logout() }
  ];
}
