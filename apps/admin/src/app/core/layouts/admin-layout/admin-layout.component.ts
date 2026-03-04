import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
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
import { AuthService } from '@repo/auth';
import { AdminSidebarComponent } from '../../components/admin-sidebar/admin-sidebar.component';

@Component({
  selector: 'app-admin-layout',
  standalone: true,
  imports: [
    CommonModule, 
    RouterModule,
    TranslateModule,
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
    AdminSidebarComponent
  ],
  template: `
    <mat-sidenav-container class="h-screen overflow-hidden">
      <!-- Sidebar -->
      <mat-sidenav #sidenav [mode]="'side'" [opened]="true" 
                   class="border-none transition-all duration-300"
                   [class.w-64]="!sidebarCollapsed()" [class.w-16]="sidebarCollapsed()">
        
        <app-admin-sidebar [collapsed]="sidebarCollapsed()"></app-admin-sidebar>
      </mat-sidenav>

      <mat-sidenav-content class="flex flex-col min-w-0 relative overflow-hidden h-full">
        <!-- Top Bar -->
        <mat-toolbar class="h-16 flex items-center justify-between px-6 shrink-0 z-20 bg-slate-50 border-b" color="default">
          <div class="flex items-center gap-2">
            <button mat-icon-button (click)="sidebarCollapsed.set(!sidebarCollapsed())">
               <mat-icon class="text-slate-600">menu</mat-icon>
            </button>
          </div>

          <div class="flex-1 px-8 hidden md:flex justify-center">
            <mat-form-field appearance="outline" class="w-full max-w-xl dense-toolbar-field" subscriptSizing="dynamic">
              <mat-icon matPrefix class="mr-3 scale-90">search</mat-icon>
              <input matInput [placeholder]="'COMMON.SEARCH' | translate" type="text" />
            </mat-form-field>
          </div>

          <div class="flex items-center gap-4">
              <!-- Language Switcher -->
              <button mat-button [matMenuTriggerFor]="langMenu">
                 <div class="flex items-center gap-2">
                    <mat-icon>language</mat-icon>
                    <span class="font-medium">{{ currentLang() === 'en' ? 'EN' : 'BN' }}</span>
                 </div>
              </button>
              <mat-menu #langMenu="matMenu">
                 <button mat-menu-item (click)="switchLang('en')">English</button>
                 <button mat-menu-item (click)="switchLang('bn')">বাংলা</button>
              </mat-menu>

            <button mat-icon-button class="hover: transition-all">
              <mat-icon [matBadge]="'4'" matBadgeColor="warn" matBadgeSize="small" class="scale-90">notifications</mat-icon>
            </button>

            <div class="h-8 w-[1px] mx-1"></div>

            @if (auth.currentUser(); as user) {
              <button mat-button [matMenuTriggerFor]="profileMenu" class="h-10 px-2">
                <div class="flex items-center gap-3 relative z-10">
                  <div class="text-right hidden sm:block">
                    <div class="text-sm font-medium leading-none truncate max-w-[100px]">{{user.firstName}} {{user.lastName}}</div>
                  </div>
                  <div class="w-8 h-8 rounded-full overflow-hidden">
                     <img [src]="'https://ui-avatars.com/?name=' + user.firstName + '+' + user.lastName + '&background=0d4b9b&color=fff&bold=true'" 
                          class="w-full h-full object-cover" />
                  </div>
                </div>
              </button>

            <mat-menu #profileMenu="matMenu">
                <div class="px-4 py-3 border-b">
                   <div class="text-sm font-medium">{{user.firstName}} {{user.lastName}}</div>
                   <div class="text-xs text-slate-500 mt-1">Administrator</div>
                </div>
                <button mat-menu-item routerLink="/settings">
                  <mat-icon>settings</mat-icon>
                  <span >{{ 'COMMON.SETTINGS' | translate }}</span>
                </button>
                <mat-divider></mat-divider>
                <button mat-menu-item (click)="auth.logout()">
                  <mat-icon class="scale-90">power_settings_new</mat-icon>
                  <span >{{ 'COMMON.LOGOUT' | translate }}</span>
                </button>
              </mat-menu>
            }
          </div>
        </mat-toolbar>

        <!-- Dynamic Viewport -->
        <main class="flex-1 overflow-y-auto p-4 sm:p-6 relative z-10 bg-slate-50">
          <div class="max-w-[1400px] mx-auto pb-12">
            <router-outlet></router-outlet>
          </div>
        </main>
      </mat-sidenav-content>
    </mat-sidenav-container>
  `
})
export class AdminLayoutComponent implements OnInit {
  auth = inject(AuthService);
  translate = inject(TranslateService);
  sidebarCollapsed = signal(false);
  currentLang = signal(localStorage.getItem('lang') || 'en');

  ngOnInit() {
    this.translate.use(this.currentLang());
  }

  switchLang(lang: string) {
    this.translate.use(lang);
    this.currentLang.set(lang);
    localStorage.setItem('lang', lang);
  }
}
