import { Component, signal, ViewChild, TemplateRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ZrdTableComponent, ZrdBadgeComponent, ZrdButtonComponent, ZrdPageHeaderComponent } from '@repo/ui';

@Component({
  selector: 'app-appointment-list',
  standalone: true,
  imports: [CommonModule, RouterModule, ZrdTableComponent, ZrdBadgeComponent, ZrdButtonComponent, ZrdPageHeaderComponent],
  template: `
    <zrd-page-header title="My Appointments" subtitle="Manage your upcoming and past medical consultations.">
       <button actions zrdButton variant="primary" routerLink="/doctors">New Booking</button>
    </zrd-page-header>

    <div class="space-y-6">
       <zrd-table [columns]="columns" [data]="appointments()" [loading]="loading()">
          <ng-template #statusTemplate let-row>
             <zrd-badge [variant]="getStatusVariant(row.status)">{{ row.status }}</zrd-badge>
          </ng-template>

          <ng-template #actionTemplate let-row>
             <div class="flex gap-2">
                <button zrdButton variant="ghost" size="sm">Details</button>
                <button *ngIf="row.status === 'SCHEDULED'" zrdButton variant="outline" size="sm" class="text-red-600 border-red-100 hover:bg-red-50">Cancel</button>
             </div>
          </ng-template>
       </zrd-table>
    </div>
  `
})
export class AppointmentListComponent {
  loading = signal(false);
  
  appointments = signal([
    { id: '1', doctor: 'Dr. Sarah Johnson', hospital: 'City Orthopedic', date: '2024-10-24', time: '10:00 AM', type: 'Follow up', status: 'SCHEDULED' },
    { id: '2', doctor: 'Dr. Mike Ross', hospital: 'Bone Health Center', date: '2024-10-28', time: '02:30 PM', type: 'Consultation', status: 'SCHEDULED' },
    { id: '3', doctor: 'Dr. Sarah Johnson', hospital: 'City Orthopedic', date: '2024-10-10', time: '11:00 AM', type: 'Emergency', status: 'COMPLETED' },
    { id: '4', doctor: 'Dr. David King', hospital: 'Metro General', date: '2024-10-05', time: '09:00 AM', type: 'Surgery', status: 'COMPLETED' },
  ]);

  columns: any[] = [
    { key: 'doctor', header: 'Specialist' },
    { key: 'hospital', header: 'Location' },
    { key: 'date', header: 'Date', width: '120px' },
    { key: 'time', header: 'Time', width: '100px' },
    { key: 'type', header: 'Type' },
    { key: 'status', header: 'Status', cellTemplate: null, width: '120px' },
    { key: 'actions', header: '', cellTemplate: null, width: '150px' }
  ];

  @ViewChild('statusTemplate') set statusTemplate(v: TemplateRef<any>) { this.columns[5].cellTemplate = v; }
  @ViewChild('actionTemplate') set actionTemplate(v: TemplateRef<any>) { this.columns[6].cellTemplate = v; }

  getStatusVariant(status: string): any {
    switch (status) {
      case 'SCHEDULED': return 'info';
      case 'COMPLETED': return 'success';
      case 'CANCELLED': return 'danger';
      default: return 'default';
    }
  }
}
