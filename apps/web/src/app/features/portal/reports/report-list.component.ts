import { Component, signal, ViewChild, TemplateRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ZrdTableComponent, ZrdBadgeComponent, ZrdButtonComponent, ZrdPageHeaderComponent } from '@repo/ui';

@Component({
  selector: 'app-report-list',
  standalone: true,
  imports: [CommonModule, ZrdTableComponent, ZrdBadgeComponent, ZrdButtonComponent, ZrdPageHeaderComponent],
  template: `
    <zrd-page-header title="Lab Reports" subtitle="View and download your diagnostic test results."></zrd-page-header>

    <div class="space-y-6">
       <zrd-table [columns]="columns" [data]="reports()" [loading]="loading()">
          <ng-template #statusTemplate let-row>
             <zrd-badge [variant]="row.status === 'FINAL' ? 'success' : 'warning'">{{ row.status }}</zrd-badge>
          </ng-template>

          <ng-template #actionTemplate let-row>
             <button zrdButton variant="outline" size="sm" [disabled]="row.status !== 'FINAL'">
                <i class="pi pi-file-pdf mr-1"></i> View Report
             </button>
          </ng-template>
       </zrd-table>
    </div>
  `
})
export class ReportListComponent {
  loading = signal(false);
  
  reports = signal([
    { id: '1', testName: 'Complete Blood Count (CBC)', category: 'Hematology', date: '2024-10-15', status: 'FINAL', doctor: 'Dr. Sarah Johnson' },
    { id: '2', testName: 'MRI Knee - Left', category: 'Radiology', date: '2024-10-12', status: 'FINAL', doctor: 'Dr. Sarah Johnson' },
    { id: '3', testName: 'Bone Density Test', category: 'Imaging', date: '2024-10-22', status: 'PENDING', doctor: 'Dr. Mike Ross' },
  ]);

  columns: any[] = [
    { key: 'date', header: 'Date', width: '120px' },
    { key: 'testName', header: 'Test Name' },
    { key: 'category', header: 'Category' },
    { key: 'status', header: 'Status', cellTemplate: null, width: '120px' },
    { key: 'actions', header: '', cellTemplate: null, width: '150px' }
  ];

  @ViewChild('statusTemplate') set statusTemplate(v: TemplateRef<any>) {
    this.columns[3].cellTemplate = v;
  }

  @ViewChild('actionTemplate') set actionTemplate(v: TemplateRef<any>) {
    this.columns[4].cellTemplate = v;
  }
}
