import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MatTooltipModule } from '@angular/material/tooltip';

@Component({
  selector: 'app-report-management',
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
          <div class="w-16 h-16 rounded-2xl flex items-center justify-center border shadow-2xl shadow-cyan-500/10">
            <mat-icon class="scale-[1.5]">biomedical_viz</mat-icon>
          </div>
          <div>
            <h1 class="text-4xl font-black tracking-tighter italic uppercase leading-tight">Diagnostic Reports</h1>
            <div class="flex items-center gap-3 mt-1.5">
              <span class="w-2 h-2 rounded-full animate-pulse"></span>
              <p class="font-black text-[10px] uppercase tracking-[0.4em]">Manage and verify system-wide laboratory and radiology diagnostics</p>
            </div>
          </div>
        </div>
        <button mat-flat-button color="primary" class="rounded-2xl h-14 px-10 font-black uppercase tracking-tighter italic shadow-2xl shadow-primary-500/20 premium-border hover: transition-all shrink-0">
           Upload Diagnostic Intel
        </button>
      </div>

      <mat-card class="/[0.01] border rounded-[40px] glass overflow-hidden animate-slide-up shadow-2xl">
        <div class="overflow-x-auto p-4">
          <table mat-table [dataSource]="reports()" class="w-full">
             <ng-container matColumnDef="timestamp">
                <th mat-header-cell *matHeaderCellDef class="text-[9px] font-black uppercase tracking-[0.3em] py-8 px-10">Timestamp</th>
                <td mat-cell *matCellDef="let row" class="py-10 px-10 border-b /[0.03]">
                  <span class="text-[10px] font-black uppercase tracking-widest italic group-hover: transition-colors">
                    {{row.date}}
                  </span>
                </td>
             </ng-container>

             <ng-container matColumnDef="patient">
                <th mat-header-cell *matHeaderCellDef class="text-[9px] font-black uppercase tracking-[0.3em] py-8">Subject Node</th>
                <td mat-cell *matCellDef="let row" class="py-10 border-b /[0.03]">
                  <div class="flex items-center gap-4">
                    <div class="w-10 h-10 rounded-xl flex items-center justify-center border text-[10px] font-black">
                      {{row.patient.charAt(0)}}
                    </div>
                    <span class="text-sm font-black uppercase italic tracking-tighter">{{row.patient}}</span>
                  </div>
                </td>
             </ng-container>

             <ng-container matColumnDef="test">
                <th mat-header-cell *matHeaderCellDef class="text-[9px] font-black uppercase tracking-[0.3em] py-8">Diagnostic Vector</th>
                <td mat-cell *matCellDef="let row" class="py-10 border-b /[0.03]">
                   <span class="text-[10px] font-black px-4 py-2 rounded-xl border uppercase tracking-[0.2em] backdrop-blur-sm">
                    {{row.test}}
                  </span>
                </td>
             </ng-container>

             <ng-container matColumnDef="status">
                <th mat-header-cell *matHeaderCellDef class="text-[9px] font-black uppercase tracking-[0.3em] py-8">Sync Status</th>
                <td mat-cell *matCellDef="let row" class="py-10 border-b /[0.03]">
                   <span [class]="row.status === 'FINAL' ? ' ' : ' '" 
                         class="px-4 py-2 rounded-xl text-[8px] font-black uppercase tracking-[0.2em] border backdrop-blur-sm">
                    {{row.status === 'FINAL' ? 'RELEASED' : 'PROCESSING'}}
                  </span>
                </td>
             </ng-container>

             <ng-container matColumnDef="actions">
                <th mat-header-cell *matHeaderCellDef class="text-[9px] font-black uppercase tracking-[0.3em] py-8 px-10 text-right">Orchestration</th>
                <td mat-cell *matCellDef="let row" class="py-10 px-10 border-b /[0.03] text-right">
                   <div class="flex justify-end gap-3 opacity-20 group-hover:opacity-100 transition-opacity">
                      <button mat-icon-button matTooltip="View Diagnostic" class="w-10 h-10 hover: hover: rounded-xl transition-all border">
                        <mat-icon class="scale-75">visibility</mat-icon>
                      </button>
                      <button mat-icon-button matTooltip="Verify Data" class="w-10 h-10 hover: hover: rounded-xl transition-all border">
                        <mat-icon class="scale-75">verified</mat-icon>
                      </button>
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
export class ReportManagementComponent {
  reports = signal([
    { test: 'CBC', patient: 'John Doe', status: 'FINAL', date: '2024-10-15' },
    { test: 'MRI', patient: 'Jane Smith', status: 'PENDING', date: '2024-10-20' },
    { test: 'BLOOD_CHEM', patient: 'Robert Wilson', status: 'FINAL', date: '2024-10-22' },
    { test: 'X_RAY', patient: 'Sarah Parker', status: 'FINAL', date: '2024-10-23' }
  ]);
  
  columns = ['timestamp', 'patient', 'test', 'status', 'actions'];
}
