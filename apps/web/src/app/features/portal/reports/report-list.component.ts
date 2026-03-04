import { Component, signal, ViewChild, TemplateRef, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ZrdTableComponent, ZrdBadgeComponent, ZrdButtonComponent, ZrdPageHeaderComponent } from '@repo/ui';
import { PublicApiService } from '../../../core/services/public-api.service';

@Component({
  selector: 'app-report-list',
  standalone: true,
  imports: [CommonModule, ZrdTableComponent, ZrdBadgeComponent, ZrdButtonComponent, ZrdPageHeaderComponent],
  template: `
    <zrd-page-header title="Lab Reports" subtitle="View and download your diagnostic test results."></zrd-page-header>

    <div class="space-y-6">
       <zrd-table [columns]="columns" [data]="reports()" [loading]="loading()">
          <ng-template #statusTemplate let-row>
             <zrd-badge [variant]="row.status === 'COMPLETED' ? 'success' : 'warning'">{{ row.status }}</zrd-badge>
          </ng-template>

          <ng-template #actionTemplate let-row>
             <button zrdButton variant="outline" size="sm" [disabled]="row.status !== 'COMPLETED'">
                <i class="pi pi-file-pdf mr-1"></i> View Report
             </button>
          </ng-template>
       </zrd-table>
    </div>
  `
})
export class ReportListComponent implements OnInit {
  private apiService = inject(PublicApiService);
  loading = signal(false);
  reports = signal<any[]>([]);

  columns: any[] = [
    { key: 'createdAt', header: 'Date', width: '120px' },
    { key: 'reportName', header: 'Test Name' },
    { key: 'doctorName', header: 'Doctor' },
    { key: 'status', header: 'Status', cellTemplate: null, width: '120px' },
    { key: 'actions', header: '', cellTemplate: null, width: '150px' }
  ];

  @ViewChild('statusTemplate') set statusTemplate(v: TemplateRef<any>) {
    this.columns[3].cellTemplate = v;
  }

  @ViewChild('actionTemplate') set actionTemplate(v: TemplateRef<any>) {
    this.columns[4].cellTemplate = v;
  }

  ngOnInit() {
    this.loadReports();
  }

  loadReports() {
    this.loading.set(true);
    this.apiService.getMyReports().subscribe({
      next: (res) => {
        this.reports.set(res.data.content);
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
      }
    });
  }
}
