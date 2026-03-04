import { Component, inject, signal, OnInit, HostListener } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatListModule } from '@angular/material/list';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatMenuModule } from '@angular/material/menu';
import { MatDividerModule } from '@angular/material/divider';
import { MatBadgeModule } from '@angular/material/badge';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { AdminSidebarComponent } from '../../components/admin-sidebar/admin-sidebar.component';
import { AdminUserMenuComponent } from '../../components/admin-header/admin-user-menu.component';

@Component({
  selector: 'app-admin-layout',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatSidenavModule,
    MatToolbarModule,
    MatListModule,
    MatIconModule,
    MatButtonModule,
    MatMenuModule,
    MatDividerModule,
    MatBadgeModule,
    MatTooltipModule,
    MatFormFieldModule,
    MatInputModule,
    AdminSidebarComponent,
    AdminUserMenuComponent
  ],
  template: `
    <mat-sidenav-container class="h-screen overflow-hidden bg-slate-50">

      <!-- ── Sidebar ── -->
      <mat-sidenav
        #sidenav
        [mode]="isMobile() ? 'over' : 'side'"
        [opened]="isMobile() ? mobileOpen() : !collapsed()"
        class="border-none"
        [style.width]="isMobile() ? '260px' : (collapsed() ? '64px' : '260px')"
        (closedStart)="mobileOpen.set(false)">
        <app-admin-sidebar
          [collapsed]="!isMobile() && collapsed()"
          (closeRequested)="mobileOpen.set(false)">
        </app-admin-sidebar>
      </mat-sidenav>

      <!-- ── Main Area ── -->
      <mat-sidenav-content class="flex flex-col min-w-0 h-full overflow-hidden">

        <!-- Header -->
        <header class="h-16 flex items-center justify-between px-4 sm:px-6 shrink-0 z-20 bg-white border-b border-slate-200">

          <!-- Left -->
          <div class="flex items-center gap-3">
            <button mat-icon-button (click)="toggle(sidenav)"
                    class="text-slate-500 hover:bg-slate-100 rounded-lg transition-colors w-9 h-9">
              <mat-icon>menu</mat-icon>
            </button>
            @if (isMobile()) {
              <span class="text-base font-semibold text-slate-800">Precision Console</span>
            } @else {
              <div class="hidden md:flex items-center gap-1.5 text-sm text-slate-400">
                <mat-icon class="text-base leading-none">home</mat-icon>
                <span>Admin</span>
              </div>
            }
          </div>

          <!-- Center Search -->
          <div class="flex-1 px-6 hidden md:flex justify-center">
            <mat-form-field appearance="outline" class="w-full max-w-xl dense-toolbar-field" subscriptSizing="dynamic">
              <mat-icon matPrefix class="text-slate-400 text-[18px] mr-2">search</mat-icon>
              <input matInput placeholder="Search patients, doctors, appointments…" />
            </mat-form-field>
          </div>

          <!-- Right Actions -->
          <div class="flex items-center gap-1">
            <!-- Mobile search -->
            <button mat-icon-button class="text-slate-500 hover:bg-slate-100 rounded-lg md:hidden">
              <mat-icon>search</mat-icon>
            </button>

            <!-- Notifications -->
            <button mat-icon-button class="text-slate-500 hover:bg-slate-100 rounded-lg transition-colors"
                    matTooltip="Notifications" matTooltipPosition="below">
              <mat-icon [matBadge]="'4'" matBadgeColor="warn" matBadgeSize="small">notifications_none</mat-icon>
            </button>

            <!-- Help -->
            <button mat-icon-button class="text-slate-500 hover:bg-slate-100 rounded-lg transition-colors hidden sm:flex"
                    matTooltip="Help" matTooltipPosition="below">
              <mat-icon>help_outline</mat-icon>
            </button>

            <div class="w-px h-6 bg-slate-200 mx-2 hidden sm:block"></div>

            <!-- Avatar / User menu -->
            <app-admin-user-menu></app-admin-user-menu>
          </div>
        </header>

        <!-- Page Content -->
        <main class="flex-1 overflow-y-auto p-4 sm:p-6 bg-slate-50">
          <div class="max-w-[1440px] mx-auto pb-10">
            <router-outlet></router-outlet>
          </div>
        </main>

      </mat-sidenav-content>
    </mat-sidenav-container>
  `,
  styles: [`
    :host { display: block; height: 100vh; }
    mat-sidenav { border-right: none !important; box-shadow: none !important; }
  `]
})
export class AdminLayoutComponent implements OnInit {
  collapsed = signal(false);
  isMobile = signal(false);
  mobileOpen = signal(false);

  ngOnInit() { this.checkBreakpoint(); }

  @HostListener('window:resize')
  checkBreakpoint() {
    this.isMobile.set(window.innerWidth < 768);
  }

  toggle(sidenav: any) {
    if (this.isMobile()) {
      this.mobileOpen.update(v => !v);
      this.mobileOpen() ? sidenav.open() : sidenav.close();
    } else {
      this.collapsed.update(v => !v);
    }
  }
}
