import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ZrdTableComponent, ZrdPageHeaderComponent } from '@repo/ui';

@Component({
  selector: 'app-appointment-management',
  standalone: true,
  imports: [CommonModule, ZrdTableComponent, ZrdPageHeaderComponent],
  template: `
    <zrd-page-header title="Appointment Control" subtitle="Oversee system-wide scheduling and doctor availability."></zrd-page-header>
    <zrd-table [columns]="columns" [data]="appointments()"></zrd-table>
  `
})
export class AppointmentManagementComponent {
  appointments = signal([
    { patient: 'John Doe', doctor: 'Dr. Sarah', date: '2024-10-24', time: '10:00 AM', status: 'CONFIRMED' },
    { patient: 'Jane Smith', doctor: 'Dr. Mike', date: '2024-10-24', time: '11:00 AM', status: 'PENDING' }
  ]);
  columns = [
    { key: 'patient', header: 'Patient' },
    { key: 'doctor', header: 'Doctor' },
    { key: 'date', header: 'Date' },
    { key: 'time', header: 'Time' },
    { key: 'status', header: 'Status' }
  ];
}
