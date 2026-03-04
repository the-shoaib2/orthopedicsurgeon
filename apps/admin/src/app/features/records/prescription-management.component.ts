import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MatTooltipModule } from '@angular/material/tooltip';

@Component({
  selector: 'app-prescription-management',
  standalone: true,
  imports: [
    CommonModule, 
    MatTableModule, 
    MatCardModule, 
    MatButtonModule, 
    MatIconModule, 
    MatChipsModule,
    MatTooltipModule
  ],
  template: `
    <div class="space-y-10 animate-fade-in pb-24 px-2">
      <div class="flex flex-col lg:flex-row lg:items-center justify-between gap-8 border-b pb-10">
        <div class="flex items-center gap-6">
          <div class="w-16 h-16 rounded-2xl flex items-center justify-center border shadow-2xl shadow-rose-500/10">
            <mat-icon class="scale-[1.5]">medication</mat-icon>
          </div>
          <div>
            <h1 class="text-4xl font-black tracking-tighter italic uppercase leading-tight">Prescription Audit</h1>
            <div class="flex items-center gap-3 mt-1.5">
              <span class="w-2 h-2 rounded-full animate-pulse"></span>
              <p class="font-black text-[10px] uppercase tracking-[0.4em]">Review and audit digital prescriptions issued by medical authorities</p>
            </div>
          </div>
        </div>
        <button mat-flat-button color="primary" class="rounded-2xl h-14 px-10 font-black uppercase tracking-tighter italic shadow-2xl shadow-primary-500/20 premium-border hover: transition-all shrink-0">
           Initialize Audit Trace
        </button>
      </div>

      <mat-card class="/[0.01] border rounded-[40px] glass overflow-hidden animate-slide-up shadow-2xl">
        <div class="overflow-x-auto p-4">
          <table mat-table [dataSource]="prescriptions()" class="w-full">
             <ng-container matColumnDef="id">
                <th mat-header-cell *matHeaderCellDef class="text-[9px] font-black uppercase tracking-[0.3em] py-8 px-10">Protocol ID</th>
                <td mat-cell *matCellDef="let row" class="py-10 px-10 border-b /[0.03]">
                  <span class="text-sm font-black px-4 py-2 rounded-xl border uppercase tracking-[0.1em] italic">
                    {{row.id}}
                  </span>
                </td>
             </ng-container>

             <ng-container matColumnDef="doctor">
                <th mat-header-cell *matHeaderCellDef class="text-[9px] font-black uppercase tracking-[0.3em] py-8">Medical Authority</th>
                <td mat-cell *matCellDef="let row" class="py-10 border-b /[0.03]">
                  <div class="flex items-center gap-4">
                    <div class="w-10 h-10 rounded-xl flex items-center justify-center border">
                       <mat-icon class="scale-75">clinical_notes</mat-icon>
                    </div>
                    <span class="text-sm font-black uppercase italic tracking-tighter">{{row.doctor}}</span>
                  </div>
                </td>
             </ng-container>

             <ng-container matColumnDef="patient">
                <th mat-header-cell *matHeaderCellDef class="text-[9px] font-black uppercase tracking-[0.3em] py-8">Subject Node</th>
                <td mat-cell *matCellDef="let row" class="py-10 border-b /[0.03]">
                   <span class="text-sm font-black uppercase tracking-widest italic">{{row.patient}}</span>
                </td>
             </ng-container>

             <ng-container matColumnDef="date">
                <th mat-header-cell *matHeaderCellDef class="text-[9px] font-black uppercase tracking-[0.3em] py-8 px-10 text-right">Timestamp</th>
                <td mat-cell *matCellDef="let row" class="py-10 px-10 border-b /[0.03] text-right">
                   <div class="flex flex-col items-end">
                      <span class="text-xs font-black italic tracking-tighter uppercase">{{row.date}}</span>
                      <span class="text-[7px] font-black uppercase tracking-widest mt-1">Audit Logged</span>
                   </div>
                </td>
             </ng-container>

             <tr mat-header-row *matHeaderRowDef="columns" class="/[0.02]"></tr>
             <tr mat-row *matRowDef="let row; columns: columns;" class="group hover:/[0.02] transition-all cursor-pointer"></tr>
          </table>
        </div>
      </mat-card>
    </div>
  `,
  styles: [`
    :host { display: block; }
    .glass { backdrop-filter: blur(40px); }
    ::ng-deep .mat-mdc-table { background: transparent !important; }
  `]
})
export class PrescriptionManagementComponent {
  prescriptions = signal([
    { id: 'RX-100', doctor: 'Dr. Sarah', patient: 'John Doe', date: '2024-10-15' },
    { id: 'RX-101', doctor: 'Dr. Mike', patient: 'Jane Smith', date: '2024-10-20' },
    { id: 'RX-102', doctor: 'Dr. Sarah', patient: 'Robert Wilson', date: '2024-10-22' },
    { id: 'RX-103', doctor: 'Dr. Sarah', patient: 'Sarah Parker', date: '2024-10-23' }
  ]);
  
  columns = ['id', 'doctor', 'patient', 'date'];
}
