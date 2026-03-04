import { Component, inject, signal, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MatListModule } from '@angular/material/list';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatDividerModule } from '@angular/material/divider';
import { AuthService } from '@repo/auth';
import { LogoutConfirmDialogComponent } from '../logout-confirm-dialog/logout-confirm-dialog.component';

interface NavItem {
  label: string;
  icon: string;
  route?: string;
  children?: NavItem[];
  badge?: string;
}

@Component({
  selector: 'app-admin-sidebar',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatListModule,
    MatIconModule,
    MatButtonModule,
    MatTooltipModule,
    MatExpansionModule,
    MatDialogModule,
    MatDividerModule
  ],
  template: `
    <div class="flex flex-col h-full bg-slate-800 transition-all duration-300 overflow-hidden"
         [class.w-16]="collapsed">

      <!-- ── Logo / Brand ── -->
      <div class="h-16 flex items-center shrink-0 border-b border-slate-700/50 px-4"
           [class.justify-center]="collapsed">
        <div class="flex items-center gap-3 overflow-hidden">
          <!-- Icon -->
          <div class="w-8 h-8 rounded-lg bg-white/10 flex items-center justify-center shrink-0">
            <mat-icon class="text-white text-[18px] leading-none">medical_services</mat-icon>
          </div>
          <!-- Brand text — hidden when collapsed -->
          @if (!collapsed) {
            <div class="flex flex-col leading-none overflow-hidden">
              <span class="text-[10px] font-semibold text-slate-400 uppercase tracking-widest">Precision</span>
              <span class="text-base font-bold text-white truncate">Console</span>
            </div>
          }
        </div>
      </div>

      <!-- ── Navigation ── -->
      <div class="flex-1 overflow-y-auto overflow-x-hidden py-2 sidebar-nav sidebar-expansion">
        <mat-nav-list [disableRipple]="false">
          @for (item of navItems; track item.label) {
            @if (item.children) {
              <!-- ─ Group with children ─ -->
              @if (collapsed) {
                <!-- Collapsed: show group icon with tooltip -->
                <div class="flex justify-center py-1">
                  <button mat-icon-button
                          [matTooltip]="item.label" matTooltipPosition="right"
                          class="text-slate-400 hover:text-white hover:bg-white/8 rounded-lg w-10 h-10">
                    <mat-icon>{{ item.icon }}</mat-icon>
                  </button>
                </div>
              } @else {
                <!-- Expanded: expansion panel -->
                <mat-expansion-panel class="mat-elevation-z0">
                  <mat-expansion-panel-header>
                    <mat-panel-title>
                      <mat-icon class="text-slate-400 text-[20px] shrink-0">{{ item.icon }}</mat-icon>
                      <span>{{ item.label }}</span>
                    </mat-panel-title>
                  </mat-expansion-panel-header>
                  <mat-nav-list class="pl-8! py-1">
                    @for (child of item.children; track child.label) {
                      <a mat-list-item [routerLink]="child.route" routerLinkActive="active-route"
                         (click)="onNavClick()" class="rounded-none">
                        <span matListItemTitle>{{ child.label }}</span>
                      </a>
                    }
                  </mat-nav-list>
                </mat-expansion-panel>
              }
            } @else {
              <!-- ─ Single nav item ─ -->
              <a mat-list-item [routerLink]="item.route" routerLinkActive="active-route"
                 [class.justify-center]="collapsed"
                 [matTooltip]="collapsed ? item.label : ''" matTooltipPosition="right"
                 (click)="onNavClick()">
                <mat-icon matListItemIcon [class.mr-0]="collapsed" class="text-[20px] shrink-0">{{ item.icon }}</mat-icon>
                @if (!collapsed) {
                  <ng-container>
                    <span matListItemTitle>{{ item.label }}</span>
                    @if (item.badge) {
                      <span matListItemMeta class="text-[10px] font-semibold bg-white/20 text-white px-1.5 py-0.5 rounded-full">
                        {{ item.badge }}
                      </span>
                    }
                  </ng-container>
                }
              </a>
            }
          }
        </mat-nav-list>
      </div>

      <!-- ── Divider ── -->
      <div class="border-t border-slate-700/50 mx-4"></div>

      <!-- ── User Role Badge ── -->
      @if (!collapsed) {
        <div class="px-4 pt-3 pb-1">
          <div class="px-3 py-2 bg-white/5 rounded-lg border border-white/10">
            <p class="text-[10px] text-slate-400 uppercase tracking-wider font-semibold mb-0.5">Logged in as</p>
            <p class="text-xs font-semibold text-white truncate">{{ getUserDisplay() }}</p>
          </div>
        </div>
      }

      <!-- ── Logout ── -->
      <div class="p-3 shrink-0" [class.flex]="collapsed" [class.justify-center]="collapsed">
        @if (collapsed) {
          <button mat-icon-button (click)="confirmLogout()"
                  matTooltip="Sign Out" matTooltipPosition="right"
                  class="text-slate-400 hover:text-red-400 hover:bg-red-500/10 rounded-lg w-10 h-10 transition-colors">
            <mat-icon>logout</mat-icon>
          </button>
        } @else {
          <button mat-button (click)="confirmLogout()"
                  class="w-full text-left rounded-lg h-10 px-3 text-slate-400 hover:text-red-400 hover:bg-red-500/10 transition-colors flex items-center gap-3">
            <mat-icon class="text-[20px]">logout</mat-icon>
            <span class="text-sm font-medium">Sign Out</span>
          </button>
        }
      </div>
    </div>
  `,
  styles: [`
    :host { display: block; height: 100%; width: 100%; overflow: hidden; }

    /* Child nav items in expansion panels */
    ::ng-deep .sidebar-nav mat-nav-list .pl-8\\! .mat-mdc-list-item {
      height: 40px !important;
      padding-left: 0 !important;
    }
    ::ng-deep .sidebar-nav mat-nav-list .pl-8\\! .mdc-list-item__primary-text {
      font-size: 13px !important;
      color: rgba(255,255,255,0.6) !important;
    }
    ::ng-deep .sidebar-nav mat-nav-list .pl-8\\! .mat-mdc-list-item.active-route .mdc-list-item__primary-text {
      color: #fff !important;
    }
  `]
})
export class AdminSidebarComponent {
  @Input() collapsed = false;
  @Output() closeRequested = new EventEmitter<void>();

