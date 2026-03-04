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
  selector: 'app-patient-management',
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
          <mat-icon color="primary" class="scale-150 ml-2">personal_injury</mat-icon>
          <div>
            <h1 class="text-2xl font-medium m-0">{{ 'PATIENTS.TITLE' | translate }}</h1>
            <p class="text-sm text-slate-500 m-0">{{ 'PATIENTS.SUBTITLE' | translate }}</p>
          </div>
        </div>
        <button mat-flat-button color="primary">
           {{ 'PATIENTS.REGISTER_BUTTON' | translate }}
        </button>
      </div>

      <mat-card>
        <div class="overflow-x-auto">
          <table mat-table [dataSource]="patients()" class="w-full">
             <!-- Name Column -->
             <ng-container matColumnDef="name">
                <th mat-header-cell *matHeaderCellDef>{{ 'PATIENTS.COLUMNS.NAME' | translate }}</th>
                <td mat-cell *matCellDef="let row">
                  <div class="flex items-center gap-4 py-2">
                    <div class="w-10 h-10 rounded-full bg-slate-100 flex items-center justify-center text-sm font-medium">
                      {{row.name.charAt(0)}}
                    </div>
                    <div class="flex flex-col">
                      <span class="font-medium">{{row.name}}</span>
                      <span class="text-xs text-slate-500">UID: PT-{{row.age}}{{row.name.length}}X</span>
                    </div>
                  </div>
                </td>
             </ng-container>

             <!-- Biometrics Column -->
             <ng-container matColumnDef="biometrics">
                <th mat-header-cell *matHeaderCellDef>{{ 'PATIENTS.COLUMNS.BIOMETRICS' | translate }}</th>
                <td mat-cell *matCellDef="let row">
                  <div class="flex items-center gap-2">
                    <mat-chip-set>
                      <mat-chip>{{row.gender}}</mat-chip>
                      <mat-chip color="primary">{{row.age}}Y</mat-chip>
                    </mat-chip-set>
                  </div>
                </td>
             </ng-container>

             <!-- Last Visit Column -->
             <ng-container matColumnDef="lastVisit">
                <th mat-header-cell *matHeaderCellDef>{{ 'PATIENTS.COLUMNS.LAST_VISIT' | translate }}</th>
                <td mat-cell *matCellDef="let row">
                  <span class="text-sm">{{row.lastVisit}}</span>
                </td>
             </ng-container>

             <!-- Status Column -->
             <ng-container matColumnDef="status">
                <th mat-header-cell *matHeaderCellDef>{{ 'PATIENTS.COLUMNS.STATUS' | translate }}</th>
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
                <th mat-header-cell *matHeaderCellDef class="text-right">{{ 'PATIENTS.COLUMNS.ACTIONS' | translate }}</th>
                <td mat-cell *matCellDef="let row" class="text-right">
                   <div class="flex justify-end gap-1">
                      <button mat-icon-button [matTooltip]="'Records'" color="primary">
                        <mat-icon>history_edu</mat-icon>
                      </button>
                      <button mat-icon-button [matTooltip]="'Edit'" color="accent">
                        <mat-icon>edit</mat-icon>
                      </button>
                   </div>
                </td>
             </ng-container>

             <tr mat-header-row *matHeaderRowDef="columns" ></tr>
             <tr mat-row *matRowDef="let row; columns: columns;" class="hover:bg-slate-50 cursor-pointer"></tr>
          </table>
          
          @if (patients().length === 0) {
            <div class="py-12 text-center text-slate-500">
               <mat-icon class="scale-150 mb-4 text-slate-400">person_off</mat-icon>
               <p class="font-medium text-sm">No patients found</p>
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
export class PatientManagementComponent {
  patients = signal([
    { name: 'John Doe', age: 45, gender: 'MALE', lastVisit: '2024-10-15', status: 'ACTIVE' },
    { name: 'Jane Smith', age: 32, gender: 'FEMALE', lastVisit: '2024-10-20', status: 'ACTIVE' },
    { name: 'Robert Wilson', age: 58, gender: 'MALE', lastVisit: '2024-10-18', status: 'ACTIVE' },
    { name: 'Sarah Parker', age: 29, gender: 'FEMALE', lastVisit: '2024-10-22', status: 'ACTIVE' }
  ]);
  
  columns = ['name', 'biometrics', 'lastVisit', 'status', 'actions'];
}
