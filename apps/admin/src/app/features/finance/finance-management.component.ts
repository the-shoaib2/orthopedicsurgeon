import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ZrdStatComponent, ZrdTableComponent, ZrdPageHeaderComponent } from '@repo/ui';

@Component({
  selector: 'app-finance-management',
  standalone: true,
  imports: [CommonModule, ZrdStatComponent, ZrdTableComponent, ZrdPageHeaderComponent],
  template: `
    <zrd-page-header title="Financial Control" subtitle="Oversee system revenue, hospital billings, and insurance claims."></zrd-page-header>
    <div class="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
       <zrd-stat label="Net Revenue" value="$1.2M" description="This quarter" [icon]="true"><span icon class="pi pi-dollar text-green-600"></span></zrd-stat>
       <zrd-stat label="Pending Claims" value="450" description="Awaiting verification" [icon]="true"><span icon class="pi pi-clock text-amber-600"></span></zrd-stat>
       <zrd-stat label="Payouts" value="$840k" description="Distributed to providers" [icon]="true"><span icon class="pi pi-external-link text-primary-600"></span></zrd-stat>
    </div>
    <zrd-table [columns]="columns" [data]="transactions()"></zrd-table>
  `
})
export class FinanceManagementComponent {
  transactions = signal([
    { id: 'TX-500', patient: 'John Doe', amount: 150.00, status: 'SUCCESS', date: '2024-10-15' },
    { id: 'TX-501', patient: 'Jane Smith', amount: 45.00, status: 'SUCCESS', date: '2024-10-16' }
  ]);
  columns = [
    { key: 'id', header: 'Tx ID' },
    { key: 'patient', header: 'Patient' },
    { key: 'amount', header: 'Amount' },
    { key: 'status', header: 'Status' },
    { key: 'date', header: 'Date' }
  ];
}