  auth = inject(AuthService);
  dialog = inject(MatDialog);

  navItems: NavItem[] = [
    { label: 'Dashboard',      icon: 'monitoring',          route: '/dashboard' },
    {
      label: 'Organization', icon: 'corporate_fare',
      children: [
        { label: 'Hospitals',    icon: 'domain',    route: '/hospitals' },
        { label: 'Departments',  icon: 'view_list', route: '/departments' },
      ]
    },
    { label: 'Medical Staff',  icon: 'medical_services',    route: '/doctors' },
    { label: 'Patients',       icon: 'contact_page',         route: '/patients' },
    { label: 'Appointments',   icon: 'event_available',      route: '/appointments' },
    { label: 'Records',        icon: 'description',           route: '/records/prescriptions' },
    { label: 'Finance',        icon: 'payments',              route: '/finance' },
    {
      label: 'Content', icon: 'dynamic_feed',
      children: [
        { label: 'Hero Section',  icon: 'view_carousel', route: '/content/hero' },
        { label: 'FAQ',           icon: 'quiz',          route: '/content/faq' },
        { label: 'Partners',      icon: 'handshake',     route: '/content/partners' },
        { label: 'Blog',          icon: 'article',       route: '/blog' },
      ]
    },
    { label: 'User Management', icon: 'admin_panel_settings', route: '/users' },
    { label: 'Settings',         icon: 'settings',              route: '/settings' },
  ];

  getUserDisplay(): string {
    const user = this.auth.currentUser();
    if (!user) return 'Administrator';
    const role = user.roles?.[0];
    const roleMap: Record<string, string> = {
      SUPER_ADMIN: 'Super Administrator',
      ADMIN: 'Administrator',
      STAFF: 'Staff',
      DOCTOR: 'Doctor',
    };
    return `${user.firstName} · ${roleMap[role] ?? role}`;
  }

  onNavClick() {
    this.closeRequested.emit();
  }

  confirmLogout() {
    const dialogRef = this.dialog.open(LogoutConfirmDialogComponent, {
      width: '400px',
      autoFocus: false,
      panelClass: 'logout-dialog'
    });
    dialogRef.afterClosed().subscribe(result => {
      if (result) this.auth.logout();
    });
  }
}
