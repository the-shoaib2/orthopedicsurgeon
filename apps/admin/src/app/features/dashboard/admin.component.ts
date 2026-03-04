import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TranslateModule } from '@ngx-translate/core';
import { MatCardModule } from '@angular/material/card';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MatDividerModule } from '@angular/material/divider';
import { MatListModule } from '@angular/material/list';
import { AuthService } from '@repo/auth';

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [
    CommonModule,
    TranslateModule,
    MatCardModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatChipsModule,
    MatDividerModule,
    MatListModule
  ],
  template: `
    <div class="space-y-6">
      <div class="flex flex-col md:flex-row md:items-center justify-between gap-4 pb-4">
        <div class="flex items-center gap-4">
          <mat-icon color="primary" class="scale-150 ml-2">analytics</mat-icon>
          <div>
            <h1 class="text-2xl font-medium m-0">{{ 'DASHBOARD.TITLE' | translate }}</h1>
            <p class="text-sm text-slate-500 m-0">{{ 'DASHBOARD.SUBTITLE' | translate }}</p>
          </div>
        </div>
        <div class="flex items-center gap-2">
           <button mat-stroked-button color="primary">
             <mat-icon>download</mat-icon> Export Matrix
           </button>
           <button mat-flat-button color="primary">
             Initialize Sync
           </button>
        </div>
      </div>

      <!-- Stats Grid -->
      <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        @for (stat of stats; track stat.labelKey; let i = $index) {
          <mat-card>
            <mat-card-content class="pt-4">
              <div class="flex items-center justify-between mb-2">
                <mat-icon color="primary">{{ stat.icon }}</mat-icon>
                <span class="text-xs font-medium px-2 py-0.5 rounded bg-slate-100 text-slate-600">
                  {{ stat.trend }}
                </span>
              </div>
              <p class="text-sm text-slate-500 mb-1">{{ stat.labelKey | translate }}</p>
              <h3 class="text-3xl font-regular mb-4">{{ stat.value }}</h3>
              <div class="flex items-center gap-2">
                <div class="flex-1 h-1 bg-slate-100">
                   <div class="h-full bg-primary-500" [style.width]="'70%'"></div>
                </div>
                <span class="text-xs text-slate-500">{{ stat.description }}</span>
              </div>
            </mat-card-content>
          </mat-card>
        }
      </div>

      <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <!-- Live Feed Table -->
        <mat-card class="lg:col-span-2">
           <mat-card-header class="pb-2">
              <div mat-card-avatar>
                <mat-icon>sensors</mat-icon>
              </div>
              <mat-card-title>{{ 'DASHBOARD.LIVE_FEED.TITLE' | translate }}</mat-card-title>
              <mat-card-subtitle>Secure Link Active</mat-card-subtitle>
           </mat-card-header>
           
           <div class="overflow-x-auto">
             <table mat-table [dataSource]="liveAppointments" class="w-full">
                <ng-container matColumnDef="patient">
                  <th mat-header-cell *matHeaderCellDef>{{ 'DASHBOARD.LIVE_FEED.COLUMNS.PATIENT' | translate }}</th>
                  <td mat-cell *matCellDef="let row">
                    <div class="flex items-center gap-3 py-2">
                      <div class="w-8 h-8 rounded-full bg-slate-100 flex items-center justify-center text-sm font-medium">{{row.patient[0]}}</div>
                      <span class="font-medium">{{row.patient}}</span>
                    </div>
                  </td>
                </ng-container>

                <ng-container matColumnDef="doctor">
                  <th mat-header-cell *matHeaderCellDef>{{ 'DASHBOARD.LIVE_FEED.COLUMNS.DOCTOR' | translate }}</th>
                  <td mat-cell *matCellDef="let row">
                    <span class="text-sm text-slate-600">{{row.doctor}}</span>
                  </td>
                </ng-container>

                <ng-container matColumnDef="time">
                  <th mat-header-cell *matHeaderCellDef>{{ 'DASHBOARD.LIVE_FEED.COLUMNS.TIME' | translate }}</th>
                  <td mat-cell *matCellDef="let row">
                    <span class="text-sm">{{row.time}}</span>
                  </td>
                </ng-container>

                <ng-container matColumnDef="status">
                  <th mat-header-cell *matHeaderCellDef class="text-right">{{ 'DASHBOARD.LIVE_FEED.COLUMNS.STATUS' | translate }}</th>
                  <td mat-cell *matCellDef="let row" class="text-right">
                    <mat-chip-set class="justify-end">
                      <mat-chip [color]="row.status === 'CONFIRMED' ? 'primary' : 'accent'">
                        {{row.status}}
                      </mat-chip>
                    </mat-chip-set>
                  </td>
                </ng-container>

                <tr mat-header-row *matHeaderRowDef="displayedColumns" ></tr>
                <tr mat-row *matRowDef="let row; columns: displayedColumns;" class="hover:bg-slate-50"></tr>
             </table>
           </div>
        </mat-card>

        <!-- Logistics Card -->
        <mat-card>
            <mat-card-header class="pb-2">
              <div mat-card-avatar>
                <mat-icon>apartment</mat-icon>
              </div>
              <mat-card-title>{{ 'DASHBOARD.LOGISTICS.TITLE' | translate }}</mat-card-title>
            </mat-card-header>

           <mat-nav-list>
              @for (h of topHospitals; track h.name) {
                <a mat-list-item>
                   <mat-icon matListItemIcon>corporate_fare</mat-icon>
                   <div matListItemTitle>{{ h.name }}</div>
                   <div matListItemLine class="text-slate-500">{{ h.city }}</div>
                   <div matListItemMeta class="text-right">
                      <p class="text-sm font-medium m-0">{{ h.revenue }}</p>
                      <p class="text-xs text-slate-500 m-0">{{ h.growth }}</p>
                   </div>
                </a>
              }
           </mat-nav-list>

           <mat-card-actions class="p-4 pt-0">
             <button mat-stroked-button color="primary" class="w-full">
               {{ 'DASHBOARD.LOGISTICS.REPORT_BUTTON' | translate }}
             </button>
           </mat-card-actions>
        </mat-card>
      </div>
    </div>
  `,
  styles: [`
    :host { display: block; }
  `]
})
export class AdminComponent {
  auth = inject(AuthService);

