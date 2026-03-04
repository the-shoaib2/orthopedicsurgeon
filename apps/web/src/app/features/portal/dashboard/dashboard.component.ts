import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ZrdStatComponent, ZrdCardComponent, ZrdBadgeComponent, ZrdAvatarComponent, ZrdButtonComponent } from '@repo/ui';
import { AuthService } from '@repo/auth';
import { PublicApiService } from '../../../core/services/public-api.service';

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
          <p class="text-secondary-500">Manage your health and appointments at OrthoSync.</p>
        </div>
        <button zrdButton variant="primary" routerLink="/doctors">
          <i class="pi pi-plus mr-2"></i> Book Appointment
        </button>
      </div>

      <!-- Quick Stats -->
      <div class="grid grid-cols-1 md:grid-cols-4 gap-6">
        <zrd-stat label="Upcoming" [value]="stats().upcomingAppointments?.length || 0" description="Scheduled visits" [icon]="true">
          <span icon class="pi pi-calendar text-primary-600"></span>
        </zrd-stat>
        <zrd-stat label="Prescriptions" [value]="stats().activePrescriptions || 0" description="Active medications" [icon]="true">
          <span icon class="pi pi-file-medical text-green-600"></span>
        </zrd-stat>
        <zrd-stat label="Lab Reports" [value]="stats().recentLabReports?.length || 0" description="Recent results" [icon]="true">
          <span icon class="pi pi-chart-bar text-secondary-600"></span>
        </zrd-stat>
        <zrd-stat label="Pending Bills" [value]="stats().pendingPayments || 0" description="To be settled" [icon]="true">
          <span icon class="pi pi-wallet text-amber-600"></span>
        </zrd-stat>
      </div>

      <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">
        <!-- Next Appointment -->
        <zrd-card class="lg:col-span-1" *ngIf="stats().nextAppointment">
          <h3 slot="header" class="text-sm font-bold text-secondary-900 uppercase tracking-widest">Next Up</h3>
          <div class="space-y-6">
            <div class="flex items-center gap-4">
              <zrd-avatar [name]="stats().nextAppointment.doctorName" size="lg"></zrd-avatar>
              <div>
                <p class="font-bold text-secondary-900 text-lg">{{ stats().nextAppointment.doctorName }}</p>
                <p class="text-xs text-primary-600 font-bold">Orthopedic Surgeon</p>
              </div>
            </div>
            
            <div class="space-y-3 pt-4 border-t border-secondary-100">
               <div class="flex items-center gap-3 text-sm">
                  <i class="pi pi-calendar text-secondary-400"></i>
                  <span class="text-secondary-700">{{ stats().nextAppointment.appointmentDate | date }}</span>
               </div>
               <div class="flex items-center gap-3 text-sm">
                  <i class="pi pi-clock text-secondary-400"></i>
                  <span class="text-secondary-700">{{ stats().nextAppointment.startTime }}</span>
               </div>
               <div class="flex items-center gap-3 text-sm">
                  <i class="pi pi-map-marker text-secondary-400"></i>
                  <span class="text-secondary-700">Medical Center</span>
               </div>
            </div>

            <button zrdButton variant="outline" class="w-full mt-4">Details</button>
          </div>
        </zrd-card>

        <!-- No Appointment Fallback -->
        <zrd-card class="lg:col-span-1" *ngIf="!stats().nextAppointment">
           <h3 slot="header" class="text-sm font-bold text-secondary-900 uppercase tracking-widest">Next Up</h3>
           <div class="flex flex-col items-center justify-center py-8 text-center">
              <div class="w-16 h-16 bg-secondary-50 rounded-full flex items-center justify-center mb-4">
                 <i class="pi pi-calendar-plus text-secondary-300 text-2xl"></i>
              </div>
              <p class="text-sm font-bold text-secondary-500">No upcoming appointments</p>
              <button zrdButton variant="ghost" size="sm" routerLink="/doctors">Book one now</button>
           </div>
        </zrd-card>

        <!-- Recent Activity Table -->
        <zrd-card class="lg:col-span-2 overflow-hidden">
           <h3 slot="header" class="text-sm font-bold text-secondary-900 uppercase tracking-widest">Recent Activity</h3>
           <div class="space-y-4">
              <div *ngIf="activities().length === 0" class="flex flex-col items-center justify-center py-12 text-secondary-400">
                 <i class="pi pi-inbox text-4xl mb-4 opacity-20"></i>
                 <p class="text-sm font-medium">No recent activity found</p>
              </div>
              <div *ngFor="let activity of activities()" class="flex items-center justify-between p-4 bg-white border border-secondary-100 rounded-xl hover:bg-secondary-50/50 transition-colors">
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
export class DashboardComponent implements OnInit {
  auth = inject(AuthService);
  private apiService = inject(PublicApiService);
  
  stats = signal<any>({});
  activities = signal<any[]>([]);
  loading = signal(false);

  ngOnInit() {
    this.loadDashboard();
  }

  loadDashboard() {
    this.loading.set(true);
    this.apiService.getPatientDashboard().subscribe({
      next: (res) => {
        const data = res.data || res; // Handling different API response wrappers
        this.stats.set(data);
        this.activities.set(this.processActivities(data));
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
      }
    });
  }

  processActivities(data: any): any[] {
    const list: any[] = [];
    
    // Add appointments to activity
    if (data.upcomingAppointments) {
      data.upcomingAppointments.forEach((app: any) => {
        list.push({
          title: 'Upcoming Appointment',
          date: app.appointmentDate,
          icon: 'pi pi-calendar',
          iconBg: 'bg-primary-500',
          status: app.status,
          statusVariant: 'info'
        });
      });
    }

    // Add lab reports to activity
    if (data.recentLabReports) {
      data.recentLabReports.forEach((rpt: any) => {
        list.push({
          title: `Lab Report: ${rpt.reportName}`,
          date: rpt.createdAt,
          icon: 'pi pi-chart-bar',
          iconBg: 'bg-secondary-500',
          status: rpt.status,
          statusVariant: 'success'
        });
      });
    }

    return list.sort((a, b) => new Date(b.date).getTime() - new Date(a.date).getTime()).slice(0, 5);
  }
}
