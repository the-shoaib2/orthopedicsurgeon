import { Component, signal, ViewChild, TemplateRef, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ZrdTableComponent, ZrdBadgeComponent, ZrdButtonComponent, ZrdPageHeaderComponent } from '@repo/ui';
import { PublicApiService } from '../../../core/services/public-api.service';

@Component({
  selector: 'app-prescription-list',
  standalone: true,
  imports: [CommonModule, ZrdTableComponent, ZrdButtonComponent, ZrdPageHeaderComponent],
  template: `
    <zrd-page-header title="Prescriptions" subtitle="A digital record of all medications prescribed to you."></zrd-page-header>

    <div class="space-y-6">
       <zrd-table [columns]="columns" [data]="prescriptions()" [loading]="loading()">
          <ng-template #actionTemplate let-row>
             <button zrdButton variant="outline" size="sm">
                <i class="pi pi-download mr-1"></i> PDF
             </button>
          </ng-template>
       </zrd-table>
    </div>
  `
})
export class PrescriptionListComponent implements OnInit {
  private apiService = inject(PublicApiService);
  loading = signal(false);
  prescriptions = signal<any[]>([]);

  columns: any[] = [
    { key: 'createdAt', header: 'Date', width: '120px' },
    { key: 'doctorName', header: 'Doctor' },
    { key: 'diagnosis', header: 'Diagnosis' },
    { key: 'status', header: 'Status' },
    { key: 'actions', header: 'Download', cellTemplate: null, width: '120px' }
  ];

  @ViewChild('actionTemplate') set actionTemplate(v: TemplateRef<any>) {
    this.columns[4].cellTemplate = v;
  }

  ngOnInit() {
    this.loadPrescriptions();
  }

  loadPrescriptions() {
    this.loading.set(true);
    this.apiService.getMyPrescriptions().subscribe({
      next: (res) => {
        this.prescriptions.set(res.data.content);
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
      }
    });
  }
}