  displayedColumns: string[] = ['patient', 'doctor', 'time', 'status'];
  
  stats = [
    { labelKey: 'DASHBOARD.STATS.REVENUE', value: '$45,280', description: 'Net earnings', icon: 'payments', trend: '+12%', trendClass: '' },
    { labelKey: 'DASHBOARD.STATS.SPECIALISTS', value: '124', description: 'Certified staff', icon: 'medical_services', trend: 'Stable', trendClass: '' },
    { labelKey: 'DASHBOARD.STATS.PATIENTS', value: '1,450', description: 'New registrations', icon: 'person_add', trend: '+45', trendClass: '' },
    { labelKey: 'DASHBOARD.STATS.SECURITY', value: 'Active', description: 'System online', icon: 'shield', trend: 'LOCKED', trendClass: '' },
  ];

  liveAppointments = [
    { patient: 'John Doe', doctor: 'Dr. Sarah Johnson', time: '10:30 AM', status: 'CONFIRMED' },
    { patient: 'Jane Smith', doctor: 'Dr. Mike Ross', time: '11:00 AM', status: 'WAITING' },
    { patient: 'Robert Brown', doctor: 'Dr. David King', time: '11:15 AM', status: 'CONFIRMED' },
    { patient: 'Emily Davis', doctor: 'Dr. Sarah Johnson', time: '11:45 AM', status: 'WAITING' },
  ];

  topHospitals = [
    { name: 'City Orthopedic', city: 'Dhaka', revenue: '$12,450', growth: '+15%' },
    { name: 'Bone Health Center', city: 'Chittagong', revenue: '$8,200', growth: '+8%' },
    { name: 'Metro General', city: 'Sylhet', revenue: '$5,900', growth: '+12%' },
    { name: 'Nightingale Clinic', city: 'Rajshahi', revenue: '$3,100', growth: '+5%' }
  ];
}
