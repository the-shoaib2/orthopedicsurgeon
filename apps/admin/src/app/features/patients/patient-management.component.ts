import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ZrdTableComponent, ZrdPageHeaderComponent } from '@repo/ui';

@Component({
  selector: 'app-patient-management',
  standalone: true,
  imports: [CommonModule, ZrdTableComponent, ZrdPageHeaderComponent],
  template: `
    <zrd-page-header title="Patient Registry" subtitle="Comprehensive patient database and medical history access."></zrd-page-header>
    <zrd-table [columns]="columns" [data]="patients()"></zrd-table>
  `
})
export class PatientManagementComponent {
  patients = signal([
    { name: 'John Doe', age: 45, gender: 'MALE', lastVisit: '2024-10-15', status: 'ACTIVE' },
    { name: 'Jane Smith', age: 32, gender: 'FEMALE', lastVisit: '2024-10-20', status: 'ACTIVE' }
  ]);
  columns = [
    { key: 'name', header: 'Patient Name' },
    { key: 'age', header: 'Age' },
    { key: 'gender', header: 'Gender' },
    { key: 'lastVisit', header: 'Last Visit' },
    { key: 'status', header: 'Status' }
  ];
}
