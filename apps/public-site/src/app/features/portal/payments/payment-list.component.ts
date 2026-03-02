import { Component, signal, ViewChild, TemplateRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ZrdTableComponent, ZrdBadgeComponent, ZrdButtonComponent, ZrdPageHeaderComponent } from '@repo/ui';

@Component({
  selector: 'app-payment-list',
  standalone: true,
  imports: [CommonModule, ZrdTableComponent, ZrdBadgeComponent, ZrdButtonComponent, ZrdPageHeaderComponent],
  template: `
    <zrd-page-header title="Billing & Payments" subtitle="View your transaction history and settle outstanding invoices."></zrd-page-header>

    <div class="space-y-6">
       <zrd-table [columns]="columns" [data]="payments()" [loading]="loading()">
          <ng-template #statusTemplate let-row>
             <zrd-badge [variant]="row.status === 'PAID' ? 'success' : 'danger'">{{ row.status }}</zrd-badge>
          </ng-template>

          <ng-template #actionTemplate let-row>
             <button *ngIf="row.status === 'UNPAID'" zrdButton variant="primary" size="sm">Pay Now</button>
             <button *ngIf="row.status === 'PAID'" zrdButton variant="ghost" size="sm">Receipt</button>
          </ng-template>
       </zrd-table>
    </div>
  `
})
export class PaymentListComponent {
  loading = signal(false);
  
  payments = signal([
    { id: 'INV-101', date: '2024-10-10', doctor: 'Dr. Sarah Johnson', service: 'Consultation Fee', amount: 150.00, status: 'PAID' },
    { id: 'INV-102', date: '2024-10-12', doctor: 'Dr. Mike Ross', service: 'Lab Test: CBC', amount: 45.00, status: 'PAID' },
    { id: 'INV-103', date: '2024-10-22', doctor: 'Dr. Sarah Johnson', service: 'MRI Scan', amount: 450.00, status: 'UNPAID' },
  ]);

  columns: any[] = [
    { key: 'id', header: 'Invoice ID', width: '120px' },
    { key: 'date', header: 'Date', width: '120px' },
    { key: 'service', header: 'Service' },
    { key: 'amount', header: 'Amount', width: '100px' },
    { key: 'status', header: 'Status', cellTemplate: null, width: '120px' },
    { key: 'actions', header: '', cellTemplate: null, width: '150px' }
  ];

  @ViewChild('statusTemplate') set statusTemplate(v: TemplateRef<any>) { this.columns[4].cellTemplate = v; }
  @ViewChild('actionTemplate') set actionTemplate(v: TemplateRef<any>) { this.columns[5].cellTemplate = v; }
}
