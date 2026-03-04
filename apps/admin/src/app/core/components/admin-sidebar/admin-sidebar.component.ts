import { Component, inject, signal, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';
import { MatListModule } from '@angular/material/list';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { AuthService } from '@repo/auth';
import { LogoutConfirmDialogComponent } from '../logout-confirm-dialog/logout-confirm-dialog.component';

interface NavItem {
  labelKey: string;
  icon: string;
  route?: string;
  children?: NavItem[];
}

@Component({
  selector: 'app-admin-sidebar',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    TranslateModule,
    MatListModule,
    MatIconModule,
    MatButtonModule,
    MatTooltipModule,
    MatExpansionModule,
    MatDialogModule
  ],
  template: `
    <div class="flex flex-col h-full bg-slate-50 border-r border-slate-200 transition-all duration-300"
         [class.w-16]="collapsed">
      
      <!-- Logo Area -->
      <div class="h-16 flex items-center px-2.5 cursor-pointer shrink-0 border-b border-slate-100">
        <div class="flex items-center gap-4 w-full">
          <div class="w-10 h-10 flex items-center justify-center shrink-0">
            <mat-icon color="primary">grid_view</mat-icon>
          </div>
          @if (!collapsed) {
            <div class="flex flex-col truncate">
              <span class="text-xs font-semibold text-slate-500 leading-none mb-1  tracking-wider">Precision</span>
              <span class="text-base font-bold leading-none text-slate-900">Console</span>
            </div>
          }
        </div>
      </div>

      <!-- Navigation List -->
      <div class="flex-1 overflow-y-auto">
        <mat-nav-list>
          @for (item of navItems; track item.labelKey) {
            @if (item.children) {
              <!-- Expandable Category -->
              @if (!collapsed) {
                <mat-expansion-panel class="mat-elevation-z0">
                  <mat-expansion-panel-header>
                    <mat-panel-title>
                      <mat-icon class="mr-3">{{ item.icon }}</mat-icon>
                      <span>{{ item.labelKey | translate }}</span>
                    </mat-panel-title>
                  </mat-expansion-panel-header>
                  <mat-nav-list>
                    @for (child of item.children; track child.labelKey) {
                      <a mat-list-item [routerLink]="child.route" routerLinkActive="active-route">
                        <span>{{ child.labelKey | translate }}</span>
                      </a>
                    }
                  </mat-nav-list>
                </mat-expansion-panel>
              } @else {
                <!-- Collapsed Dropdown Icon -->
                <div class="flex justify-center my-2">
                  <button mat-icon-button [matTooltip]="item.labelKey | translate" matTooltipPosition="right">
                    <mat-icon>{{ item.icon }}</mat-icon>
                  </button>
                </div>
              }
            } @else {
              <!-- Single Item -->
              <a mat-list-item [routerLink]="item.route" routerLinkActive="active-route"
                 [matTooltip]="collapsed ? (item.labelKey | translate) : ''"
                 matTooltipPosition="right">
                 <mat-icon matListItemIcon [class.mr-0]="collapsed">{{ item.icon }}</mat-icon>
                 @if (!collapsed) {
                   <span matListItemTitle>{{ item.labelKey | translate }}</span>
                 }
              </a>
            }
          }
        </mat-nav-list>
      </div>

      <!-- Footer / Logout -->
      <div class="p-4 border-t border-slate-100 shrink-0">
        <button mat-button class="w-full text-left" color="warn" (click)="confirmLogout()">
          <mat-icon>logout</mat-icon>
          @if (!collapsed) {
            <span>{{ 'COMMON.LOGOUT' | translate }}</span>
          }
        </button>
      </div>
    </div>
  `,
  styles: [`
    :host { display: block; height: 100%; width: 100%; overflow: hidden; }
    
    .active-route {
      background: rgba(63, 81, 181, 0.12); /* Default Material Primary Light for active state */
    }
  `]
})
export class AdminSidebarComponent {
  @Input() collapsed = false;
  
  auth = inject(AuthService);
  dialog = inject(MatDialog);

  navItems: NavItem[] = [
    { labelKey: 'COMMON.NAV.OVERVIEW', icon: 'monitoring', route: '/dashboard' },
    { 
      labelKey: 'COMMON.NAV.ORGANIZATION', 
      icon: 'corporate_fare', 
      children: [
        { labelKey: 'COMMON.NAV.HOSPITALS', icon: 'domain', route: '/hospitals' },
        { labelKey: 'COMMON.NAV.DEPARTMENTS', icon: 'view_list', route: '/departments' },
      ]
    },
    { labelKey: 'COMMON.NAV.MEDICAL_STAFF', icon: 'medical_services', route: '/doctors' },
    { labelKey: 'COMMON.NAV.PATIENTS', icon: 'contact_page', route: '/patients' },
    { labelKey: 'COMMON.NAV.APPOINTMENTS', icon: 'event_available', route: '/appointments' },
    { labelKey: 'COMMON.NAV.RECORDS', icon: 'description', route: '/records' },
    { labelKey: 'COMMON.NAV.FINANCE', icon: 'payments', route: '/finance' },
    { 
      labelKey: 'COMMON.NAV.CONTENT.GROUP', 
      icon: 'dynamic_feed', 
      children: [
        { labelKey: 'COMMON.NAV.CONTENT.HERO', icon: 'view_carousel', route: '/content/hero' },
        { labelKey: 'COMMON.NAV.CONTENT.FAQ', icon: 'quiz', route: '/content/faq' },
        { labelKey: 'COMMON.NAV.CONTENT.PARTNERS', icon: 'handshake', route: '/content/partners' },
        { labelKey: 'COMMON.NAV.CONTENT.BLOG', icon: 'podcasts', route: '/blog' },
      ]
    },
    { labelKey: 'COMMON.NAV.SETTINGS', icon: 'settings', route: '/settings' },
  ];

  confirmLogout() {
    const dialogRef = this.dialog.open(LogoutConfirmDialogComponent, {
      width: '400px',
      autoFocus: false,
      panelClass: 'logout-dialog'
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.auth.logout();
      }
    });
  }
}
