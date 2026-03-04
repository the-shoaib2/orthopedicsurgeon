import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatDividerModule } from '@angular/material/divider';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';

@Component({
  selector: 'app-prescription-management',
  standalone: true,
  imports: [
    CommonModule, MatButtonModule, MatIconModule,
    MatMenuModule, MatDividerModule, MatFormFieldModule, MatInputModule
  ],
  template: `
    <div class="space-y-6">
      <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 class="text-2xl font-semibold text-slate-900 m-0">Prescriptions</h1>
          <p class="text-sm text-slate-500 mt-1 m-0">View and manage all patient prescriptions and medication records.</p>
        </div>
        <button mat-flat-button color="primary">
          <mat-icon class="text-[18px]">add</mat-icon>
          New Prescription
        </button>
      </div>

      <!-- Table Card -->
      <div class="bg-white rounded-xl border border-slate-200 overflow-hidden">
        <div class="flex flex-col sm:flex-row gap-3 px-6 py-4 border-b border-slate-100">
          <mat-form-field appearance="outline" class="flex-1 sm:max-w-sm" subscriptSizing="dynamic">
            <mat-icon matPrefix class="text-slate-400 text-[18px] mr-2">search</mat-icon>
            <input matInput placeholder="Search prescriptions…" />
          </mat-form-field>
        </div>

        <div class="divide-y divide-slate-100">
          @for (p of prescriptions(); track p.id) {
            <div class="flex items-center gap-4 px-6 py-4 hover:bg-slate-50 transition-colors">
              <div class="w-10 h-10 rounded-lg bg-indigo-50 flex items-center justify-center shrink-0">
                <mat-icon class="text-indigo-600 text-[20px]">description</mat-icon>
              </div>
              <div class="flex-1 min-w-0">
                <p class="font-medium text-sm text-slate-900 m-0">{{ p.patient }}</p>
                <p class="text-xs text-slate-400 m-0">Dr. {{ p.doctor }} · {{ p.date }}</p>
              </div>
              <div class="hidden sm:block text-center">
                <p class="text-sm text-slate-600 m-0">{{ p.medication }}</p>
                <p class="text-xs text-slate-400 m-0">{{ p.dosage }}</p>
              </div>
              <span class="text-xs font-semibold px-2.5 py-1 rounded-full shrink-0"
                    [class]="p.status === 'Active'
                      ? 'bg-green-50 text-green-700 border border-green-200'
                      : 'bg-slate-100 text-slate-500 border border-slate-200'">
                {{ p.status }}
              </span>
              <button mat-icon-button [matMenuTriggerFor]="menu"
                      class="text-slate-400 hover:text-slate-700 hover:bg-slate-100 rounded-lg shrink-0">
                <mat-icon>more_vert</mat-icon>
              </button>
              <mat-menu #menu="matMenu">
                <button mat-menu-item><mat-icon>visibility</mat-icon> View</button>
                <button mat-menu-item><mat-icon>edit</mat-icon> Edit</button>
                <button mat-menu-item><mat-icon>print</mat-icon> Print</button>
                <mat-divider></mat-divider>
                <button mat-menu-item class="text-red-600"><mat-icon class="text-red-500">delete</mat-icon> Delete</button>
              </mat-menu>
            </div>
          }
        </div>

        <div class="px-6 py-3 border-t border-slate-100 bg-slate-50/50">
          <span class="text-xs text-slate-400">{{ prescriptions().length }} prescription(s)</span>
        </div>
      </div>
    </div>
  `,
  styles: [`:host { display: block; }`]
})
export class PrescriptionManagementComponent {
  prescriptions = signal([
    { id: 1, patient: 'John Doe',      doctor: 'Sarah Johnson', date: 'Oct 15, 2024', medication: 'Ibuprofen 400mg',    dosage: '3x daily', status: 'Active' },
    { id: 2, patient: 'Jane Smith',    doctor: 'Mike Ross',     date: 'Oct 16, 2024', medication: 'Amoxicillin 500mg', dosage: '2x daily', status: 'Active' },
    { id: 3, patient: 'Robert Wilson', doctor: 'David King',    date: 'Oct 10, 2024', medication: 'Aspirin 100mg',     dosage: '1x daily', status: 'Expired' },
    { id: 4, patient: 'Sarah Parker',  doctor: 'Lisa Chen',     date: 'Oct 20, 2024', medication: 'Metformin 500mg',   dosage: '2x daily', status: 'Active' },
  ]);
}
