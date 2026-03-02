import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ZrdSidebarComponent, ZrdNavItem, ZrdAvatarComponent, ZrdDropdownComponent, ZrdButtonComponent } from '@repo/ui';
import { AuthService } from '@repo/auth';

@Component({
  selector: 'app-admin-layout',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <div class="flex h-screen bg-background overflow-hidden font-sans">
      <!-- Sidebar -->
      <aside [class.w-72]="!sidebarCollapsed()" [class.w-20]="sidebarCollapsed()" class="bg-white/5 border-r border-white/10 transition-all duration-500 glass flex flex-col shrink-0">
        <div class="h-24 flex items-center justify-center border-b border-white/10 px-6 overflow-hidden">
           <div class="flex items-center gap-3 w-full">
             <div class="w-10 h-10 bg-primary rounded-xl flex items-center justify-center shrink-0 shadow-lg shadow-primary/20">
               <span class="text-white font-black italic">A</span>
             </div>
             <span *ngIf="!sidebarCollapsed()" class="text-xl font-black tracking-tight text-white italic truncate">ADMIN PORTAL</span>
           </div>
        </div>

        <nav class="flex-1 py-10 px-4 space-y-2 overflow-y-auto custom-scrollbar">
           <div *ngFor="let item of navItems" 
                [routerLink]="item.route" 
                routerLinkActive="bg-primary/20 text-primary border-primary/20 shadow-lg shadow-primary/5"
                class="flex items-center gap-4 px-4 py-3.5 rounded-xl cursor-pointer hover:bg-white/5 transition-all text-white/50 border border-transparent group">
             <i [class]="item.icon" class="text-xl group-hover:scale-110 transition-transform"></i>
             <span *ngIf="!sidebarCollapsed()" class="font-bold text-sm tracking-wide">{{item.label}}</span>
           </div>
        </nav>

        <div class="p-4 border-t border-white/10">
           <div (click)="sidebarCollapsed.set(!sidebarCollapsed())" class="flex items-center gap-4 px-4 py-3.5 rounded-xl cursor-pointer hover:bg-white/5 transition-all text-white/30 border border-transparent">
             <i class="pi pi-chevron-left transition-transform duration-500" [class.rotate-180]="sidebarCollapsed()"></i>
             <span *ngIf="!sidebarCollapsed()" class="font-bold text-xs uppercase tracking-widest text-white/30">Collapse Menu</span>
           </div>
        </div>
      </aside>

      <!-- Main Content Area -->
      <div class="flex-1 flex flex-col min-w-0 bg-transparent relative overflow-hidden">
        <!-- Top Bar -->
        <header class="h-24 flex items-center justify-between px-10 shrink-0 border-b border-white/5 backdrop-blur-sm z-20">
          <div class="flex flex-col">
            <span class="text-[10px] font-black uppercase tracking-[0.3em] text-primary mb-1">Command Center</span>
            <h2 class="text-2xl font-black text-white tracking-tighter italic uppercase">Precision Console</h2>
          </div>

          <div class="flex items-center gap-8">
            <div class="hidden md:flex items-center bg-white/5 px-6 py-2.5 rounded-full border border-white/10 w-96 group focus-within:border-primary/50 transition-all">
               <i class="pi pi-search text-white/30 mr-3 group-focus-within:text-primary transition-colors"></i>
               <input type="text" placeholder="Search operational matrices..." class="bg-transparent border-none text-sm outline-none w-full text-white placeholder-white/20 font-bold" />
               <kbd class="text-[10px] font-black text-white/20 bg-white/5 px-2 py-0.5 rounded ml-2">⌘K</kbd>
            </div>

            <div class="flex items-center gap-4">
              <button class="relative p-3 bg-white/5 text-white/40 rounded-full hover:text-primary transition-all border border-white/10 shadow-xl group">
                <i class="pi pi-bell"></i>
                <span class="absolute top-3 right-3 w-2 h-2 bg-red-500 rounded-full border-2 border-[#121212] animate-pulse"></span>
              </button>

              <div class="h-10 w-[1px] bg-white/10 mx-2"></div>

              <ng-container *ngIf="auth.currentUser() as user">
                <div class="flex items-center gap-4 cursor-pointer group">
                  <div class="text-right hidden sm:block">
                    <div class="text-xs font-black text-white uppercase italic">{{user.firstName}} {{user.lastName}}</div>
                    <div class="text-[9px] font-bold text-primary tracking-widest uppercase">System Admin</div>
                  </div>
                  <div class="w-12 h-12 rounded-full border-2 border-primary/30 p-0.5 group-hover:border-primary transition-all shadow-xl shadow-primary/10">
                     <img src="https://ui-avatars.com/api/?name={{user.firstName}}+{{user.lastName}}&background=3b82f6&color=fff" class="w-full h-full rounded-full object-cover" />
                  </div>
                </div>
              </ng-container>
            </div>
          </div>
        </header>

        <!-- Dynamic Viewport -->
        <main class="flex-1 overflow-y-auto p-12 custom-scrollbar relative z-10 scroll-smooth">
          <div class="max-w-7xl mx-auto space-y-10 animate-fade-in">
            <router-outlet></router-outlet>
          </div>
        </main>

        <!-- Decorative UI elements -->
        <div class="fixed top-[-10%] right-[-10%] w-[800px] h-[800px] bg-primary/2 rounded-full blur-[150px] pointer-events-none"></div>
        <div class="fixed bottom-[-10%] left-[-10%] w-[600px] h-[600px] bg-accent/2 rounded-full blur-[120px] pointer-events-none"></div>
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
