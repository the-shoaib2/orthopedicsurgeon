import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MatListModule } from '@angular/material/list';
import { MatDividerModule } from '@angular/material/divider';
import { MatTooltipModule } from '@angular/material/tooltip';
import { AuthService } from '@repo/auth';

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatCardModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatChipsModule,
    MatListModule,
    MatDividerModule,
    MatTooltipModule
  ],
  template: `
    <div class="space-y-6">

      <!-- Page Header -->
      <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 class="text-2xl font-semibold text-slate-900 m-0">Dashboard</h1>
          <p class="text-sm text-slate-500 mt-1 m-0">Welcome back. Here's what's happening today.</p>
        </div>
        <div class="flex items-center gap-2">
          <button mat-stroked-button class="text-slate-600 border-slate-300 hover:bg-slate-50">
            <mat-icon class="text-[18px]">download</mat-icon>
            Export Report
          </button>
          <button mat-flat-button color="primary">
            <mat-icon class="text-[18px]">add</mat-icon>
            Quick Add
          </button>
        </div>
      </div>

      <!-- Stats Grid -->
      <div class="grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-4 gap-4">
        @for (stat of stats; track stat.label) {
          <div class="bg-white rounded-xl border border-slate-200 p-5 hover:shadow-md transition-shadow">
            <div class="flex items-center justify-between mb-3">
              <div class="w-10 h-10 rounded-lg flex items-center justify-center"
                   [class]="stat.iconBg">
                <mat-icon class="text-[22px]" [class]="stat.iconColor">{{ stat.icon }}</mat-icon>
              </div>
              <span class="text-xs font-semibold px-2 py-1 rounded-full"
                    [class]="stat.trendClass">
                {{ stat.trend }}
              </span>
            </div>
            <p class="text-sm text-slate-500 mb-1 font-medium">{{ stat.label }}</p>
            <h3 class="text-2xl font-bold text-slate-900 m-0">{{ stat.value }}</h3>
            <p class="text-xs text-slate-400 mt-1 m-0">{{ stat.description }}</p>
          </div>
        }
      </div>

      <!-- Content Area -->
      <div class="grid grid-cols-1 xl:grid-cols-3 gap-6">

        <!-- Live Appointments Table -->
        <div class="xl:col-span-2 bg-white rounded-xl border border-slate-200 overflow-hidden">
          <div class="flex items-center justify-between px-6 py-4 border-b border-slate-100">
            <div class="flex items-center gap-2">
              <div class="w-2 h-2 rounded-full bg-green-500 animate-pulse"></div>
              <h2 class="text-base font-semibold text-slate-800 m-0">Live Appointments</h2>
            </div>
            <button mat-button color="primary" class="text-sm">View All</button>
          </div>

          <div class="overflow-x-auto">
            <table mat-table [dataSource]="liveAppointments" class="w-full">

              <ng-container matColumnDef="patient">
                <th mat-header-cell *matHeaderCellDef class="text-xs font-semibold text-slate-500 uppercase tracking-wide py-3">Patient</th>
                <td mat-cell *matCellDef="let row" class="py-3">
                  <div class="flex items-center gap-3">
                    <div class="w-8 h-8 rounded-full bg-slate-100 flex items-center justify-center text-sm font-semibold text-slate-600">
                      {{ row.patient[0] }}
                    </div>
                    <span class="font-medium text-slate-800 text-sm">{{ row.patient }}</span>
                  </div>
                </td>
              </ng-container>

              <ng-container matColumnDef="doctor">
                <th mat-header-cell *matHeaderCellDef class="text-xs font-semibold text-slate-500 uppercase tracking-wide py-3">Doctor</th>
                <td mat-cell *matCellDef="let row">
                  <span class="text-sm text-slate-600">{{ row.doctor }}</span>
                </td>
              </ng-container>

              <ng-container matColumnDef="time">
                <th mat-header-cell *matHeaderCellDef class="text-xs font-semibold text-slate-500 uppercase tracking-wide py-3">Time</th>
                <td mat-cell *matCellDef="let row">
                  <span class="text-sm text-slate-700 font-medium">{{ row.time }}</span>
                </td>
              </ng-container>

              <ng-container matColumnDef="status">
                <th mat-header-cell *matHeaderCellDef class="text-xs font-semibold text-slate-500 uppercase tracking-wide py-3 text-right pr-6">Status</th>
                <td mat-cell *matCellDef="let row" class="text-right pr-6">
                  <span class="text-xs font-semibold px-2.5 py-1 rounded-full"
                        [class]="row.status === 'CONFIRMED'
                          ? 'bg-green-50 text-green-700 border border-green-200'
                          : 'bg-amber-50 text-amber-700 border border-amber-200'">
                    {{ row.status }}
                  </span>
                </td>
              </ng-container>

              <tr mat-header-row *matHeaderRowDef="displayedColumns" class="bg-slate-50/50"></tr>
              <tr mat-row *matRowDef="let row; columns: displayedColumns;"
                  class="hover:bg-slate-50 transition-colors border-t border-slate-50 cursor-pointer"></tr>
            </table>
          </div>
        </div>

        <!-- Top Hospitals -->
        <div class="bg-white rounded-xl border border-slate-200 overflow-hidden">
          <div class="flex items-center justify-between px-6 py-4 border-b border-slate-100">
            <h2 class="text-base font-semibold text-slate-800 m-0">Top Hospitals</h2>
            <button mat-button color="primary" class="text-sm">Details</button>
          </div>

          <div class="divide-y divide-slate-100">
            @for (h of topHospitals; track h.name; let i = $index) {
              <div class="flex items-center gap-4 px-6 py-4 hover:bg-slate-50 transition-colors cursor-pointer">
                <div class="w-8 h-8 rounded-lg bg-slate-100 flex items-center justify-center text-sm font-bold text-slate-500 shrink-0">
                  {{ i + 1 }}
                </div>
                <div class="flex-1 min-w-0">
                  <p class="font-medium text-sm text-slate-800 truncate m-0">{{ h.name }}</p>
                  <p class="text-xs text-slate-400 m-0">{{ h.city }}</p>
                </div>
                <div class="text-right shrink-0">
                  <p class="text-sm font-semibold text-slate-800 m-0">{{ h.revenue }}</p>
                  <p class="text-xs text-green-600 font-medium m-0">{{ h.growth }}</p>
                </div>
              </div>
            }
          </div>

          <div class="px-6 py-4 border-t border-slate-100">
            <button mat-stroked-button class="w-full text-slate-600 border-slate-300 hover:bg-slate-50">
              View Full Report
            </button>
          </div>
        </div>
      </div>

      <!-- Quick Stats Row -->
      <div class="grid grid-cols-2 sm:grid-cols-4 gap-4">
        @for (q of quickStats; track q.label) {
          <div class="bg-white rounded-xl border border-slate-200 p-4 text-center">
            <mat-icon class="text-slate-400 mb-2">{{ q.icon }}</mat-icon>
            <p class="text-xl font-bold text-slate-900 m-0">{{ q.value }}</p>
            <p class="text-xs text-slate-500 m-0 mt-1">{{ q.label }}</p>
          </div>
        }
      </div>
    </div>
  `,
  styles: [`
    :host { display: block; }
    ::ng-deep .mat-mdc-table { background: transparent !important; }
  `]
})
export class AdminComponent {
  auth = inject(AuthService);

