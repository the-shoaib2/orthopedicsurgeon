import { Component, signal, inject, OnInit } from '@angular/core';
import { CommonModule, CurrencyPipe } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatDividerModule } from '@angular/material/divider';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';

@Component({
  selector: 'app-finance-management',
  standalone: true,
  imports: [
    CommonModule, CurrencyPipe, MatTableModule, MatButtonModule, MatIconModule,
    MatMenuModule, MatDividerModule, MatProgressBarModule, MatFormFieldModule, MatInputModule
  ],
  template: `
    <div class="space-y-6">
      <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 class="text-2xl font-semibold text-slate-900 m-0">Finance</h1>
          <p class="text-sm text-slate-500 mt-1 m-0">Revenue overview, billing, and transaction management.</p>
        </div>
        <button mat-flat-button color="primary">
          <mat-icon class="text-[18px]">download</mat-icon>
          Export Report
        </button>
      </div>

      <!-- Summary Cards -->
      <div class="grid grid-cols-1 sm:grid-cols-3 gap-4">
        @for (s of summaryCards; track s.label) {
          <div class="bg-white rounded-xl border border-slate-200 p-5">
            <div class="flex items-center justify-between mb-3">
              <div class="w-10 h-10 rounded-lg flex items-center justify-center" [class]="s.iconBg">
                <mat-icon class="text-[22px]" [class]="s.iconColor">{{ s.icon }}</mat-icon>
              </div>
              <span class="text-xs font-semibold px-2 py-1 rounded-full" [class]="s.badgeClass">
                {{ s.badge }}
              </span>
            </div>
            <p class="text-sm text-slate-500 mb-1 font-medium m-0">{{ s.label }}</p>
            <h3 class="text-2xl font-bold text-slate-900 m-0">{{ s.value }}</h3>
          </div>
        }
      </div>

      <!-- Transactions Table -->
      <div class="bg-white rounded-xl border border-slate-200 overflow-hidden">
        <div class="flex items-center justify-between px-6 py-4 border-b border-slate-100">
          <h2 class="text-base font-semibold text-slate-800 m-0">Recent Transactions</h2>
          <mat-form-field appearance="outline" class="w-64" subscriptSizing="dynamic">
            <mat-icon matPrefix class="text-slate-400 text-[18px] mr-2">search</mat-icon>
            <input matInput placeholder="Search transactions…" />
          </mat-form-field>
        </div>

        <div class="overflow-x-auto">
          <table mat-table [dataSource]="transactions()" class="w-full">

            <ng-container matColumnDef="id">
              <th mat-header-cell *matHeaderCellDef class="text-xs font-semibold text-slate-500 uppercase tracking-wide py-3 pl-6">Transaction ID</th>
              <td mat-cell *matCellDef="let row" class="py-3 pl-6">
                <span class="font-mono text-sm font-semibold text-slate-700">{{ row.id }}</span>
              </td>
            </ng-container>

            <ng-container matColumnDef="patient">
              <th mat-header-cell *matHeaderCellDef class="text-xs font-semibold text-slate-500 uppercase tracking-wide py-3">Patient</th>
              <td mat-cell *matCellDef="let row" class="py-3">
                <div class="flex items-center gap-3">
                  <div class="w-8 h-8 rounded-full bg-slate-100 flex items-center justify-center text-sm font-semibold text-slate-500 shrink-0">
                    {{ row.patient.charAt(0) }}
                  </div>
                  <span class="text-sm text-slate-700">{{ row.patient }}</span>
                </div>
              </td>
            </ng-container>

            <ng-container matColumnDef="amount">
              <th mat-header-cell *matHeaderCellDef class="text-xs font-semibold text-slate-500 uppercase tracking-wide py-3">Amount</th>
              <td mat-cell *matCellDef="let row">
                <span class="font-semibold text-slate-800">{{ row.amount | currency }}</span>
              </td>
            </ng-container>

            <ng-container matColumnDef="status">
              <th mat-header-cell *matHeaderCellDef class="text-xs font-semibold text-slate-500 uppercase tracking-wide py-3">Status</th>
              <td mat-cell *matCellDef="let row">
                <span class="text-xs font-semibold px-2.5 py-1 rounded-full"
                      [class]="row.status === 'SUCCESS'
                        ? 'bg-green-50 text-green-700 border border-green-200'
                        : 'bg-amber-50 text-amber-700 border border-amber-200'">
                  {{ row.status === 'SUCCESS' ? 'Settled' : 'Pending' }}
                </span>
              </td>
            </ng-container>

            <ng-container matColumnDef="date">
              <th mat-header-cell *matHeaderCellDef class="text-xs font-semibold text-slate-500 uppercase tracking-wide py-3 text-right pr-6">Date</th>
              <td mat-cell *matCellDef="let row" class="text-right pr-6">
                <span class="text-sm text-slate-500">{{ row.date }}</span>
              </td>
            </ng-container>

            <tr mat-header-row *matHeaderRowDef="columns" class="bg-slate-50/50"></tr>
            <tr mat-row *matRowDef="let row; columns: columns;"
                class="border-t border-slate-50 hover:bg-slate-50/80 transition-colors cursor-pointer"></tr>
          </table>
        </div>

        <div class="px-6 py-3 border-t border-slate-100 bg-slate-50/50">
          <span class="text-xs text-slate-400">{{ transactions().length }} transaction(s)</span>
        </div>
      </div>
    </div>
  `,
  styles: [`:host { display: block; } ::ng-deep .mat-mdc-table { background: transparent !important; }`]
})
export class FinanceManagementComponent {
  summaryCards = [
    { label: 'Net Revenue',         value: '$1.24M', icon: 'payments',         iconBg: 'bg-blue-50',   iconColor: 'text-blue-600',  badge: '+12.5%',  badgeClass: 'bg-green-50 text-green-700' },
    { label: 'Pending Claims',      value: '452',    icon: 'pending_actions',  iconBg: 'bg-red-50',    iconColor: 'text-red-600',   badge: 'Review',  badgeClass: 'bg-red-50 text-red-700'   },
    { label: 'Provider Payouts',    value: '$842k',  icon: 'output',           iconBg: 'bg-indigo-50', iconColor: 'text-indigo-600', badge: 'Stable',  badgeClass: 'bg-slate-100 text-slate-600' },
  ];

  transactions = signal([
    { id: 'TX-500', patient: 'John Doe',      amount: 150.00,  status: 'SUCCESS', date: 'Oct 15, 2024' },
    { id: 'TX-501', patient: 'Jane Smith',    amount: 45.00,   status: 'SUCCESS', date: 'Oct 16, 2024' },
    { id: 'TX-502', patient: 'Robert Wilson', amount: 2450.00, status: 'PENDING', date: 'Oct 18, 2024' },
    { id: 'TX-503', patient: 'Sarah Parker',  amount: 320.00,  status: 'SUCCESS', date: 'Oct 22, 2024' },
    { id: 'TX-504', patient: 'Emily Davis',   amount: 890.00,  status: 'PENDING', date: 'Oct 23, 2024' },
  ]);

  columns = ['id', 'patient', 'amount', 'status', 'date'];
}
