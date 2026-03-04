import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatDividerModule } from '@angular/material/divider';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { AuthService } from '@repo/auth';
import { LogoComponent } from '@core/components/logo/logo.component';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [
    CommonModule, 
    RouterModule, 
    MatToolbarModule, 
    MatButtonModule, 
    MatIconModule, 
    MatMenuModule,
    MatDividerModule,
    MatFormFieldModule,
    MatInputModule,
    LogoComponent
  ],
  template: `
    <!-- Emergency Top Bar -->
    <div class="h-10 bg-secondary-900 border-b border-white/5 text-white fixed top-0 left-0 right-0 z-[1001] flex items-center">
      <div class="max-w-7xl mx-auto w-full flex items-center justify-between px-2 sm:px-4">
        <!-- Top Bar -->
        <div class="flex items-center gap-4">
          <div class="flex items-center gap-2">
            <mat-icon class="text-primary text-sm scale-75">error</mat-icon>
            <span class="text-[9px] font-black uppercase tracking-[0.2em]">Emergency:</span>
            <span class="text-[9px] font-black uppercase tracking-[0.2em] text-primary underline underline-offset-4">+1 (800) 911-ORTHO</span>
          </div>
          <div class="hidden md:flex items-center gap-2 border-l border-white/10 pl-6">
            <mat-icon class="text-white/40 text-sm scale-75">location_on</mat-icon>
            <span class="text-[9px] font-bold uppercase tracking-widest text-white/40">Clinical Hub, Ortho City</span>
          </div>
        </div>
        <div class="flex items-center gap-4">
          <button class="text-[9px] font-black uppercase tracking-widest text-white/40 hover:text-primary transition-colors">EN</button>
          <div class="w-[1px] h-3 bg-white/10"></div>
          <button class="text-[9px] font-black uppercase tracking-widest text-white/40 hover:text-primary transition-colors">BN</button>
        </div>
      </div>
    </div>

    <mat-toolbar class="h-24 bg-white/80 backdrop-blur-xl border-b border-gray-100 transition-all fixed top-10 left-0 right-0 z-[1000]">
      <div class="max-w-7xl mx-auto w-full flex items-center px-2 sm:px-4">
        <app-logo [height]="44" routerLink="/"></app-logo>

        <span class="flex-1"></span>

        <!-- Desktop Links -->
        <div class="hidden md:flex items-center gap-2">
          <a mat-button routerLink="/" routerLinkActive="active" [routerLinkActiveOptions]="{exact: true}">Home</a>
          <a mat-button routerLink="/departments" routerLinkActive="active">Centres</a>
          <a mat-button routerLink="/doctors" routerLinkActive="active">Specialists</a>
          <a mat-button routerLink="/hospitals" routerLinkActive="active">Facilities</a>
          <a mat-button routerLink="/about" routerLinkActive="active">About</a>
          <a mat-button routerLink="/contact" routerLinkActive="active">Contact</a>
          <a mat-button (click)="navigateToPortal()">Portal</a>
        </div>

      <div class="flex items-center gap-2 ml-4">
        <ng-container *ngIf="auth.currentUser() as user; else guest">
          <button mat-icon-button [matMenuTriggerFor]="userMenu">
            <mat-icon>account_circle</mat-icon>
          </button>

          <mat-menu #userMenu="matMenu">
            <button mat-menu-item (click)="navigateToPortal()">
              <mat-icon>dashboard</mat-icon>
              <span>My Dashboard</span>
            </button>
            <mat-divider></mat-divider>
            <button mat-menu-item (click)="auth.logout()">
              <mat-icon>logout</mat-icon>
              <span>Logout</span>
            </button>
          </mat-menu>
        </ng-container>

        <ng-template #guest>
          <div class="hidden md:flex items-center gap-2">
            <a mat-button matButton="tonal" routerLink="/auth/login">Sign In</a>
            <a mat-flat-button routerLink="/auth/register">Get Started</a>
          </div>
        </ng-template>

        <!-- Mobile Menu Trigger -->
        <button mat-icon-button class="flex md:!hidden" [matMenuTriggerFor]="mobileMenu">
          <mat-icon>menu</mat-icon>
        </button>

        <mat-menu #mobileMenu="matMenu">
          <div class="p-4">
            <app-logo [height]="44" routerLink="/"></app-logo>
          </div>
          <mat-divider></mat-divider>
          
          <ng-container *ngIf="!auth.currentUser()">
            <a mat-menu-item routerLink="/auth/login">Sign In</a>
            <a mat-menu-item routerLink="/auth/register">Get Started</a>
          </ng-container>

          <mat-divider></mat-divider>
          <a mat-menu-item routerLink="/" routerLinkActive="active" [routerLinkActiveOptions]="{exact: true}">Home</a>
          <a mat-menu-item routerLink="/departments" routerLinkActive="active">Centres</a>
          <a mat-menu-item routerLink="/doctors" routerLinkActive="active">Specialists</a>
          <a mat-menu-item routerLink="/hospitals" routerLinkActive="active">Facilities</a>
          <a mat-menu-item routerLink="/about" routerLinkActive="active">About</a>
          <a mat-menu-item routerLink="/contact" routerLinkActive="active">Contact</a>
          <mat-divider></mat-divider>
          <button mat-menu-item (click)="navigateToPortal()">Portal</button>
        </mat-menu>
        </div>
      </div>
    </mat-toolbar>
  `,
  styles: [`
    :host { display: block; }
    .active { background: rgba(255, 255, 255, 0.1); }
  `]
})
export class NavbarComponent {
  auth = inject(AuthService);

  navigateToPortal() {
    window.location.href = 'http://localhost:4200';
  }
}
