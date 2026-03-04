import { Component, signal, ViewChild, TemplateRef, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ZrdTableComponent, ZrdBadgeComponent, ZrdButtonComponent, ZrdPageHeaderComponent } from '@repo/ui';
import { PublicApiService } from '../../../core/services/public-api.service';

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
                <button *ngIf="row.status === 'PENDING' || row.status === 'CONFIRMED'" zrdButton variant="outline" size="sm" class="text-red-600 border-red-100 hover:bg-red-50">Cancel</button>
             </div>
          </ng-template>
       </zrd-table>
    </div>
  `
})
export class AppointmentListComponent implements OnInit {
  private apiService = inject(PublicApiService);
  loading = signal(false);
  appointments = signal<any[]>([]);

  columns: any[] = [
    { key: 'doctorName', header: 'Specialist' },
    { key: 'appointmentDate', header: 'Date', width: '120px' },
    { key: 'startTime', header: 'Time', width: '100px' },
    { key: 'type', header: 'Type' },
    { key: 'status', header: 'Status', cellTemplate: null, width: '120px' },
    { key: 'actions', header: '', cellTemplate: null, width: '150px' }
  ];

  @ViewChild('statusTemplate') set statusTemplate(v: TemplateRef<any>) { this.columns[4].cellTemplate = v; }
  @ViewChild('actionTemplate') set actionTemplate(v: TemplateRef<any>) { this.columns[5].cellTemplate = v; }

  ngOnInit() {
    this.loadAppointments();
  }

  loadAppointments() {
    this.loading.set(true);
    this.apiService.getMyAppointments().subscribe({
      next: (res) => {
        this.appointments.set(res.data.content);
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
      }
    });
  }

  getStatusVariant(status: string): any {
    switch (status) {
      case 'PENDING': return 'warning';
      case 'CONFIRMED': return 'info';
      case 'COMPLETED': return 'success';
      case 'CANCELLED': return 'danger';
      default: return 'default';
    }
  }
}
