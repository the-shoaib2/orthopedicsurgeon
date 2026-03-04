import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MatTableModule } from '@angular/material/table';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatMenuModule } from '@angular/material/menu';
import { MatDividerModule } from '@angular/material/divider';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';

@Component({
  selector: 'app-patient-management',
  standalone: true,
  imports: [
    CommonModule, RouterModule,
    MatTableModule, MatCardModule, MatButtonModule, MatIconModule,
    MatTooltipModule, MatMenuModule, MatDividerModule, MatFormFieldModule, MatInputModule
  ],
  template: `
    <div class="space-y-6">

      <!-- Header -->
      <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 class="text-2xl font-semibold text-slate-900 m-0">Patient Registry</h1>
          <p class="text-sm text-slate-500 mt-1 m-0">View and manage all registered patients.</p>
        </div>
        <button mat-flat-button color="primary">
          <mat-icon class="text-[18px]">person_add</mat-icon>
          Register Patient
        </button>
      </div>

      <!-- Table Card -->
      <div class="bg-white rounded-xl border border-slate-200 overflow-hidden">
        <div class="flex flex-col sm:flex-row gap-3 px-6 py-4 border-b border-slate-100">
          <mat-form-field appearance="outline" class="flex-1 sm:max-w-sm" subscriptSizing="dynamic">
            <mat-icon matPrefix class="text-slate-400 text-[18px] mr-2">search</mat-icon>
            <input matInput placeholder="Search patients…" />
          </mat-form-field>
        </div>

        <div class="overflow-x-auto">
          <table mat-table [dataSource]="patients()" class="w-full">

            <ng-container matColumnDef="name">
              <th mat-header-cell *matHeaderCellDef class="text-xs font-semibold text-slate-500 uppercase tracking-wide py-3 pl-6">Patient</th>
              <td mat-cell *matCellDef="let row" class="py-3 pl-6">
                <div class="flex items-center gap-3">
                  <div class="w-9 h-9 rounded-full bg-amber-100 flex items-center justify-center text-sm font-semibold text-amber-700 shrink-0">
                    {{ row.name.charAt(0) }}
                  </div>
                  <div>
                    <p class="font-medium text-sm text-slate-900 m-0">{{ row.name }}</p>
                    <p class="text-xs text-slate-400 m-0">PT-{{ row.age }}{{ row.name.length }}X</p>
                  </div>
                </div>
              </td>
            </ng-container>

            <ng-container matColumnDef="biometrics">
              <th mat-header-cell *matHeaderCellDef class="text-xs font-semibold text-slate-500 uppercase tracking-wide py-3">Demographics</th>
              <td mat-cell *matCellDef="let row" class="py-3">
                <div class="flex items-center gap-2">
                  <span class="text-xs font-medium px-2 py-0.5 rounded-full bg-slate-100 text-slate-600">{{ row.gender }}</span>
                  <span class="text-xs font-medium px-2 py-0.5 rounded-full bg-blue-50 text-blue-600">{{ row.age }} yrs</span>
                </div>
              </td>
            </ng-container>

            <ng-container matColumnDef="lastVisit">
              <th mat-header-cell *matHeaderCellDef class="text-xs font-semibold text-slate-500 uppercase tracking-wide py-3">Last Visit</th>
              <td mat-cell *matCellDef="let row">
                <span class="text-sm text-slate-600">{{ row.lastVisit }}</span>
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
                  <button mat-menu-item [routerLink]="['/patients', row.id || 'dummy-id', 'health']">
                    <mat-icon>history_edu</mat-icon> Health Records
                  </button>
                  <button mat-menu-item><mat-icon>event</mat-icon> Appointments</button>
                  <button mat-menu-item><mat-icon>edit</mat-icon> Edit Details</button>
                  <mat-divider></mat-divider>
                  <button mat-menu-item class="text-red-600"><mat-icon class="text-red-500">block</mat-icon> Deactivate</button>
                </mat-menu>
              </td>
            </ng-container>

            <tr mat-header-row *matHeaderRowDef="columns" class="bg-slate-50/50"></tr>
            <tr mat-row *matRowDef="let row; columns: columns;"
                class="border-t border-slate-50 hover:bg-slate-50/80 transition-colors cursor-pointer"></tr>
          </table>

          @if (patients().length === 0) {
            <div class="py-16 text-center">
              <mat-icon class="text-slate-300 text-[48px] w-12 h-12 mb-3">person_off</mat-icon>
              <p class="font-medium text-slate-500 text-sm">No patients found</p>
            </div>
          }
        </div>

        <div class="px-6 py-3 border-t border-slate-100 bg-slate-50/50">
          <span class="text-xs text-slate-400">{{ patients().length }} patient(s)</span>
        </div>
      </div>
    </div>
  `,
  styles: [`:host { display: block; } ::ng-deep .mat-mdc-table { background: transparent !important; }`]
})
export class PatientManagementComponent {
  patients = signal([
    { id: 'p1', name: 'John Doe',       age: 45, gender: 'Male',   lastVisit: 'Oct 15, 2024', status: 'ACTIVE' },
    { id: 'p2', name: 'Jane Smith',     age: 32, gender: 'Female', lastVisit: 'Oct 20, 2024', status: 'ACTIVE' },
    { id: 'p3', name: 'Robert Wilson',  age: 58, gender: 'Male',   lastVisit: 'Oct 18, 2024', status: 'ACTIVE' },
    { id: 'p4', name: 'Sarah Parker',   age: 29, gender: 'Female', lastVisit: 'Oct 22, 2024', status: 'ACTIVE' },
    { id: 'p5', name: 'David Martinez', age: 64, gender: 'Male',   lastVisit: 'Oct 10, 2024', status: 'INACTIVE' },
  ]);

  columns = ['name', 'biometrics', 'lastVisit', 'status', 'actions'];
}
