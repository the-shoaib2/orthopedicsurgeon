import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ZrdTableComponent, ZrdPageHeaderComponent } from '@repo/ui';

@Component({
  selector: 'app-report-management',
  standalone: true,
  imports: [CommonModule, ZrdTableComponent, ZrdPageHeaderComponent],
  template: `
    <zrd-page-header title="Diagnostic Reports" subtitle="Manage and verify laboratory and radiology reports."></zrd-page-header>
    <zrd-table [columns]="columns" [data]="reports()"></zrd-table>
  `
})
export class ReportManagementComponent {
  reports = signal([
    { test: 'CBC', patient: 'John Doe', status: 'FINAL', date: '2024-10-15' },
    { test: 'MRI', patient: 'Jane Smith', status: 'PENDING', date: '2024-10-20' }
  ]);
  columns = [
    { key: 'date', header: 'Date' },
    { key: 'patient', header: 'Patient' },
    { key: 'test', header: 'Test' },
    { key: 'status', header: 'Status' }
  ];
}
