import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatDividerModule } from '@angular/material/divider';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';

@Component({
  selector: 'app-report-management',
  standalone: true,
  imports: [
    CommonModule, MatButtonModule, MatIconModule,
    MatMenuModule, MatDividerModule, MatFormFieldModule, MatInputModule
  ],
  template: `
    <div class="space-y-6">
      <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 class="text-2xl font-semibold text-slate-900 m-0">Medical Reports</h1>
          <p class="text-sm text-slate-500 mt-1 m-0">Access and manage all patient medical reports and test results.</p>
        </div>
        <button mat-flat-button color="primary">
          <mat-icon class="text-[18px]">upload</mat-icon>
          Upload Report
        </button>
      </div>

      <div class="bg-white rounded-xl border border-slate-200 overflow-hidden">
        <div class="flex flex-col sm:flex-row gap-3 px-6 py-4 border-b border-slate-100">
          <mat-form-field appearance="outline" class="flex-1 sm:max-w-sm" subscriptSizing="dynamic">
            <mat-icon matPrefix class="text-slate-400 text-[18px] mr-2">search</mat-icon>
            <input matInput placeholder="Search reports…" />
          </mat-form-field>
        </div>

        <div class="divide-y divide-slate-100">
          @for (r of reports(); track r.id) {
            <div class="flex items-center gap-4 px-6 py-4 hover:bg-slate-50 transition-colors">
              <div class="w-10 h-10 rounded-lg flex items-center justify-center shrink-0"
                   [class]="r.type === 'X-Ray' ? 'bg-blue-50' : r.type === 'MRI' ? 'bg-purple-50' : 'bg-teal-50'">
                <mat-icon class="text-[20px]"
                          [class]="r.type === 'X-Ray' ? 'text-blue-600' : r.type === 'MRI' ? 'text-purple-600' : 'text-teal-600'">
                  radiology
                </mat-icon>
              </div>
              <div class="flex-1 min-w-0">
                <p class="font-medium text-sm text-slate-900 m-0">{{ r.patient }}</p>
                <p class="text-xs text-slate-400 m-0">{{ r.type }} · Dr. {{ r.doctor }} · {{ r.date }}</p>
              </div>
              <span class="text-xs font-semibold px-2.5 py-1 rounded-full shrink-0"
                    [class]="r.type === 'X-Ray' ? 'bg-blue-50 text-blue-700 border border-blue-200'
                             : r.type === 'MRI' ? 'bg-purple-50 text-purple-700 border border-purple-200'
                             : 'bg-teal-50 text-teal-700 border border-teal-200'">
                {{ r.type }}
              </span>
              <button mat-icon-button [matMenuTriggerFor]="menu"
                      class="text-slate-400 hover:text-slate-700 hover:bg-slate-100 rounded-lg shrink-0">
                <mat-icon>more_vert</mat-icon>
              </button>
              <mat-menu #menu="matMenu">
                <button mat-menu-item><mat-icon>visibility</mat-icon> View Report</button>
                <button mat-menu-item><mat-icon>download</mat-icon> Download</button>
                <button mat-menu-item><mat-icon>share</mat-icon> Share</button>
                <mat-divider></mat-divider>
                <button mat-menu-item class="text-red-600"><mat-icon class="text-red-500">delete</mat-icon> Delete</button>
              </mat-menu>
            </div>
          }
        </div>

        <div class="px-6 py-3 border-t border-slate-100 bg-slate-50/50">
          <span class="text-xs text-slate-400">{{ reports().length }} report(s)</span>
        </div>
      </div>
    </div>
  `,
  styles: [`:host { display: block; }`]
})
export class ReportManagementComponent {
  reports = signal([
    { id: 1, patient: 'John Doe',      type: 'X-Ray', doctor: 'Sarah Johnson', date: 'Oct 15, 2024' },
    { id: 2, patient: 'Jane Smith',    type: 'MRI',   doctor: 'Mike Ross',     date: 'Oct 16, 2024' },
    { id: 3, patient: 'Robert Wilson', type: 'CT',    doctor: 'David King',    date: 'Oct 18, 2024' },
    { id: 4, patient: 'Sarah Parker',  type: 'X-Ray', doctor: 'Lisa Chen',     date: 'Oct 22, 2024' },
  ]);
}
