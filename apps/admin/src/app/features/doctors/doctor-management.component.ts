import { Component, signal, ViewChild, TemplateRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ZrdTableComponent, ZrdBadgeComponent, ZrdButtonComponent, ZrdPageHeaderComponent, ZrdSearchInputComponent, ZrdAvatarComponent } from '@repo/ui';

@Component({
  selector: 'app-doctor-management',
  standalone: true,
  imports: [CommonModule, ZrdTableComponent, ZrdBadgeComponent, ZrdButtonComponent, ZrdPageHeaderComponent, ZrdSearchInputComponent, ZrdAvatarComponent],
  template: `
    <zrd-page-header title="Medical Staff" subtitle="Manage hospital surgeons, specializations, and availability.">
       <button actions zrdButton variant="primary">
          <i class="pi pi-plus mr-2"></i> Onboard Doctor
       </button>
    </zrd-page-header>

    <div class="space-y-6">
       <div class="flex justify-between items-center bg-white p-4 rounded-2xl border border-secondary-100 shadow-sm">
          <zrd-search-input placeholder="Filter by name, specialty, or hospital..." class="max-w-md"></zrd-search-input>
          <div class="flex gap-2">
             <button zrdButton variant="outline" size="sm"><i class="pi pi-filter mr-2"></i> Filters</button>
             <button zrdButton variant="outline" size="sm"><i class="pi pi-download mr-2"></i> Export</button>
          </div>
       </div>

       <zrd-table [columns]="columns" [data]="doctors()" [loading]="loading()">
          <ng-template #doctorTemplate let-row>
             <div class="flex items-center gap-3">
                <zrd-avatar [name]="row.name" size="sm"></zrd-avatar>
                <div>
                   <p class="text-sm font-bold text-secondary-900">{{ row.name }}</p>
                   <p class="text-[10px] text-secondary-400 font-medium uppercase">{{ row.specialization }}</p>
                </div>
             </div>
          </ng-template>

          <ng-template #statusTemplate let-row>
             <zrd-badge [variant]="row.status === 'ACTIVE' ? 'success' : 'warning'">{{ row.status }}</zrd-badge>
          </ng-template>

          <ng-template #actionTemplate let-row>
             <div class="flex gap-2">
                <button zrdButton variant="ghost" size="sm"><i class="pi pi-pencil"></i></button>
                <button zrdButton variant="ghost" size="sm" class="text-red-600"><i class="pi pi-trash"></i></button>
             </div>
          </ng-template>
       </zrd-table>
    </div>
  `
})
export class DoctorManagementComponent {
  loading = signal(false);
  
  doctors = signal([
    { id: '1', name: 'Dr. Sarah Johnson', specialization: 'Orthopedic Surgery', hospital: 'City Orthopedic', status: 'ACTIVE', experience: '12 Yrs' },
    { id: '2', name: 'Dr. Mike Ross', specialization: 'Sports Medicine', hospital: 'Bone Health Center', status: 'ACTIVE', experience: '8 Yrs' },
    { id: '3', name: 'Dr. David King', specialization: 'Pediatric Ortho', hospital: 'Metro General', status: 'ON_LEAVE', experience: '15 Yrs' },
  ]);

  columns: any[] = [
    { key: 'name', header: 'Specialist', cellTemplate: null },
    { key: 'hospital', header: 'Affiliated Hospital' },
    { key: 'experience', header: 'Exp.', width: '80px' },
    { key: 'status', header: 'Status', cellTemplate: null, width: '120px' },
    { key: 'actions', header: '', cellTemplate: null, width: '100px' }
  ];

  @ViewChild('doctorTemplate') set doctorTemplate(v: TemplateRef<any>) { this.columns[0].cellTemplate = v; }
  @ViewChild('statusTemplate') set statusTemplate(v: TemplateRef<any>) { this.columns[3].cellTemplate = v; }
  @ViewChild('actionTemplate') set actionTemplate(v: TemplateRef<any>) { this.columns[4].cellTemplate = v; }
}
