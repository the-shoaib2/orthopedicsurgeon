import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ZrdTableComponent, ZrdPageHeaderComponent } from '@repo/ui';

@Component({
  selector: 'app-hospital-management',
  standalone: true,
  imports: [CommonModule, ZrdTableComponent, ZrdPageHeaderComponent],
  template: `
    <zrd-page-header title="Organization" subtitle="Manage hospital network, clinics, and service categories."></zrd-page-header>
    <zrd-table [columns]="columns" [data]="hospitals()"></zrd-table>
  `
})
export class HospitalManagementComponent {
  hospitals = signal([
    { name: 'City Orthopedic', city: 'Dhaka', doctors: 45, status: 'ACTIVE' },
    { name: 'Bone Health Center', city: 'Chittagong', doctors: 28, status: 'ACTIVE' }
  ]);
  columns = [
    { key: 'name', header: 'Hospital' },
    { key: 'city', header: 'City' },
    { key: 'doctors', header: 'Staff Count' },
    { key: 'status', header: 'Status' }
  ];
}
