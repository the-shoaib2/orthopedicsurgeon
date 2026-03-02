import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ZrdStatComponent, ZrdCardComponent, ZrdBadgeComponent, ZrdAvatarComponent, ZrdButtonComponent } from '@repo/ui';
import { AuthService } from '@repo/auth';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule, ZrdStatComponent, ZrdCardComponent, ZrdBadgeComponent, ZrdAvatarComponent, ZrdButtonComponent],
  template: `
    <div class="space-y-8 animate-in fade-in slide-in-from-bottom-4 duration-500">
      <!-- Welcome -->
      <div class="flex items-center justify-between">
        <div>
          <h1 class="text-2xl font-black text-secondary-900">Welcome back, {{ auth.currentUser()?.firstName }}!</h1>
          <p class="text-secondary-500">You have 2 appointments scheduled for this week.</p>
        </div>
        <button zrdButton variant="primary" routerLink="/doctors">
          <i class="pi pi-plus mr-2"></i> Book Appointment
        </button>
      </div>

      <!-- Quick Stats -->
      <div class="grid grid-cols-1 md:grid-cols-4 gap-6">
        <zrd-stat label="Upcoming" value="2" description="Next: Tomorrow, 10 AM" [icon]="true">
          <span icon class="pi pi-calendar text-primary-600"></span>
        </zrd-stat>
        <zrd-stat label="Prescriptions" value="5" description="2 active medications" [icon]="true">
          <span icon class="pi pi-file-medical text-green-600"></span>
        </zrd-stat>
        <zrd-stat label="Lab Reports" value="12" description="Last checkup: 2 weeks ago" [icon]="true">
          <span icon class="pi pi-chart-bar text-secondary-600"></span>
        </zrd-stat>
        <zrd-stat label="Wallet Bal." value="$0.00" description="No pending payments" [icon]="true">
          <span icon class="pi pi-wallet text-amber-600"></span>
        </zrd-stat>
      </div>

      <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">
        <!-- Next Appointment -->
        <zrd-card class="lg:col-span-1">
          <h3 slot="header" class="text-sm font-bold text-secondary-900 uppercase tracking-widest">Next Up</h3>
          <div class="space-y-6">
            <div class="flex items-center gap-4">
              <zrd-avatar name="Sarah Johnson" size="lg"></zrd-avatar>
              <div>
                <p class="font-bold text-secondary-900 text-lg">Dr. Sarah Johnson</p>
                <p class="text-xs text-primary-600 font-bold">Orthopedic Surgeon</p>
              </div>
            </div>
            
            <div class="space-y-3 pt-4 border-t border-secondary-100">
               <div class="flex items-center gap-3 text-sm">
                  <i class="pi pi-calendar text-secondary-400"></i>
                  <span class="text-secondary-700">Tomorrow, October 24</span>
               </div>
               <div class="flex items-center gap-3 text-sm">
                  <i class="pi pi-clock text-secondary-400"></i>
                  <span class="text-secondary-700">10:00 AM - 10:30 AM</span>
               </div>
               <div class="flex items-center gap-3 text-sm">
                  <i class="pi pi-map-marker text-secondary-400"></i>
                  <span class="text-secondary-700">City Orthopedic Center</span>
               </div>
            </div>

            <button zrdButton variant="outline" class="w-full mt-4">Reschedule</button>
          </div>
        </zrd-card>

        <!-- Recent Activity Table -->
        <zrd-card class="lg:col-span-2 overflow-hidden">
           <h3 slot="header" class="text-sm font-bold text-secondary-900 uppercase tracking-widest">Recent Activity</h3>
           <div class="space-y-4">
              <div *ngFor="let activity of activities" class="flex items-center justify-between p-4 bg-white border border-secondary-100 rounded-xl hover:bg-secondary-50/50 transition-colors">
                 <div class="flex items-center gap-4">
                    <div [class]="activity.iconBg" class="w-10 h-10 rounded-lg flex items-center justify-center text-white">
                       <i [class]="activity.icon"></i>
                    </div>
                    <div>
                       <p class="text-sm font-bold text-secondary-900">{{ activity.title }}</p>
                       <p class="text-[10px] text-secondary-400 font-medium uppercase">{{ activity.date }}</p>
                    </div>
                 </div>
                 <zrd-badge [variant]="activity.statusVariant">{{ activity.status }}</zrd-badge>
              </div>
           </div>
        </zrd-card>
      </div>
    </div>
  `
})
export class DashboardComponent {
  auth = inject(AuthService);

  activities: any[] = [
    { title: 'New Prescription Added', date: 'Today, 11:30 AM', icon: 'pi pi-file-medical', iconBg: 'bg-green-500', status: 'Added', statusVariant: 'success' },
    { title: 'Lab Report: Blood Test', date: 'Yesterday, 04:15 PM', icon: 'pi pi-chart-bar', iconBg: 'bg-primary-500', status: 'Pending', statusVariant: 'warning' },
    { title: 'Payment Confirmed', date: 'Oct 21, 2024', icon: 'pi pi-wallet', iconBg: 'bg-amber-500', status: 'Paid', statusVariant: 'success' },
    { title: 'Appointment Booked', date: 'Oct 20, 2024', icon: 'pi pi-calendar', iconBg: 'bg-secondary-500', status: 'Confirmed', statusVariant: 'info' }
  ];
}
