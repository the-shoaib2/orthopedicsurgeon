import { Component, signal, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatMenuModule } from '@angular/material/menu';
import { MatDividerModule } from '@angular/material/divider';
import { MatTooltipModule } from '@angular/material/tooltip';
import { AdminApiService } from '@core/services/admin-api.service';
import { DoctorSummary } from '@repo/types';

@Component({
  selector: 'app-doctor-management',
  standalone: true,
  imports: [
    CommonModule,
    MatTableModule, MatCardModule, MatFormFieldModule, MatInputModule,
    MatButtonModule, MatIconModule, MatChipsModule, MatProgressBarModule,
    MatMenuModule, MatDividerModule, MatTooltipModule
  ],
  template: `
    <div class="space-y-6">

      <!-- Header -->
      <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 class="text-2xl font-semibold text-slate-900 m-0">Medical Staff</h1>
          <p class="text-sm text-slate-500 mt-1 m-0">Manage doctors, specialists, and their hospital assignments.</p>
        </div>
        <button mat-flat-button color="primary">
          <mat-icon class="text-[18px]">person_add</mat-icon>
          Enlist Doctor
        </button>
      </div>

      <!-- Table Card -->
      <div class="bg-white rounded-xl border border-slate-200 overflow-hidden">
        <!-- Toolbar -->
        <div class="flex flex-col sm:flex-row gap-3 px-6 py-4 border-b border-slate-100">
          <mat-form-field appearance="outline" class="flex-1 sm:max-w-sm" subscriptSizing="dynamic">
            <mat-icon matPrefix class="text-slate-400 text-[18px] mr-2">search</mat-icon>
            <input matInput placeholder="Search by name or specialization…" (keyup)="applyFilter($event)" />
          </mat-form-field>
          <button mat-stroked-button class="text-slate-600 border-slate-300 ml-auto">
            <mat-icon class="text-[18px]">tune</mat-icon>
            Filter
          </button>
        </div>

        @if (loading()) {
          <mat-progress-bar mode="query" color="primary"></mat-progress-bar>
        }

        <div class="overflow-x-auto">
          <table mat-table [dataSource]="doctors()" class="w-full">

            <ng-container matColumnDef="name">
              <th mat-header-cell *matHeaderCellDef class="text-xs font-semibold text-slate-500 uppercase tracking-wide py-3 pl-6">Doctor</th>
              <td mat-cell *matCellDef="let row" class="py-3 pl-6">
                <div class="flex items-center gap-3">
                  <div class="w-9 h-9 rounded-full bg-teal-100 flex items-center justify-center text-sm font-semibold text-teal-700 shrink-0">
                    {{ row.firstName[0] }}
                  </div>
                  <div>
                    <p class="font-medium text-sm text-slate-900 m-0">Dr. {{ row.firstName }} {{ row.lastName }}</p>
                    <p class="text-xs text-slate-400 m-0">ID: {{ row.id.split('-')[0] }}</p>
                  </div>
                </div>
              </td>
            </ng-container>

            <ng-container matColumnDef="specialization">
              <th mat-header-cell *matHeaderCellDef class="text-xs font-semibold text-slate-500 uppercase tracking-wide py-3">Specialization</th>
              <td mat-cell *matCellDef="let row" class="py-3">
                <span class="text-xs font-semibold px-2.5 py-1 rounded-full bg-indigo-50 text-indigo-700 border border-indigo-200">
                  {{ row.specialization }}
                </span>
              </td>
            </ng-container>

            <ng-container matColumnDef="hospital">
              <th mat-header-cell *matHeaderCellDef class="text-xs font-semibold text-slate-500 uppercase tracking-wide py-3">Hospital</th>
              <td mat-cell *matCellDef="let row">
                <div class="flex items-center gap-2">
                  <mat-icon class="text-slate-400 text-[16px]">corporate_fare</mat-icon>
                  <span class="text-sm text-slate-600">{{ row.hospitalName || 'Unassigned' }}</span>
                </div>
              </td>
            </ng-container>

            <ng-container matColumnDef="status">
              <th mat-header-cell *matHeaderCellDef class="text-xs font-semibold text-slate-500 uppercase tracking-wide py-3">Status</th>
              <td mat-cell *matCellDef="let row">
                <span class="text-xs font-semibold px-2.5 py-1 rounded-full"
                      [class]="row.status === 'ACTIVE'
                        ? 'bg-green-50 text-green-700 border border-green-200'
                        : 'bg-slate-100 text-slate-500 border border-slate-200'">
                  {{ row.status === 'ACTIVE' ? 'Active' : 'Inactive' }}
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
                  <button mat-menu-item><mat-icon>edit</mat-icon> Edit Profile</button>
                  <button mat-menu-item><mat-icon>event</mat-icon> View Schedule</button>
                  <mat-divider></mat-divider>
                  <button mat-menu-item class="text-red-600"><mat-icon class="text-red-500">block</mat-icon> Deactivate</button>
                </mat-menu>
              </td>
            </ng-container>

            <tr mat-header-row *matHeaderRowDef="displayedColumns" class="bg-slate-50/50"></tr>
            <tr mat-row *matRowDef="let row; columns: displayedColumns;"
                class="border-t border-slate-50 hover:bg-slate-50/80 transition-colors cursor-pointer"></tr>
          </table>

          @if (doctors().length === 0 && !loading()) {
            <div class="py-16 text-center">
              <mat-icon class="text-slate-300 text-[48px] w-12 h-12 mb-3">person_off</mat-icon>
              <p class="font-medium text-slate-500 text-sm">No doctors found</p>
              <p class="text-xs text-slate-400">Try adjusting your search criteria.</p>
            </div>
          }
        </div>

        <div class="px-6 py-3 border-t border-slate-100 bg-slate-50/50">
          <span class="text-xs text-slate-400">{{ doctors().length }} doctor(s) found</span>
        </div>
      </div>
    </div>
  `,
  styles: [`:host { display: block; } ::ng-deep .mat-mdc-table { background: transparent !important; }`]
})
export class DoctorManagementComponent implements OnInit {
  private api = inject(AdminApiService);

  doctors = signal<DoctorSummary[]>([]);
  loading = signal(false);
  displayedColumns = ['name', 'specialization', 'hospital', 'status', 'actions'];

  ngOnInit() { this.loadDoctors(); }

  loadDoctors() {
    this.loading.set(true);
    this.api.getDoctors().subscribe({
      next: (res) => { this.doctors.set(res.data.content); this.loading.set(false); },
      error: () => { this.loading.set(false); }
    });
  }

  applyFilter(event: Event) {
    // server-side filtering can be implemented here
  }
}
