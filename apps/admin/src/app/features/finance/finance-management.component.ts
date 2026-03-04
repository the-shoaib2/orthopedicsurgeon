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
import { CurrencyPipe } from '@angular/common';

@Component({
  selector: 'app-finance-management',
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
    MatInputModule,
    CurrencyPipe
  ],
  template: `
    <div class="space-y-6">
      <div class="flex flex-col lg:flex-row lg:items-center justify-between gap-4 pb-4">
        <div class="flex items-center gap-4">
          <mat-icon color="primary" class="scale-150 ml-2">account_balance_wallet</mat-icon>
          <div>
            <h1 class="text-2xl font-medium m-0">Fiscal Flux</h1>
            <p class="text-sm text-slate-500 m-0">Oversee system revenue, hospital billings, and insurance claims</p>
          </div>
        </div>
        <div class="flex gap-4">
          <button mat-flat-button color="primary">
             Generate Fiscal Audit
          </button>
        </div>
      </div>

      <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
        <mat-card>
          <mat-card-content class="pt-4">
            <div class="flex items-center justify-between mb-4">
              <mat-icon color="primary">payments</mat-icon>
              <span class="text-xs font-medium px-2 py-0.5 rounded bg-slate-100 text-slate-600">+12.5%</span>
            </div>
            <p class="text-sm text-slate-500 mb-1">Net Operational Revenue</p>
            <h3 class="text-3xl font-regular mb-1">$1.24M</h3>
          </mat-card-content>
        </mat-card>

        <mat-card>
          <mat-card-content class="pt-4">
            <div class="flex items-center justify-between mb-4">
              <mat-icon color="warn">pending_actions</mat-icon>
              <span class="text-xs font-medium px-2 py-0.5 rounded bg-red-50 text-red-600">CRITICAL</span>
            </div>
            <p class="text-sm text-slate-500 mb-1">Pending Insurance Claims</p>
            <h3 class="text-3xl font-regular mb-1">452</h3>
          </mat-card-content>
        </mat-card>

        <mat-card>
          <mat-card-content class="pt-4">
            <div class="flex items-center justify-between mb-4">
              <mat-icon color="accent">output</mat-icon>
              <span class="text-xs font-medium px-2 py-0.5 rounded bg-blue-50 text-blue-600">STABLE</span>
            </div>
            <p class="text-sm text-slate-500 mb-1">Provider Payout Aggregate</p>
            <h3 class="text-3xl font-regular mb-1">$842k</h3>
          </mat-card-content>
        </mat-card>
      </div>

      <mat-card>
        <div class="overflow-x-auto">
          <table mat-table [dataSource]="transactions()" class="w-full">
             <ng-container matColumnDef="id">
                <th mat-header-cell *matHeaderCellDef>Transaction ID</th>
                <td mat-cell *matCellDef="let row">
                  <span class="font-medium">
                    {{row.id}}
                  </span>
                </td>
             </ng-container>

             <ng-container matColumnDef="patient">
                <th mat-header-cell *matHeaderCellDef>Associated Subject</th>
                <td mat-cell *matCellDef="let row">
                  <div class="flex items-center gap-4 py-2">
                    <div class="w-8 h-8 rounded-full bg-slate-100 flex items-center justify-center text-sm font-medium">
                      {{row.patient.charAt(0)}}
                    </div>
                    <span>{{row.patient}}</span>
                  </div>
                </td>
             </ng-container>

             <ng-container matColumnDef="amount">
                <th mat-header-cell *matHeaderCellDef>Quantum</th>
                <td mat-cell *matCellDef="let row">
                  <span class="font-medium">{{row.amount | currency}}</span>
                </td>
             </ng-container>

             <ng-container matColumnDef="status">
                <th mat-header-cell *matHeaderCellDef>Verification</th>
                <td mat-cell *matCellDef="let row">
                   <mat-chip-set>
                     <mat-chip [color]="row.status === 'SUCCESS' ? 'primary' : 'accent'">
                       {{row.status === 'SUCCESS' ? 'SETTLED' : 'VERIFYING'}}
                     </mat-chip>
                   </mat-chip-set>
                </td>
             </ng-container>

             <ng-container matColumnDef="date">
                <th mat-header-cell *matHeaderCellDef class="text-right">Timestamp</th>
                <td mat-cell *matCellDef="let row" class="text-right">
                   <span class="text-sm text-slate-600">{{row.date}}</span>
                </td>
             </ng-container>

             <tr mat-header-row *matHeaderRowDef="columns"></tr>
             <tr mat-row *matRowDef="let row; columns: columns;" class="hover:bg-slate-50 cursor-pointer"></tr>
          </table>
          
          @if (transactions().length === 0) {
            <div class="py-12 text-center text-slate-500">
               <mat-icon class="scale-150 mb-4 text-slate-400">no_sim</mat-icon>
               <p class="font-medium text-sm">No ledger data in current cycle</p>
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
export class FinanceManagementComponent {
  transactions = signal([
    { id: 'TX-500', patient: 'John Doe', amount: 150.00, status: 'SUCCESS', date: '2024-10-15' },
    { id: 'TX-501', patient: 'Jane Smith', amount: 45.00, status: 'SUCCESS', date: '2024-10-16' },
    { id: 'TX-502', patient: 'Robert Wilson', amount: 2450.00, status: 'PENDING', date: '2024-10-18' },
    { id: 'TX-503', patient: 'Sarah Parker', amount: 320.00, status: 'SUCCESS', date: '2024-10-22' }
  ]);
  
  columns = ['id', 'patient', 'amount', 'status', 'date'];
}
