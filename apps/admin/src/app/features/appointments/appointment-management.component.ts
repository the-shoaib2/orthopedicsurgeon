import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatDividerModule } from '@angular/material/divider';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';

@Component({
  selector: 'app-appointment-management',
  standalone: true,
  imports: [
    CommonModule, MatTableModule, MatButtonModule, MatIconModule,
    MatMenuModule, MatDividerModule, MatFormFieldModule, MatInputModule
  ],
  template: `
    <div class="space-y-6">
      <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 class="text-2xl font-semibold text-slate-900 m-0">Appointments</h1>
          <p class="text-sm text-slate-500 mt-1 m-0">Manage and track all patient appointment sessions.</p>
        </div>
        <button mat-flat-button color="primary">
          <mat-icon class="text-[18px]">add</mat-icon>
          New Appointment
        </button>
      </div>

      <!-- Summary Stats -->
      <div class="grid grid-cols-2 sm:grid-cols-4 gap-4">
        @for (s of summaryStats; track s.label) {
          <div class="bg-white rounded-xl border border-slate-200 px-5 py-4">
            <p class="text-xl font-bold text-slate-900 m-0">{{ s.value }}</p>
            <p class="text-xs text-slate-500 m-0 mt-0.5">{{ s.label }}</p>
          </div>
        }
      </div>

      <div class="bg-white rounded-xl border border-slate-200 overflow-hidden">
        <div class="flex flex-col sm:flex-row gap-3 px-6 py-4 border-b border-slate-100">
          <mat-form-field appearance="outline" class="flex-1 sm:max-w-sm" subscriptSizing="dynamic">
            <mat-icon matPrefix class="text-slate-400 text-[18px] mr-2">search</mat-icon>
            <input matInput placeholder="Search appointments…" />
          </mat-form-field>
        </div>

        <div class="overflow-x-auto">
          <table mat-table [dataSource]="appointments()" class="w-full">

            <ng-container matColumnDef="patient">
              <th mat-header-cell *matHeaderCellDef class="text-xs font-semibold text-slate-500 uppercase tracking-wide py-3 pl-6">Patient</th>
              <td mat-cell *matCellDef="let row" class="py-3 pl-6">
                <div class="flex items-center gap-3">
                  <div class="w-8 h-8 rounded-full bg-slate-100 flex items-center justify-center text-sm font-semibold text-slate-600 shrink-0">
                    {{ row.patient.charAt(0) }}
                  </div>
                  <span class="font-medium text-sm text-slate-800">{{ row.patient }}</span>
                </div>
              </td>
            </ng-container>

            <ng-container matColumnDef="doctor">
              <th mat-header-cell *matHeaderCellDef class="text-xs font-semibold text-slate-500 uppercase tracking-wide py-3">Doctor</th>
              <td mat-cell *matCellDef="let row">
                <div class="flex items-center gap-2">
                  <mat-icon class="text-slate-400 text-[16px]">medical_services</mat-icon>
                  <span class="text-sm text-slate-600">{{ row.doctor }}</span>
                </div>
              </td>
            </ng-container>

            <ng-container matColumnDef="timestamp">
              <th mat-header-cell *matHeaderCellDef class="text-xs font-semibold text-slate-500 uppercase tracking-wide py-3">Date &amp; Time</th>
              <td mat-cell *matCellDef="let row">
                <div>
                  <p class="text-sm font-medium text-slate-800 m-0">{{ row.date }}</p>
                  <p class="text-xs text-slate-400 m-0">{{ row.time }}</p>
                </div>
              </td>
            </ng-container>

            <ng-container matColumnDef="status">
              <th mat-header-cell *matHeaderCellDef class="text-xs font-semibold text-slate-500 uppercase tracking-wide py-3">Status</th>
              <td mat-cell *matCellDef="let row">
                <span class="text-xs font-semibold px-2.5 py-1 rounded-full"
                      [class]="getStatusStyle(row.status)">
                  {{ row.status }}
                </span>
              </td>
            </ng-container>

            <ng-container matColumnDef="actions">
              <th mat-header-cell *matHeaderCellDef class="text-xs font-semibold text-slate-500 uppercase tracking-wide py-3 text-right pr-6">Actions</th>
              <td mat-cell *matCellDef="let row" class="text-right pr-6">
                <button mat-icon-button [matMenuTriggerFor]="menu"
                        class="text-slate-400 hover:text-slate-700 hover:bg-slate-100 rounded-lg">
                  <mat-icon>more_vert</mat-icon>
                </button>
                <mat-menu #menu="matMenu">
                  <button mat-menu-item><mat-icon>edit_calendar</mat-icon> Reschedule</button>
                  <button mat-menu-item><mat-icon>check_circle</mat-icon> Confirm</button>
                  <mat-divider></mat-divider>
                  <button mat-menu-item class="text-red-600"><mat-icon class="text-red-500">cancel</mat-icon> Cancel</button>
                </mat-menu>
              </td>
            </ng-container>

            <tr mat-header-row *matHeaderRowDef="columns" class="bg-slate-50/50"></tr>
            <tr mat-row *matRowDef="let row; columns: columns;"
                class="border-t border-slate-50 hover:bg-slate-50/80 transition-colors cursor-pointer"></tr>
          </table>
        </div>

        <div class="px-6 py-3 border-t border-slate-100 bg-slate-50/50">
          <span class="text-xs text-slate-400">{{ appointments().length }} appointment(s)</span>
        </div>
      </div>
    </div>
  `,
  styles: [`:host { display: block; } ::ng-deep .mat-mdc-table { background: transparent !important; }`]
})
export class AppointmentManagementComponent {
  summaryStats = [
    { label: 'Total Today',  value: '48' },
    { label: 'Confirmed',    value: '32' },
    { label: 'Pending',      value: '12' },
    { label: 'Cancelled',    value: '4'  },
  ];

  appointments = signal([
    { patient: 'John Doe',      doctor: 'Dr. Sarah Johnson', date: 'Oct 24, 2024', time: '10:00 AM', status: 'CONFIRMED' },
    { patient: 'Jane Smith',    doctor: 'Dr. Mike Ross',     date: 'Oct 24, 2024', time: '11:00 AM', status: 'PENDING'   },
    { patient: 'Robert Wilson', doctor: 'Dr. Sarah Johnson', date: 'Oct 25, 2024', time: '02:30 PM', status: 'CONFIRMED' },
    { patient: 'Sarah Parker',  doctor: 'Dr. Mike Ross',     date: 'Oct 25, 2024', time: '09:15 AM', status: 'CONFIRMED' },
    { patient: 'Emily Davis',   doctor: 'Dr. Lisa Chen',     date: 'Oct 26, 2024', time: '03:00 PM', status: 'CANCELLED' },
  ]);

  columns = ['patient', 'doctor', 'timestamp', 'status', 'actions'];

  getStatusStyle(status: string): string {
    const m: Record<string, string> = {
      CONFIRMED: 'bg-green-50 text-green-700 border border-green-200',
      PENDING:   'bg-amber-50 text-amber-700 border border-amber-200',
      CANCELLED: 'bg-red-50 text-red-700 border border-red-200',
    };
    return m[status] ?? 'bg-slate-100 text-slate-600';
  }
}
