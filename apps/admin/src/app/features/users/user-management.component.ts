import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';

@Component({
  selector: 'app-user-management',
  standalone: true,
  imports: [
    CommonModule,
    MatTableModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatChipsModule,
    MatTooltipModule,
    MatFormFieldModule,
    MatInputModule
  ],
  template: `
    <div class="space-y-10 animate-fade-in pb-24 px-2">
      <div class="flex flex-col lg:flex-row lg:items-center justify-between gap-8 border-b pb-10">
        <div class="flex items-center gap-6">
          <div class="w-16 h-16 rounded-2xl flex items-center justify-center border shadow-2xl shadow-blue-500/10">
            <mat-icon class="scale-[1.5]">admin_panel_settings</mat-icon>
          </div>
          <div>
            <h1 class="text-4xl font-black tracking-tighter italic uppercase leading-tight">Authority Protocol</h1>
            <div class="flex items-center gap-3 mt-1.5">
              <span class="w-2 h-2 rounded-full animate-pulse"></span>
              <p class="font-black text-[10px] uppercase tracking-[0.4em]">Advanced user administration, role assignment, and security gatekeeper</p>
            </div>
          </div>
        </div>
        <button mat-flat-button color="primary" class="rounded-2xl h-14 px-10 font-black uppercase tracking-tighter italic shadow-2xl shadow-primary-500/20 premium-border hover: transition-all shrink-0">
           Authorize New Node
        </button>
      </div>

      <div class="flex flex-col md:flex-row gap-6 items-center animate-slide-up /[0.01] p-6 rounded-3xl border glass">
          <mat-form-field appearance="outline" class="flex-1 w-full premium-field" subscriptSizing="dynamic">
            <mat-label>Identity Transmission Search</mat-label>
            <mat-icon matPrefix class="mr-3 opacity-40">search</mat-icon>
            <input matInput placeholder="QUERY IDENTITY, ROLE, OR SECTOR...">
          </mat-form-field>

          <div class="flex gap-3 w-full md:w-auto">
             <button mat-stroked-button class="h-14 px-8 premium-stroked-button">
                <mat-icon class="scale-75 mr-2">security</mat-icon> Role Matrix
             </button>
             <button mat-stroked-button class="h-14 px-8 premium-stroked-button">
                <mat-icon class="scale-75 mr-2">lock_reset</mat-icon> Gate Reset
             </button>
          </div>
      </div>

      <mat-card class="/[0.01] border rounded-[40px] glass overflow-hidden animate-slide-up shadow-2xl">
        <div class="overflow-x-auto p-4">
          <table mat-table [dataSource]="users()" class="w-full">
             <ng-container matColumnDef="identity">
                <th mat-header-cell *matHeaderCellDef class="text-[9px] font-black uppercase tracking-[0.3em] py-8 px-10">System Identity</th>
                <td mat-cell *matCellDef="let row" class="py-10 px-10 border-b /[0.03]">
                  <div class="flex items-center gap-5">
                    <div class="w-14 h-14 rounded-2xl flex items-center justify-center border group-hover: transition-all font-black text-lg shadow-inner overflow-hidden uppercase italic">
                      {{row.firstName.charAt(0)}}{{row.lastName.charAt(0)}}
                    </div>
                    <div class="flex flex-col">
                      <span class="text-lg font-black tracking-tight uppercase italic group-hover: transition-colors">{{row.firstName}} {{row.lastName}}</span>
                      <span class="text-[8px] font-bold uppercase tracking-[0.2em] mt-1 italic">{{row.email}}</span>
                    </div>
                  </div>
                </td>
             </ng-container>

             <ng-container matColumnDef="roles">
                <th mat-header-cell *matHeaderCellDef class="text-[9px] font-black uppercase tracking-[0.3em] py-8">Privilege Nodes</th>
                <td mat-cell *matCellDef="let row" class="py-10 border-b /[0.03]">
                  <mat-chip-set>
                    @for (role of row.roles; track role) {
                      <mat-chip class="premium-chip-neutral">
                        {{role}}
                      </mat-chip>
                    }
                  </mat-chip-set>
                </td>
             </ng-container>

             <ng-container matColumnDef="status">
                <th mat-header-cell *matHeaderCellDef class="text-[9px] font-black uppercase tracking-[0.3em] py-8">Gate Access</th>
                <td mat-cell *matCellDef="let row" class="py-10 border-b /[0.03]">
                  <mat-chip-set>
                    <mat-chip [class]="row.status === 'ACTIVE' ? 'premium-chip-success' : 'premium-chip-danger'">
                      {{row.status === 'ACTIVE' ? 'AUTHORIZED' : 'TERMINATED'}}
                    </mat-chip>
                  </mat-chip-set>
                </td>
             </ng-container>

             <ng-container matColumnDef="actions">
                <th mat-header-cell *matHeaderCellDef class="text-[9px] font-black uppercase tracking-[0.3em] py-8 px-10 text-right">Orchestration</th>
                <td mat-cell *matCellDef="let row" class="py-10 px-10 border-b /[0.03] text-right">
                   <div class="flex justify-end gap-3 opacity-20 group-hover:opacity-100 transition-opacity">
                      <button mat-icon-button matTooltip="Modify Credentials" class="w-10 h-10 hover: hover: rounded-xl transition-all border">
                        <mat-icon class="scale-75">manage_accounts</mat-icon>
                      </button>
                      <button mat-icon-button matTooltip="Audit Logs" class="w-10 h-10 hover: hover: rounded-xl transition-all border">
                        <mat-icon class="scale-75">assignment_ind</mat-icon>
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

    ::ng-deep .premium-field .mat-mdc-text-field-wrapper {
      background-color: rgba(255, 255, 255, 0.03) !important;
      border-radius: 16px !important;
      padding: 8px 16px !important;
    }
    ::ng-deep .premium-field .mat-mdc-form-field-label {
      color: rgba(255, 255, 255, 0.2) !important;
      font-size: 10px !important;
      text-transform: uppercase !important;
      letter-spacing: 0.1em !important;
      font-weight: 900 !important;
    }

    .premium-stroked-button {
      background: rgba(255, 255, 255, 0.05) !important;
      border-color: rgba(255, 255, 255, 0.05) !important;
      border-radius: 16px !important;
      font-size: 10px !important;
      font-weight: 900 !important;
      text-transform: uppercase !important;
      letter-spacing: 0.1em !important;
      color: rgba(255, 255, 255, 0.4) !important;
    }

    ::ng-deep .premium-chip-success {
      --mdc-chip-elevated-container-color: rgba(34, 197, 94, 0.1) !important;
      --mdc-chip-label-text-color: #22c55e !important;
      font-size: 9px !important;
      font-weight: 900 !important;
      border: 1px solid rgba(34, 197, 94, 0.2) !important;
    }

    ::ng-deep .premium-chip-danger {
      --mdc-chip-elevated-container-color: rgba(239, 68, 68, 0.1) !important;
      --mdc-chip-label-text-color: #ef4444 !important;
      font-size: 9px !important;
      font-weight: 900 !important;
      border: 1px solid rgba(239, 68, 68, 0.2) !important;
    }

    ::ng-deep .premium-chip-neutral {
      --mdc-chip-elevated-container-color: rgba(255, 255, 255, 0.05) !important;
      --mdc-chip-label-text-color: rgba(255, 255, 255, 0.4) !important;
      font-size: 9px !important;
      font-weight: 900 !important;
      border: 1px solid rgba(255, 255, 255, 0.05) !important;
    }
  `]
})
export class UserManagementComponent {
  users = signal([
    { id: '1', firstName: 'Admin', lastName: 'User', email: 'admin@orthosync.com', roles: ['SUPER_ADMIN', 'ADMIN'], status: 'ACTIVE' },
    { id: '2', firstName: 'Sarah', lastName: 'Johnson', email: 'sarah.j@orthosync.com', roles: ['DOCTOR'], status: 'ACTIVE' },
    { id: '3', firstName: 'John', lastName: 'Doe', email: 'john.doe@gmail.com', roles: ['PATIENT'], status: 'ACTIVE' },
    { id: '4', firstName: 'Mike', lastName: 'Reception', email: 'mike.r@hospital.com', roles: ['RECEPTIONIST'], status: 'INACTIVE' },
  ]);

  columns = ['identity', 'roles', 'status', 'actions'];
}
