import { Component, signal, ViewChild, TemplateRef, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ZrdTableComponent, ZrdBadgeComponent, ZrdButtonComponent, ZrdPageHeaderComponent } from '@repo/ui';
import { PublicApiService } from '../../../core/services/public-api.service';
import { switchMap } from 'rxjs';

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
             <button *ngIf="row.status === 'PENDING'" zrdButton variant="primary" size="sm">Pay Now</button>
             <button *ngIf="row.status === 'PAID'" zrdButton variant="ghost" size="sm">Receipt</button>
          </ng-template>
       </zrd-table>
    </div>
  `
})
export class PaymentListComponent implements OnInit {
  private apiService = inject(PublicApiService);
  loading = signal(false);
  payments = signal<any[]>([]);

  columns: any[] = [
    { key: 'invoiceNumber', header: 'Invoice ID', width: '120px' },
    { key: 'createdAt', header: 'Date', width: '120px' },
    { key: 'amount', header: 'Amount', width: '100px' },
    { key: 'status', header: 'Status', cellTemplate: null, width: '120px' },
    { key: 'actions', header: '', cellTemplate: null, width: '150px' }
  ];

  @ViewChild('statusTemplate') set statusTemplate(v: TemplateRef<any>) { this.columns[3].cellTemplate = v; }
  @ViewChild('actionTemplate') set actionTemplate(v: TemplateRef<any>) { this.columns[4].cellTemplate = v; }

  ngOnInit() {
    this.loadPayments();
  }

  loadPayments() {
    this.loading.set(true);
    this.apiService.getMyProfile().pipe(
      switchMap(profileRes => {
        const patientId = profileRes.data.id;
        return this.apiService.getPatientPayments(patientId);
      })
    ).subscribe({
      next: (res) => {
        this.payments.set(res.data.content);
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
      }
    });
  }
}
