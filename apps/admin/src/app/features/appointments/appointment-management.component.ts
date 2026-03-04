import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TranslateModule } from '@ngx-translate/core';
import { MatTableModule } from '@angular/material/table';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MatTooltipModule } from '@angular/material/tooltip';

@Component({
  selector: 'app-appointment-management',
  standalone: true,
  imports: [
    CommonModule, 
    TranslateModule,
    MatTableModule, 
    MatCardModule, 
    MatButtonModule, 
    MatIconModule, 
    MatChipsModule,
    MatTooltipModule
  ],
  template: `
    <div class="space-y-6">
      <div class="flex flex-col md:flex-row md:items-center justify-between gap-4 pb-4">
        <div class="flex items-center gap-4">
          <mat-icon color="primary" class="scale-150 ml-2">event_available</mat-icon>
          <div>
            <h1 class="text-2xl font-medium m-0">{{ 'APPOINTMENTS.TITLE' | translate }}</h1>
            <p class="text-sm text-slate-500 m-0">{{ 'APPOINTMENTS.SUBTITLE' | translate }}</p>
          </div>
        </div>
        <button mat-flat-button color="primary">
           {{ 'APPOINTMENTS.NEW_SESSION' | translate }}
        </button>
      </div>

      <mat-card>
        <div class="overflow-x-auto">
          <table mat-table [dataSource]="appointments()" class="w-full">
             <!-- Patient Column -->
             <ng-container matColumnDef="patient">
                <th mat-header-cell *matHeaderCellDef>{{ 'APPOINTMENTS.COLUMNS.PATIENT' | translate }}</th>
                <td mat-cell *matCellDef="let row">
                  <div class="flex items-center gap-4 py-2">
                    <div class="w-10 h-10 rounded-full bg-slate-100 flex items-center justify-center text-sm font-medium">
                      {{row.patient.charAt(0)}}
                    </div>
                    <div class="flex flex-col">
                      <span class="font-medium">{{row.patient}}</span>
                      <span class="text-xs text-slate-500">Status: Nominal</span>
                    </div>
                  </div>
                </td>
             </ng-container>

             <!-- Doctor Column -->
             <ng-container matColumnDef="doctor">
                <th mat-header-cell *matHeaderCellDef>{{ 'APPOINTMENTS.COLUMNS.DOCTOR' | translate }}</th>
                <td mat-cell *matCellDef="let row">
                  <div class="flex items-center gap-2">
                    <mat-icon class="text-[18px] w-[18px] h-[18px] text-slate-500">medical_services</mat-icon>
                    <span class="text-sm">{{row.doctor}}</span>
                  </div>
                </td>
             </ng-container>

             <!-- Time Column -->
             <ng-container matColumnDef="timestamp">
                <th mat-header-cell *matHeaderCellDef>{{ 'APPOINTMENTS.COLUMNS.TIMESTAMP' | translate }}</th>
                <td mat-cell *matCellDef="let row">
                  <div class="flex flex-col">
                    <span class="text-sm font-medium">{{row.date}}</span>
                    <span class="text-xs text-slate-500">{{row.time}}</span>
                  </div>
                </td>
             </ng-container>

             <!-- Status Column -->
             <ng-container matColumnDef="status">
                <th mat-header-cell *matHeaderCellDef>{{ 'APPOINTMENTS.COLUMNS.STATUS' | translate }}</th>
                <td mat-cell *matCellDef="let row">
                  <mat-chip-set>
                    <mat-chip [color]="row.status === 'CONFIRMED' ? 'primary' : 'accent'">
                      {{row.status}}
                    </mat-chip>
                  </mat-chip-set>
                </td>
             </ng-container>

             <!-- Actions Column -->
             <ng-container matColumnDef="actions">
                <th mat-header-cell *matHeaderCellDef class="text-right">{{ 'APPOINTMENTS.COLUMNS.ACTIONS' | translate }}</th>
                <td mat-cell *matCellDef="let row" class="text-right">
                   <div class="flex justify-end gap-1">
                      <button mat-icon-button [matTooltip]="'Edit'" color="primary">
                        <mat-icon>edit_calendar</mat-icon>
                      </button>
                      <button mat-icon-button [matTooltip]="'Cancel'" color="warn">
                        <mat-icon>cancel</mat-icon>
                      </button>
                   </div>
                </td>
             </ng-container>

             <tr mat-header-row *matHeaderRowDef="columns" ></tr>
             <tr mat-row *matRowDef="let row; columns: columns;" class="hover:bg-slate-50 cursor-pointer"></tr>
          </table>
          
          @if (appointments().length === 0) {
            <div class="py-12 text-center text-slate-500">
               <mat-icon class="scale-150 mb-4 text-slate-400">event_busy</mat-icon>
               <p class="font-medium text-sm">No active sessions</p>
            </div>
          }
        </div>
      </mat-card>
    </div>
  `,
  styles: [`
    :host { display: block; }
  `]
})
export class AppointmentManagementComponent {
  appointments = signal([
    { patient: 'John Doe', doctor: 'Dr. Sarah', date: '2024-10-24', time: '10:00 AM', status: 'CONFIRMED' },
    { patient: 'Jane Smith', doctor: 'Dr. Mike', date: '2024-10-24', time: '11:00 AM', status: 'PENDING' },
    { patient: 'Robert Wilson', doctor: 'Dr. Sarah', date: '2024-10-25', time: '02:30 PM', status: 'CONFIRMED' },
    { patient: 'Sarah Parker', doctor: 'Dr. Mike', date: '2024-10-25', time: '09:15 AM', status: 'CONFIRMED' }
  ]);
  
  columns = ['patient', 'doctor', 'timestamp', 'status', 'actions'];
}
