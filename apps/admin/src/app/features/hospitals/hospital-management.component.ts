import { Component, signal, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatDividerModule } from '@angular/material/divider';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { AdminApiService } from '@core/services/admin-api.service';
import { Hospital } from '@repo/types';

@Component({
  selector: 'app-hospital-management',
  standalone: true,
  imports: [
    CommonModule, MatTableModule, MatButtonModule, MatIconModule,
    MatMenuModule, MatDividerModule, MatProgressBarModule, MatFormFieldModule, MatInputModule
  ],
  template: `
    <div class="space-y-6">
      <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 class="text-2xl font-semibold text-slate-900 m-0">Hospitals</h1>
          <p class="text-sm text-slate-500 mt-1 m-0">Manage registered hospitals and their operational status.</p>
        </div>
        <button mat-flat-button color="primary">
          <mat-icon class="text-[18px]">add</mat-icon>
          Add Hospital
        </button>
      </div>

      <div class="bg-white rounded-xl border border-slate-200 overflow-hidden">
        <div class="flex flex-col sm:flex-row gap-3 px-6 py-4 border-b border-slate-100">
          <mat-form-field appearance="outline" class="flex-1 sm:max-w-sm" subscriptSizing="dynamic">
            <mat-icon matPrefix class="text-slate-400 text-[18px] mr-2">search</mat-icon>
            <input matInput placeholder="Search hospitals…" />
          </mat-form-field>
        </div>

        @if (loading()) {
          <mat-progress-bar mode="query" color="primary"></mat-progress-bar>
        }

        <div class="overflow-x-auto">
          <table mat-table [dataSource]="hospitals()" class="w-full">

            <ng-container matColumnDef="name">
              <th mat-header-cell *matHeaderCellDef class="text-xs font-semibold text-slate-500 uppercase tracking-wide py-3 pl-6">Hospital</th>
              <td mat-cell *matCellDef="let row" class="py-3 pl-6">
                <div class="flex items-center gap-3">
                  <div class="w-9 h-9 rounded-lg bg-slate-100 flex items-center justify-center shrink-0">
                    <mat-icon class="text-slate-500 text-[18px]">corporate_fare</mat-icon>
                  </div>
                  <div>
                    <p class="font-medium text-sm text-slate-900 m-0">{{ row.name }}</p>
                    <p class="text-xs text-slate-400 m-0">License: {{ row.licenseNumber }}</p>
                  </div>
                </div>
              </td>
            </ng-container>

            <ng-container matColumnDef="city">
              <th mat-header-cell *matHeaderCellDef class="text-xs font-semibold text-slate-500 uppercase tracking-wide py-3">City</th>
              <td mat-cell *matCellDef="let row">
                <div class="flex items-center gap-1.5">
                  <mat-icon class="text-slate-400 text-[15px]">location_on</mat-icon>
                  <span class="text-sm text-slate-600">{{ row.city }}</span>
                </div>
              </td>
            </ng-container>

            <ng-container matColumnDef="phone">
              <th mat-header-cell *matHeaderCellDef class="text-xs font-semibold text-slate-500 uppercase tracking-wide py-3">Phone</th>
              <td mat-cell *matCellDef="let row">
                <span class="text-sm text-slate-600">{{ row.phone }}</span>
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
                  <button mat-menu-item><mat-icon>settings_applications</mat-icon> Manage</button>
                  <button mat-menu-item><mat-icon>analytics</mat-icon> Analytics</button>
                  <mat-divider></mat-divider>
                  <button mat-menu-item class="text-red-600"><mat-icon class="text-red-500">power_settings_new</mat-icon> Deactivate</button>
                </mat-menu>
              </td>
            </ng-container>

            <tr mat-header-row *matHeaderRowDef="displayedColumns" class="bg-slate-50/50"></tr>
            <tr mat-row *matRowDef="let row; columns: displayedColumns;"
                class="border-t border-slate-50 hover:bg-slate-50/80 transition-colors cursor-pointer"></tr>
          </table>

          @if (hospitals().length === 0 && !loading()) {
            <div class="py-16 text-center">
              <mat-icon class="text-slate-300 text-[48px] w-12 h-12 mb-3">domain_disabled</mat-icon>
              <p class="font-medium text-slate-500 text-sm">No hospitals found</p>
            </div>
          }
        </div>

        <div class="px-6 py-3 border-t border-slate-100 bg-slate-50/50">
          <span class="text-xs text-slate-400">{{ hospitals().length }} hospital(s)</span>
        </div>
      </div>
    </div>
  `,
  styles: [`:host { display: block; } ::ng-deep .mat-mdc-table { background: transparent !important; }`]
})
export class HospitalManagementComponent implements OnInit {
  private api = inject(AdminApiService);
  hospitals = signal<Hospital[]>([]);
  loading = signal(false);
  displayedColumns = ['name', 'city', 'phone', 'status', 'actions'];

  ngOnInit() { this.loadHospitals(); }

  loadHospitals() {
    this.loading.set(true);
    this.api.getHospitals().subscribe({
      next: (res) => { this.hospitals.set(res.data.content); this.loading.set(false); },
      error: () => { this.loading.set(false); }
    });
  }
}
