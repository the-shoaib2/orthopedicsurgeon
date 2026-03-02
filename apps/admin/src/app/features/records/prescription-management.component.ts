import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ZrdTableComponent, ZrdPageHeaderComponent } from '@repo/ui';

@Component({
  selector: 'app-prescription-management',
  standalone: true,
  imports: [CommonModule, ZrdTableComponent, ZrdPageHeaderComponent],
  template: `
    <zrd-page-header title="Prescription Audit" subtitle="Review and audit digital prescriptions issued by staff."></zrd-page-header>
    <zrd-table [columns]="columns" [data]="prescriptions()"></zrd-table>
  `
})
export class PrescriptionManagementComponent {
  prescriptions = signal([
    { id: 'RX-100', doctor: 'Dr. Sarah', patient: 'John Doe', date: '2024-10-15' },
    { id: 'RX-101', doctor: 'Dr. Mike', patient: 'Jane Smith', date: '2024-10-20' }
  ]);
  columns = [
    { key: 'id', header: 'Rx ID' },
    { key: 'doctor', header: 'Doctor' },
    { key: 'patient', header: 'Patient' },
    { key: 'date', header: 'Date' }
  ];
}
