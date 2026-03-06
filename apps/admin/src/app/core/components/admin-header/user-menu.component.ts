import {
  Component,
  inject,
  signal,
  HostListener,
  ElementRef,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { MatRippleModule } from '@angular/material/core';
import { AuthService } from '@repo/auth';
import { ZrdButtonComponent, ZrdCardComponent } from '@repo/ui';

@Component({
  selector: 'app-admin-user-menu',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatIconModule,
    MatRippleModule,
    ZrdButtonComponent,
    ZrdCardComponent
  ],
  template: `
    <!-- Avatar Trigger (Circular, no scale animation) -->
    <button class="relative outline-none focus:ring-2 focus:ring-google-blue/30 rounded-full transition-shadow" (click)="toggle()">
      <div class="w-10 h-10 rounded-full overflow-hidden border border-google-gray-200 dark:border-white/10 shadow-sm bg-google-gray-100 dark:bg-google-gray-800 flex items-center justify-center">
         @if (auth.currentUser(); as user) {
           @if (user.imageUrl) {
             <img [src]="user.imageUrl" [alt]="user.firstName" class="w-full h-full object-cover" />
           } @else {
             <span class="text-sm font-bold text-google-gray-600 dark:text-google-gray-300">{{ getInitials(user) }}</span>
           }
         } @else {
           <mat-icon class="text-google-gray-400">account_circle</mat-icon>
         }
      </div>
    </button>

    <!-- Spartan/Google Identity Panel -->
    @if (isOpen()) {
      <div class="fixed inset-0 z-[998]" (click)="close()"></div>
      <div class="absolute top-[calc(100%+12px)] right-0 w-[400px] z-[999] animate-in fade-in zoom-in-95 duration-200">
        <zrd-card variant="default" class="p-0 overflow-hidden !rounded-[28px] bg-white dark:bg-google-gray-50 ring-1 ring-black/5">
          
          <!-- Google Style Account Header -->
          <div class="p-8 pb-6 relative rounded-t-2xl bg-google-slate-50 dark:bg-google-gray-800/20 flex flex-col items-center">
             <div class="flex flex-col items-center text-center w-full">
                <!-- Large Avatar/Initials (Separated with prominent border) -->
                <div class="relative mb-4 group cursor-pointer">
                  <div class="w-24 h-24 rounded-full bg-white dark:bg-google-gray-800 shadow-md overflow-hidden flex items-center justify-center relative">
                     @if (auth.currentUser(); as user) {
                       @if (user.imageUrl) {
                         <img [src]="user.imageUrl" [alt]="user.firstName" class="w-full h-full object-cover" />
                       } @else {
                         <span class="text-4xl font-normal text-google-gray-600 dark:text-google-gray-200">{{ getInitials(user) }}</span>
                       }
                     } @else {
                        <mat-icon class="text-5xl text-google-gray-400">account_circle</mat-icon>
                     }
                     
                     <div class="absolute inset-x-0 bottom-0 h-1/3 bg-black/30 flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity">
                        <mat-icon class="text-white !text-lg">camera_alt</mat-icon>
                     </div>
                  </div>
                </div>
                
                @if (auth.currentUser(); as user) {
                  <h2 class="text-xl font-medium text-google-gray-900 dark:text-white m-0">
                    Hi, {{ user.firstName }}!
                  </h2>
                  <div class="text-sm text-google-gray-500 dark:text-google-gray-400 mt-1">
                    {{ user.email }}
                  </div>
                  
                  <a routerLink="/settings" (click)="close()" class="mt-4 px-6 py-2 rounded-full border border-google-gray-200 dark:border-white/10 hover:bg-google-gray-100 dark:hover:bg-white/5 transition-colors text-sm font-medium text-google-gray-700 dark:text-google-gray-300">
                    Manage your profile
                  </a>
                } @else {
                  <h3 class="text-xl font-medium text-google-gray-900 dark:text-white m-0">Welcome</h3>
                  <p class="text-sm text-google-gray-500 dark:text-google-gray-400 mt-2 italic">Please sign in to access your dashboard</p>
                }
             </div>
          </div>

          <!-- Quick Navigation & Services -->
          <div class="p-2 border-t border-google-gray-100 dark:border-white/10">
             <div class="grid grid-cols-1 gap-1">
               <a routerLink="/settings" (click)="close()" class="flex items-center gap-4 p-4 px-6 rounded-full hover:bg-google-gray-50 dark:hover:bg-white/5 transition-colors group">
                  <div class="w-6 h-6 flex items-center justify-center text-google-gray-500 group-hover:text-google-blue">
                     <mat-icon>person_outline</mat-icon>
                  </div>
                  <div class="flex flex-col flex-1">
                     <span class="text-sm font-medium text-google-gray-700 dark:text-google-gray-200">Personal info</span>
                  </div>
               </a>

               <a routerLink="/settings/security" (click)="close()" class="flex items-center gap-4 p-4 px-6 rounded-full hover:bg-google-gray-50 dark:hover:bg-white/5 transition-colors group">
                  <div class="w-6 h-6 flex items-center justify-center text-google-gray-500 group-hover:text-google-emerald">
                     <mat-icon>security</mat-icon>
                  </div>
                  <div class="flex flex-col flex-1">
                     <span class="text-sm font-medium text-google-gray-700 dark:text-google-gray-200">Security settings</span>
                  </div>
               </a>
             </div>
          </div>

          <!-- Bottom Actions -->
          <div class="p-4 flex flex-col items-center gap-3 border-t border-google-gray-100 dark:border-white/10 text-center">
             @if (auth.currentUser()) {
               <div class="w-full flex flex-col items-center gap-2">
                 <zrd-button variant="outline" (click)="onLogout()" class="w-full py-3.5 cursor-pointer border-google-gray-200 dark:border-white/10 hover:border-google-red/20 hover:bg-google-red/5 hover:text-google-red text-google-gray-600 dark:text-google-gray-300 transition-all font-medium">
                    <mat-icon class="text-[20px]">logout</mat-icon>
                    <span>Sign out of all accounts</span>
                 </zrd-button>
               </div>
             } @else {
               <zrd-button variant="primary" class="w-full  py-3.5" routerLink="/auth/login" (click)="close()">
                  Sign in
               </zrd-button>
             }
             
             <div class="flex items-center justify-center gap-2 mt-2">
                <a href="#" class="text-[11px] text-google-gray-500 hover:text-google-blue">Privacy Policy</a>
                <span class="w-1 h-1 rounded-full bg-google-gray-300"></span>
                <a href="#" class="text-[11px] text-google-gray-500 hover:text-google-blue">Terms of Service</a>
             </div>
          </div>
        </zrd-card>
      </div>
    }
  `,
  styles: [`
    :host { position: relative; display: inline-block; }
  `]
})
export class AdminUserMenuComponent {
  auth = inject(AuthService);
  private elRef = inject(ElementRef);

  isOpen = signal(false);
  toggle() { this.isOpen.update(v => !v); }
  close()  { this.isOpen.set(false); }

  getInitials(user: any): string {
    const first = user.firstName?.charAt(0) || '';
    const last = user.lastName?.charAt(0) || '';
    return (first + last).toUpperCase() || '?';
  }

  avatarUrl(user: any): string {
    return user.imageUrl || '';
  }

  getRoleLabel(roles: string[]): string {
    if (!roles || roles.length === 0) return 'OPERATOR';
    const map: Record<string, string> = {
      SUPER_ADMIN: 'SYSTEM_ARCHITECT',
      ADMIN: 'GOVERNANCE_ADMIN',
      STAFF: 'CLINICAL_STAFF',
      DOCTOR: 'MEDICAL_PRACTITIONER',
      PATIENT: 'CARE_RECIPIENT',
    };
    return map[roles[0]] ?? roles[0];
  }

  onLogout() { this.close(); this.auth.logout(); }
  onHelp()   { this.close(); window.open('mailto:support@orthosync.com', '_blank'); }

  @HostListener('document:keydown.escape')
  onEscape() { this.close(); }
}
