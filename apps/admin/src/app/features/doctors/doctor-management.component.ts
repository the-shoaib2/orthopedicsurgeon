import { Component, signal, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TranslateModule } from '@ngx-translate/core';
import { MatTableModule } from '@angular/material/table';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { AdminApiService } from '@core/services/admin-api.service';
import { DoctorSummary } from '@repo/types';

@Component({
  selector: 'app-doctor-management',
  standalone: true,
  imports: [
    CommonModule, 
    TranslateModule,
    MatTableModule, 
    MatCardModule, 
    MatFormFieldModule, 
    MatInputModule, 
    MatButtonModule, 
    MatIconModule, 
    MatChipsModule,
    MatProgressBarModule
  ],
  template: `
    <div class="space-y-6">
      <div class="flex flex-col md:flex-row md:items-center justify-between gap-4 pb-4">
        <div class="flex items-center gap-4">
          <mat-icon color="primary" class="scale-150 ml-2">medical_services</mat-icon>
          <div>
            <h1 class="text-2xl font-medium m-0">{{ 'DOCTORS.TITLE' | translate }}</h1>
            <p class="text-sm text-slate-500 m-0">{{ 'DOCTORS.SUBTITLE' | translate }}</p>
          </div>
        </div>
        <div class="flex items-center gap-4">
           <!-- Form field styling uses native material -->
          <mat-form-field appearance="outline" class="w-full md:w-64" subscriptSizing="dynamic">
            <mat-icon matPrefix>search</mat-icon>
            <input matInput (keyup)="applyFilter($event)" [placeholder]="'DOCTORS.FILTER_PLACEHOLDER' | translate">
          </mat-form-field>
          <button mat-flat-button color="primary">
             {{ 'DOCTORS.ENLIST_BUTTON' | translate }}
          </button>
        </div>
      </div>

      <mat-card>
        @if (loading()) {
          <mat-progress-bar mode="query" color="primary"></mat-progress-bar>
        }
        
        <div class="overflow-x-auto">
          <table mat-table [dataSource]="doctors()" class="w-full">
             <!-- Name Column -->
             <ng-container matColumnDef="name">
                <th mat-header-cell *matHeaderCellDef>{{ 'DOCTORS.COLUMNS.NAME' | translate }}</th>
                <td mat-cell *matCellDef="let row">
                  <div class="flex items-center gap-4 py-2">
                    <div class="w-10 h-10 rounded-full bg-slate-100 flex items-center justify-center text-sm font-medium">
                      {{row.firstName[0]}}
                    </div>
                    <div class="flex flex-col">
                      <span class="font-medium">{{row.firstName}} {{row.lastName}}</span>
                      <span class="text-xs text-slate-500">ID: {{row.id.split('-')[0]}}</span>
                    </div>
                  </div>
                </td>
             </ng-container>

             <!-- Specialization Column -->
             <ng-container matColumnDef="specialization">
                <th mat-header-cell *matHeaderCellDef>{{ 'DOCTORS.COLUMNS.SPECIALIZATION' | translate }}</th>
                <td mat-cell *matCellDef="let row">
                  <mat-chip-set>
                    <mat-chip>{{row.specialization}}</mat-chip>
                  </mat-chip-set>
                </td>
             </ng-container>

             <!-- Hospital Column -->
             <ng-container matColumnDef="hospital">
                <th mat-header-cell *matHeaderCellDef>{{ 'DOCTORS.COLUMNS.HOSPITAL' | translate }}</th>
                <td mat-cell *matCellDef="let row">
                  <div class="flex items-center gap-2 text-slate-600">
                    <mat-icon class="text-[18px] w-[18px] h-[18px]">corporate_fare</mat-icon>
                    <span class="text-sm">{{row.hospitalName || 'N/A'}}</span>
                  </div>
                </td>
             </ng-container>

             <!-- Status Column -->
             <ng-container matColumnDef="status">
                <th mat-header-cell *matHeaderCellDef>{{ 'DOCTORS.COLUMNS.STATUS' | translate }}</th>
                <td mat-cell *matCellDef="let row">
                  <mat-chip-set>
                    <mat-chip [color]="row.status === 'ACTIVE' ? 'primary' : 'accent'">
                      {{row.status}}
                    </mat-chip>
                  </mat-chip-set>
                </td>
             </ng-container>

             <!-- Actions Column -->
             <ng-container matColumnDef="actions">
                <th mat-header-cell *matHeaderCellDef class="text-right">{{ 'DOCTORS.COLUMNS.ACTIONS' | translate }}</th>
                <td mat-cell *matCellDef="let row" class="text-right">
                   <div class="flex justify-end gap-1">
                      <button mat-icon-button color="primary">
                        <mat-icon>edit</mat-icon>
                      </button>
                      <button mat-icon-button color="warn">
                        <mat-icon>delete</mat-icon>
                      </button>
                   </div>
                </td>
             </ng-container>

             <tr mat-header-row *matHeaderRowDef="displayedColumns" ></tr>
             <tr mat-row *matRowDef="let row; columns: displayedColumns;" class="hover:bg-slate-50 cursor-pointer"></tr>
          </table>

          @if (doctors().length === 0 && !loading()) {
            <div class="py-12 text-center text-slate-500">
               <mat-icon class="scale-150 mb-4 text-slate-400">person_off</mat-icon>
               <p class="font-medium text-sm">{{ 'DOCTORS.NO_DATA' | translate }}</p>
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
export class DoctorManagementComponent implements OnInit {
  private api = inject(AdminApiService);
  
  doctors = signal<DoctorSummary[]>([]);
  loading = signal(false);
  
  displayedColumns = ['name', 'specialization', 'hospital', 'status', 'actions'];

  ngOnInit() {
    this.loadDoctors();
  }

  loadDoctors() {
    this.loading.set(true);
    this.api.getDoctors().subscribe({
      next: (res) => {
        this.doctors.set(res.data.content);
        this.loading.set(false);
      },
      error: (err) => {
        console.error('Failed to load doctors', err);
        this.loading.set(false);
      }
    });
  }

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    // Server side filtering can be implemented here
  }
}
