import { Component, inject, signal, ViewChild, TemplateRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ZrdStatComponent, ZrdCardComponent, ZrdTableComponent, ZrdBadgeComponent, ZrdButtonComponent, ZrdPageHeaderComponent } from '@repo/ui';
import { AuthService } from '@repo/auth';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, ZrdStatComponent, ZrdCardComponent, ZrdTableComponent, ZrdBadgeComponent, ZrdButtonComponent, ZrdPageHeaderComponent],
  template: `
    <div class="space-y-8 animate-in fade-in slide-in-from-bottom-4 duration-500">
      <zrd-page-header title="Command Center" [subtitle]="'Logged in as ' + auth.currentUser()?.roles?.[0]"></zrd-page-header>

      <!-- Admin Stats -->
      <div class="grid grid-cols-1 md:grid-cols-4 gap-6">
        <zrd-stat label="Total Revenue" value="$45,280" description="+12% from last month" [icon]="true">
          <span icon class="pi pi-dollar text-green-600"></span>
        </zrd-stat>
        <zrd-stat label="Active Doctors" value="124" description="Across 8 hospitals" [icon]="true">
          <span icon class="pi pi-users text-primary-600"></span>
        </zrd-stat>
        <zrd-stat label="New Patients" value="1,450" description="+45 this week" [icon]="true">
          <span icon class="pi pi-user-plus text-secondary-600"></span>
        </zrd-stat>
        <zrd-stat label="System Alerts" value="3" description="All services operational" [icon]="true">
          <span icon class="pi pi-shield text-amber-600"></span>
        </zrd-stat>
      </div>

      <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">
        <!-- Live Appointments Monitor -->
        <zrd-card class="lg:col-span-2">
           <div header class="flex items-center justify-between w-full">
              <span class="text-sm font-bold text-secondary-900 uppercase tracking-widest">Live Appointment Feed</span>
              <button zrdButton variant="ghost" size="sm">View All</button>
           </div>
           
           <zrd-table [columns]="columns" [data]="liveAppointments">
              <ng-template #statusTemplate let-row>
                 <zrd-badge [variant]="row.status === 'CONFIRMED' ? 'success' : 'info'">{{ row.status }}</zrd-badge>
              </ng-template>
           </zrd-table>
        </zrd-card>

        <!-- Top Performing Hospitals -->
        <zrd-card>
           <h3 slot="header" class="text-sm font-bold text-secondary-900 uppercase tracking-widest">Hospital Performance</h3>
           <div class="space-y-6">
              <div *ngFor="let h of topHospitals" class="flex items-center justify-between">
                 <div class="flex items-center gap-3">
                    <div class="w-8 h-8 rounded bg-secondary-100 flex items-center justify-center text-secondary-600">
                       <i class="pi pi-building"></i>
                    </div>
                    <div>
                       <p class="text-sm font-bold text-secondary-900">{{ h.name }}</p>
                       <p class="text-[10px] text-secondary-400">{{ h.city }}</p>
                    </div>
                 </div>
                 <div class="text-right">
                    <p class="text-xs font-black text-secondary-900">{{ h.revenue }}</p>
                    <p class="text-[10px] text-green-600 font-bold">{{ h.growth }}</p>
                 </div>
              </div>
              <button zrdButton variant="outline" class="w-full mt-4">Full Report</button>
           </div>
        </zrd-card>
      </div>
    </div>
  `
})
export class AdminDashboardComponent {
  auth = inject(AuthService);

  liveAppointments = [
    { patient: 'John Doe', doctor: 'Dr. Sarah Johnson', time: '10:30 AM', status: 'CONFIRMED' },
    { patient: 'Jane Smith', doctor: 'Dr. Mike Ross', time: '11:00 AM', status: 'WAITING' },
    { patient: 'Robert Brown', doctor: 'Dr. David King', time: '11:15 AM', status: 'CONFIRMED' },
    { patient: 'Emily Davis', doctor: 'Dr. Sarah Johnson', time: '11:45 AM', status: 'WAITING' },
  ];

  columns: any[] = [
    { key: 'patient', header: 'Patient' },
    { key: 'doctor', header: 'Specialist' },
    { key: 'time', header: 'Time' },
    { key: 'status', header: 'Status', cellTemplate: null }
  ];

  @ViewChild('statusTemplate') set statusTemplate(v: TemplateRef<any>) { 
    this.columns[3].cellTemplate = v; 
  }

  topHospitals = [
    { name: 'City Orthopedic', city: 'Dhaka', revenue: '$12,450', growth: '+15%' },
    { name: 'Bone Health Center', city: 'Chittagong', revenue: '$8,200', growth: '+8%' },
    { name: 'Metro General', city: 'Sylhet', revenue: '$5,900', growth: '+12%' },
    { name: 'Nightingale Clinic', city: 'Rajshahi', revenue: '$3,100', growth: '+5%' }
  ];
}
