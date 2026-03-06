import { Component, inject, signal, OnInit, HostListener } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';
import { 
  ZrdSidebarComponent, 
  ZrdNavItem 
} from '@repo/ui';
import { AdminUserMenuComponent } from '@core/components/admin-header/user-menu.component';
import { ThemeService } from '@core/services/theme.service';

@Component({
  selector: 'app-admin-layout',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatIconModule,
    MatTooltipModule,
    ZrdSidebarComponent,
    AdminUserMenuComponent
  ],
  template: `
    <div class="flex h-screen overflow-hidden bg-google-gray-50 dark:bg-google-gray-900">
      
      <!-- Spartan Sidebar -->
      <zrd-sidebar 
        [items]="navItems" 
        [collapsed]="collapsed()"
      ></zrd-sidebar>

      <div class="flex-1 flex flex-col min-w-0 transition-all duration-300">
        <!-- Spartan Header -->
        <header class="h-16 flex items-center justify-between px-6 shrink-0 z-20 bg-white/80 dark:bg-google-gray-900/80 backdrop-blur-md border-b border-google-gray-200 dark:border-white/10 sticky top-0">
          
          <div class="flex items-center gap-4">
             <button (click)="toggleCollapsed()" class="p-2 h-10 w-10 flex items-center justify-center rounded-full hover:bg-google-gray-100 dark:hover:bg-white/5 transition-colors">
               <mat-icon class="text-google-gray-600 dark:text-google-gray-400">menu</mat-icon>
             </button>
             
             <div class="hidden md:flex items-center gap-2 text-sm">
               <span class="text-google-gray-400">Admin</span>
               <mat-icon class="text-xs text-google-gray-300">chevron_right</mat-icon>
               <span class="font-medium text-google-gray-900 dark:text-white">Dashboard</span>
             </div>
          </div>

          <!-- Search -->
          <div class="flex-1 max-w-2xl px-8 hidden lg:block">
            <div class="relative group">
              <mat-icon class="absolute left-4 top-1/2 -translate-y-1/2 text-google-gray-400 group-focus-within:text-google-blue transition-colors">search</mat-icon>
              <input 
                type="text" 
                placeholder="Search resources, patients, doctors..." 
                class="w-full bg-google-gray-100 dark:bg-white/5 border-none rounded-pill py-2.5 pl-12 pr-4 text-sm focus:ring-2 focus:ring-google-blue/20 transition-all outline-none"
              />
            </div>
          </div>

          <!-- Actions -->
          <div class="flex items-center gap-2">
            <button (click)="themeService.toggleTheme()" 
                    class="h-10 w-10 flex items-center justify-center rounded-full hover:bg-google-gray-100 dark:hover:bg-white/5 text-google-gray-600 dark:text-google-gray-400 transition-all"
                    matTooltip="Toggle theme">
              <mat-icon>{{ themeService.isDarkMode() ? 'light_mode' : 'dark_mode' }}</mat-icon>
            </button>

            <button class="h-10 w-10 flex items-center justify-center rounded-full hover:bg-google-gray-100 dark:hover:bg-white/5 text-google-gray-600 dark:text-google-gray-400 transition-all relative">
              <mat-icon>notifications_none</mat-icon>
              <span class="absolute top-2 right-2 w-2 h-2 bg-google-red rounded-full border-2 border-white dark:border-google-gray-900"></span>
            </button>

            <div class="w-px h-6 bg-google-gray-200 dark:bg-white/10 mx-2"></div>

            <div class="flex items-center gap-2">
              <app-admin-user-menu></app-admin-user-menu>
            </div>
          </div>
        </header>

        <!-- Main Content -->
        <main class="flex-1 overflow-y-auto custom-scrollbar">
          <div class="p-6 lg:p-8 max-w-[1600px] mx-auto">
            <router-outlet></router-outlet>
          </div>
        </main>
      </div>
    </div>
  `,
  styles: [`
    :host { display: block; height: 100vh; }
    .custom-scrollbar::-webkit-scrollbar { width: 6px; }
    .custom-scrollbar::-webkit-scrollbar-thumb { background: theme('colors.google-gray.300'); border-radius: 10px; }
    .custom-scrollbar::-webkit-scrollbar-track { background: transparent; }
  `]
})
export class AdminLayoutComponent implements OnInit {
  collapsed = signal(false);
  isMobile = signal(false);
  themeService = inject(ThemeService);

  navItems: ZrdNavItem[] = [
    { label: 'Dashboard', icon: 'mat-icon:home', route: '/dashboard' },
    { label: 'Appointments', icon: 'mat-icon:calendar_today', route: '/appointments' },
    { label: 'Doctors', icon: 'mat-icon:medical_services', route: '/doctors' },
    { label: 'Patients', icon: 'mat-icon:people', route: '/patients' },
    { label: 'Prescriptions', icon: 'mat-icon:description', route: '/records/prescriptions' },
    { label: 'Reports', icon: 'mat-icon:assessment', route: '/records/reports' },
    { label: 'Hospitals', icon: 'mat-icon:local_hospital', route: '/hospitals' },
    { label: 'Finance', icon: 'mat-icon:payments', route: '/finance' },
    { label: 'Content Management', icon: 'mat-icon:article', route: '/content/hero' },
    { label: 'Blog', icon: 'mat-icon:rss_feed', route: '/blog' },
    { label: 'Users', icon: 'mat-icon:admin_panel_settings', route: '/users' }
  ];

  ngOnInit() { this.checkBreakpoint(); }

  @HostListener('window:resize')
  checkBreakpoint() {
    const mobile = window.innerWidth < 1024;
    this.isMobile.set(mobile);
    if (mobile) this.collapsed.set(true);
  }

  toggleCollapsed() {
    this.collapsed.update(v => !v);
  }
}
