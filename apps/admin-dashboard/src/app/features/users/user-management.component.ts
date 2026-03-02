import { Component, signal, ViewChild, TemplateRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ZrdTableComponent, ZrdBadgeComponent, ZrdButtonComponent, ZrdPageHeaderComponent, ZrdSearchInputComponent, ZrdAvatarComponent } from '@repo/ui';

@Component({
  selector: 'app-user-management',
  standalone: true,
  imports: [CommonModule, ZrdTableComponent, ZrdBadgeComponent, ZrdButtonComponent, ZrdPageHeaderComponent, ZrdSearchInputComponent, ZrdAvatarComponent],
  template: `
    <zrd-page-header title="Platform Users" subtitle="System-wide user administration, role assignment, and security.">
       <button actions zrdButton variant="primary">
          <i class="pi pi-user-plus mr-2"></i> Create User
       </button>
    </zrd-page-header>

    <div class="space-y-6">
       <div class="grid grid-cols-1 md:flex justify-between items-center gap-4 bg-white p-4 rounded-2xl border border-secondary-100 shadow-sm">
          <zrd-search-input placeholder="Search by email, role, or name..." class="max-w-md"></zrd-search-input>
          <div class="flex gap-2">
             <button zrdButton variant="outline" size="sm"><i class="pi pi-shield mr-2"></i> Role Manager</button>
             <button zrdButton variant="outline" size="sm"><i class="pi pi-lock-open mr-2"></i> Reset PW</button>
          </div>
       </div>

       <zrd-table [columns]="columns" [data]="users()" [loading]="loading()">
          <ng-template #userTemplate let-row>
             <div class="flex items-center gap-3">
                <zrd-avatar [name]="row.firstName + ' ' + row.lastName" size="sm"></zrd-avatar>
                <div>
                   <p class="text-sm font-bold text-secondary-900">{{ row.firstName }} {{ row.lastName }}</p>
                   <p class="text-[10px] text-secondary-400 font-medium tracking-tight">{{ row.email }}</p>
                </div>
             </div>
          </ng-template>

          <ng-template #roleTemplate let-row>
             <div class="flex flex-wrap gap-1">
                <zrd-badge *ngFor="let role of row.roles" variant="outline">{{ role }}</zrd-badge>
             </div>
          </ng-template>

          <ng-template #statusTemplate let-row>
             <zrd-badge [variant]="row.status === 'ACTIVE' ? 'success' : 'danger'">{{ row.status }}</zrd-badge>
          </ng-template>

          <ng-template #actionTemplate let-row>
             <button zrdButton variant="ghost" size="sm"><i class="pi pi-ellipsis-v"></i></button>
          </ng-template>
       </zrd-table>
    </div>
  `
})
export class UserManagementComponent {
  loading = signal(false);
  
  users = signal([
    { id: '1', firstName: 'Admin', lastName: 'User', email: 'admin@orthosync.com', roles: ['SUPER_ADMIN', 'ADMIN'], status: 'ACTIVE' },
    { id: '2', firstName: 'Sarah', lastName: 'Johnson', email: 'sarah.j@orthosync.com', roles: ['DOCTOR'], status: 'ACTIVE' },
    { id: '3', firstName: 'John', lastName: 'Doe', email: 'john.doe@gmail.com', roles: ['PATIENT'], status: 'ACTIVE' },
    { id: '4', firstName: 'Mike', lastName: 'Reception', email: 'mike.r@hospital.com', roles: ['RECEPTIONIST'], status: 'INACTIVE' },
  ]);

  columns: any[] = [
    { key: 'user', header: 'Identity', cellTemplate: null },
    { key: 'roles', header: 'Roles & Permissions', cellTemplate: null },
    { key: 'status', header: 'Access Status', cellTemplate: null, width: '120px' },
    { key: 'actions', header: '', cellTemplate: null, width: '80px' }
  ];

  @ViewChild('userTemplate') set userTemplate(v: TemplateRef<any>) { this.columns[0].cellTemplate = v; }
  @ViewChild('roleTemplate') set roleTemplate(v: TemplateRef<any>) { this.columns[1].cellTemplate = v; }
  @ViewChild('statusTemplate') set statusTemplate(v: TemplateRef<any>) { this.columns[2].cellTemplate = v; }
  @ViewChild('actionTemplate') set actionTemplate(v: TemplateRef<any>) { this.columns[3].cellTemplate = v; }
}
