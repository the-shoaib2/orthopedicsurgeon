import { Component, signal, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TranslateModule } from '@ngx-translate/core';
import { MatTableModule } from '@angular/material/table';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { AdminApiService } from '@core/services/admin-api.service';
import { Hospital } from '@repo/types';

@Component({
  selector: 'app-hospital-management',
  standalone: true,
  imports: [
    CommonModule, 
    TranslateModule,
    MatTableModule, 
    MatCardModule, 
    MatButtonModule, 
    MatIconModule, 
    MatChipsModule,
    MatProgressBarModule
  ],
  template: `
    <div class="space-y-6">
      <div class="flex flex-col md:flex-row md:items-center justify-between gap-4 pb-4">
        <div class="flex items-center gap-4">
          <mat-icon color="primary" class="scale-150 ml-2">apartment</mat-icon>
          <div>
            <h1 class="text-2xl font-medium m-0">{{ 'HOSPITALS.TITLE' | translate }}</h1>
            <p class="text-sm text-slate-500 m-0">{{ 'HOSPITALS.SUBTITLE' | translate }}</p>
          </div>
        </div>
        <button mat-flat-button color="primary">
           {{ 'HOSPITALS.ADD_BUTTON' | translate }}
        </button>
      </div>

      <mat-card>
        @if (loading()) {
          <mat-progress-bar mode="query" color="primary"></mat-progress-bar>
        }
        
        <div class="overflow-x-auto">
          <table mat-table [dataSource]="hospitals()" class="w-full">
             <!-- Name Column -->
             <ng-container matColumnDef="name">
                <th mat-header-cell *matHeaderCellDef>{{ 'HOSPITALS.COLUMNS.NAME' | translate }}</th>
                <td mat-cell *matCellDef="let row">
                  <div class="flex items-center gap-4 py-2">
                    <div class="w-10 h-10 rounded bg-slate-100 flex items-center justify-center text-slate-400">
                      <mat-icon>corporate_fare</mat-icon>
                    </div>
                    <div class="flex flex-col">
                      <span class="font-medium">{{row.name}}</span>
                      <span class="text-xs text-slate-500">License: {{row.licenseNumber}}</span>
                    </div>
                  </div>
                </td>
             </ng-container>

             <!-- City Column -->
             <ng-container matColumnDef="city">
                <th mat-header-cell *matHeaderCellDef>{{ 'HOSPITALS.COLUMNS.CITY' | translate }}</th>
                <td mat-cell *matCellDef="let row">
                  <span class="text-sm">{{row.city}}</span>
                </td>
             </ng-container>

             <!-- Phone Column -->
             <ng-container matColumnDef="phone">
                <th mat-header-cell *matHeaderCellDef>{{ 'HOSPITALS.COLUMNS.PHONE' | translate }}</th>
                <td mat-cell *matCellDef="let row">
                  <span class="text-sm">{{row.phone}}</span>
                </td>
             </ng-container>

             <!-- Status Column -->
             <ng-container matColumnDef="status">
                <th mat-header-cell *matHeaderCellDef>{{ 'HOSPITALS.COLUMNS.STATUS' | translate }}</th>
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
                <th mat-header-cell *matHeaderCellDef class="text-right">{{ 'HOSPITALS.COLUMNS.ACTIONS' | translate }}</th>
                <td mat-cell *matCellDef="let row" class="text-right">
                   <div class="flex justify-end gap-1">
                      <button mat-icon-button color="primary">
                        <mat-icon>settings_applications</mat-icon>
                      </button>
                      <button mat-icon-button color="warn">
                        <mat-icon>power_settings_new</mat-icon>
                      </button>
                   </div>
                </td>
             </ng-container>

             <tr mat-header-row *matHeaderRowDef="displayedColumns" ></tr>
             <tr mat-row *matRowDef="let row; columns: displayedColumns;" class="hover:bg-slate-50 cursor-pointer"></tr>
          </table>
          
          @if (hospitals().length === 0 && !loading()) {
            <div class="py-12 text-center text-slate-500">
               <mat-icon class="scale-150 mb-4 text-slate-400">domain_disabled</mat-icon>
               <p class="font-medium text-sm">{{ 'HOSPITALS.NO_DATA' | translate }}</p>
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
export class HospitalManagementComponent implements OnInit {
  private api = inject(AdminApiService);
  
  hospitals = signal<Hospital[]>([]);
  loading = signal(false);
  
  displayedColumns = ['name', 'city', 'phone', 'status', 'actions'];

  ngOnInit() {
    this.loadHospitals();
  }

  loadHospitals() {
    this.loading.set(true);
    this.api.getHospitals().subscribe({
      next: (res) => {
        this.hospitals.set(res.data.content);
        this.loading.set(false);
      },
      error: (err) => {
        console.error('Failed to load hospitals', err);
        this.loading.set(false);
      }
    });
  }
}