  displayedColumns = ['patient', 'doctor', 'time', 'status'];

  stats = [
    {
      label: 'Total Revenue', value: '$45,280', description: 'Net earnings this month',
      icon: 'payments', iconBg: 'bg-blue-50', iconColor: 'text-blue-600',
      trend: '+12%', trendClass: 'bg-green-50 text-green-700'
    },
    {
      label: 'Medical Staff', value: '124', description: 'Certified specialists',
      icon: 'medical_services', iconBg: 'bg-indigo-50', iconColor: 'text-indigo-600',
      trend: 'Stable', trendClass: 'bg-slate-100 text-slate-600'
    },
    {
      label: 'New Patients', value: '1,450', description: 'Registered this month',
      icon: 'person_add', iconBg: 'bg-emerald-50', iconColor: 'text-emerald-600',
      trend: '+45', trendClass: 'bg-green-50 text-green-700'
    },
    {
      label: 'System Status', value: 'Active', description: 'All systems operational',
      icon: 'shield', iconBg: 'bg-green-50', iconColor: 'text-green-600',
      trend: 'Secure', trendClass: 'bg-green-50 text-green-700'
    },
  ];

  liveAppointments = [
    { patient: 'John Doe',       doctor: 'Dr. Sarah Johnson', time: '10:30 AM', status: 'CONFIRMED' },
    { patient: 'Jane Smith',     doctor: 'Dr. Mike Ross',     time: '11:00 AM', status: 'WAITING' },
    { patient: 'Robert Brown',   doctor: 'Dr. David King',    time: '11:15 AM', status: 'CONFIRMED' },
    { patient: 'Emily Davis',    doctor: 'Dr. Sarah Johnson', time: '11:45 AM', status: 'WAITING' },
    { patient: 'Michael Wilson', doctor: 'Dr. Lisa Chen',     time: '12:00 PM', status: 'CONFIRMED' },
  ];

  topHospitals = [
    { name: 'City Orthopedic',    city: 'Dhaka',      revenue: '$12,450', growth: '+15%' },
    { name: 'Bone Health Center', city: 'Chittagong', revenue: '$8,200',  growth: '+8%'  },
    { name: 'Metro General',      city: 'Sylhet',     revenue: '$5,900',  growth: '+12%' },
    { name: 'Nightingale Clinic', city: 'Rajshahi',   revenue: '$3,100',  growth: '+5%'  },
  ];

  quickStats = [
    { label: 'Appointments Today',   value: '48',  icon: 'event_available' },
    { label: 'Pending Prescriptions', value: '12',  icon: 'description' },
    { label: 'Active Hospitals',      value: '8',   icon: 'corporate_fare' },
    { label: 'Open Invoices',         value: '$3.2k', icon: 'receipt_long' },
  ];
}
