import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatMenuModule } from '@angular/material/menu';
import { MatDividerModule } from '@angular/material/divider';

@Component({
  selector: 'app-user-management',
  standalone: true,
  imports: [
    CommonModule,
    MatTableModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatChipsModule,
    MatTooltipModule,
    MatFormFieldModule,
    MatInputModule,
    MatMenuModule,
    MatDividerModule
  ],
  template: `
    <div class="space-y-6">

      <!-- Page Header -->
      <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 class="text-2xl font-semibold text-slate-900 m-0">User Management</h1>
          <p class="text-sm text-slate-500 mt-1 m-0">Manage system accounts, roles, and access permissions.</p>
        </div>
        <button mat-flat-button color="primary">
          <mat-icon class="text-[18px]">person_add</mat-icon>
          Add User
        </button>
      </div>

      <!-- Role Summary Cards -->
      <div class="grid grid-cols-2 sm:grid-cols-4 gap-4">
        @for (r of roleSummary; track r.role) {
          <div class="bg-white rounded-xl border border-slate-200 px-5 py-4">
            <div class="flex items-center gap-3">
              <div class="w-9 h-9 rounded-lg flex items-center justify-center" [class]="r.iconBg">
                <mat-icon class="text-[20px]" [class]="r.iconColor">{{ r.icon }}</mat-icon>
              </div>
              <div>
                <p class="text-xl font-bold text-slate-900 m-0">{{ r.count }}</p>
                <p class="text-xs text-slate-500 m-0">{{ r.role }}</p>
              </div>
            </div>
          </div>
        }
      </div>

      <!-- Filters & Table -->
      <div class="bg-white rounded-xl border border-slate-200 overflow-hidden">
        <!-- Toolbar -->
        <div class="flex flex-col sm:flex-row sm:items-center gap-3 px-6 py-4 border-b border-slate-100">
          <mat-form-field appearance="outline" class="flex-1 sm:max-w-sm" subscriptSizing="dynamic">
            <mat-icon matPrefix class="text-slate-400 text-[18px] mr-2">search</mat-icon>
            <input matInput placeholder="Search by name, email or role…"
                   (input)="filterSearch($event)" />
          </mat-form-field>

          <div class="flex items-center gap-2 ml-auto">
            <button mat-stroked-button class="text-slate-600 border-slate-300" [matMenuTriggerFor]="roleMenu">
              <mat-icon class="text-[18px]">filter_list</mat-icon>
              Filter Role
            </button>
            <mat-menu #roleMenu="matMenu">
              <button mat-menu-item (click)="filterRole('')">All Roles</button>
              <button mat-menu-item (click)="filterRole('SUPER_ADMIN')">Super Admin</button>
              <button mat-menu-item (click)="filterRole('ADMIN')">Admin</button>
              <button mat-menu-item (click)="filterRole('DOCTOR')">Doctor</button>
              <button mat-menu-item (click)="filterRole('PATIENT')">Patient</button>
            </mat-menu>
          </div>
        </div>

        <!-- Table -->
        <div class="overflow-x-auto">
          <table mat-table [dataSource]="filteredUsers()" class="w-full">

            <!-- Name / Identity -->
            <ng-container matColumnDef="identity">
              <th mat-header-cell *matHeaderCellDef class="text-xs font-semibold text-slate-500 uppercase tracking-wide py-3 pl-6">User</th>
              <td mat-cell *matCellDef="let row" class="py-3 pl-6">
                <div class="flex items-center gap-3">
                  <div class="w-9 h-9 rounded-full flex items-center justify-center text-sm font-semibold text-white bg-slate-500 shrink-0">
                    {{ row.firstName.charAt(0) }}{{ row.lastName.charAt(0) }}
                  </div>
                  <div>
                    <p class="font-medium text-sm text-slate-900 m-0">{{ row.firstName }} {{ row.lastName }}</p>
                    <p class="text-xs text-slate-400 m-0">{{ row.email }}</p>
                  </div>
                </div>
              </td>
            </ng-container>

            <!-- Roles -->
            <ng-container matColumnDef="roles">
              <th mat-header-cell *matHeaderCellDef class="text-xs font-semibold text-slate-500 uppercase tracking-wide py-3">Roles</th>
              <td mat-cell *matCellDef="let row" class="py-3">
                <div class="flex flex-wrap gap-1.5">
                  @for (role of row.roles; track role) {
                    <span class="text-xs font-semibold px-2 py-0.5 rounded-full"
                          [class]="getRoleStyle(role)">
                      {{ getRoleLabel(role) }}
                    </span>
                  }
                </div>
              </td>
            </ng-container>

            <!-- Status -->
            <ng-container matColumnDef="status">
              <th mat-header-cell *matHeaderCellDef class="text-xs font-semibold text-slate-500 uppercase tracking-wide py-3">Status</th>
              <td mat-cell *matCellDef="let row" class="py-3">
                <span class="text-xs font-semibold px-2.5 py-1 rounded-full"
                      [class]="row.status === 'ACTIVE'
                        ? 'bg-green-50 text-green-700 border border-green-200'
                        : 'bg-red-50 text-red-700 border border-red-200'">
                  {{ row.status === 'ACTIVE' ? 'Active' : 'Inactive' }}
                </span>
              </td>
            </ng-container>

            <!-- Joined -->
            <ng-container matColumnDef="joined">
              <th mat-header-cell *matHeaderCellDef class="text-xs font-semibold text-slate-500 uppercase tracking-wide py-3">Joined</th>
              <td mat-cell *matCellDef="let row">
                <span class="text-sm text-slate-500">{{ row.joined }}</span>
              </td>
            </ng-container>

            <!-- Actions -->
            <ng-container matColumnDef="actions">
              <th mat-header-cell *matHeaderCellDef class="text-xs font-semibold text-slate-500 uppercase tracking-wide py-3 text-right pr-6">Actions</th>
              <td mat-cell *matCellDef="let row" class="text-right pr-6 py-3">
                <button mat-icon-button [matMenuTriggerFor]="actionMenu"
                        class="text-slate-400 hover:text-slate-700 hover:bg-slate-100 rounded-lg">
                  <mat-icon>more_vert</mat-icon>
                </button>
                <mat-menu #actionMenu="matMenu">
                  <button mat-menu-item>
                    <mat-icon>edit</mat-icon> Edit User
                  </button>
                  <button mat-menu-item>
                    <mat-icon>manage_accounts</mat-icon> Change Role
                  </button>
                  <button mat-menu-item>
                    <mat-icon>lock_reset</mat-icon> Reset Password
                  </button>
                  <mat-divider></mat-divider>
                  <button mat-menu-item class="text-red-600">
                    <mat-icon class="text-red-500">block</mat-icon>
                    {{ row.status === 'ACTIVE' ? 'Deactivate' : 'Activate' }}
                  </button>
                </mat-menu>
              </td>
            </ng-container>

            <tr mat-header-row *matHeaderRowDef="columns" class="bg-slate-50/50"></tr>
            <tr mat-row *matRowDef="let row; columns: columns;"
                class="border-t border-slate-50 hover:bg-slate-50/80 transition-colors cursor-pointer"></tr>
          </table>
        </div>

        <!-- Empty State -->
        @if (filteredUsers().length === 0) {
          <div class="py-16 text-center">
            <mat-icon class="text-slate-300 text-[48px] w-12 h-12 mb-3">person_off</mat-icon>
            <p class="font-medium text-slate-500 text-sm">No users found</p>
            <p class="text-xs text-slate-400">Try adjusting your search or filter.</p>
          </div>
        }

        <!-- Footer -->
        <div class="flex items-center justify-between px-6 py-3 border-t border-slate-100 bg-slate-50/50">
          <span class="text-xs text-slate-400">{{ filteredUsers().length }} user(s)</span>
        </div>
      </div>
    </div>
  `,
  styles: [`
    :host { display: block; }
    ::ng-deep .mat-mdc-table { background: transparent !important; }
  `]
})
export class UserManagementComponent {
  searchQuery = signal('');
  roleFilter = signal('');

  allUsers = signal([
    { id: '1', firstName: 'Admin',   lastName: 'User',       email: 'admin@orthosync.com',      roles: ['SUPER_ADMIN', 'ADMIN'], status: 'ACTIVE',   joined: 'Jan 1, 2024' },
    { id: '2', firstName: 'Sarah',   lastName: 'Johnson',    email: 'sarah.j@orthosync.com',    roles: ['ADMIN'],                status: 'ACTIVE',   joined: 'Feb 14, 2024' },
    { id: '3', firstName: 'Dr. Mike', lastName: 'Ross',      email: 'mike.ross@hospital.com',   roles: ['DOCTOR'],               status: 'ACTIVE',   joined: 'Mar 3, 2024' },
    { id: '4', firstName: 'John',    lastName: 'Doe',         email: 'john.doe@gmail.com',       roles: ['PATIENT'],              status: 'ACTIVE',   joined: 'Apr 10, 2024' },
    { id: '5', firstName: 'Mike',    lastName: 'Reception',  email: 'mike.r@hospital.com',      roles: ['STAFF'],                status: 'INACTIVE', joined: 'May 5, 2024' },
    { id: '6', firstName: 'Lisa',    lastName: 'Chen',       email: 'lisa.chen@orthosync.com',  roles: ['DOCTOR'],               status: 'ACTIVE',   joined: 'Jun 20, 2024' },
  ]);

  filteredUsers = () => {
    let users = this.allUsers();
    const q = this.searchQuery().toLowerCase();
    const role = this.roleFilter();
    if (q) users = users.filter(u =>
      (u.firstName + ' ' + u.lastName).toLowerCase().includes(q) ||
      u.email.toLowerCase().includes(q) ||
      u.roles.some(r => r.toLowerCase().includes(q))
    );
    if (role) users = users.filter(u => u.roles.includes(role));
    return users;
  };

  roleSummary = [
    { role: 'Super Admins', count: 1, icon: 'shield',           iconBg: 'bg-purple-50', iconColor: 'text-purple-600' },
    { role: 'Admins',       count: 2, icon: 'admin_panel_settings', iconBg: 'bg-blue-50',   iconColor: 'text-blue-600'   },
    { role: 'Doctors',      count: 2, icon: 'medical_services', iconBg: 'bg-teal-50',   iconColor: 'text-teal-600'   },
    { role: 'Patients',     count: 1, icon: 'person',           iconBg: 'bg-amber-50',  iconColor: 'text-amber-600'  },
  ];

  columns = ['identity', 'roles', 'status', 'joined', 'actions'];

  getRoleLabel(role: string): string {
    const map: Record<string, string> = {
      SUPER_ADMIN: 'Super Admin',
      ADMIN: 'Admin',
      DOCTOR: 'Doctor',
      PATIENT: 'Patient',
      STAFF: 'Staff',
    };
    return map[role] ?? role;
  }

  getRoleStyle(role: string): string {
    const map: Record<string, string> = {
      SUPER_ADMIN: 'bg-purple-50 text-purple-700 border border-purple-200',
      ADMIN:       'bg-blue-50 text-blue-700 border border-blue-200',
      DOCTOR:      'bg-teal-50 text-teal-700 border border-teal-200',
      PATIENT:     'bg-amber-50 text-amber-700 border border-amber-200',
      STAFF:       'bg-slate-100 text-slate-600 border border-slate-200',
    };
    return map[role] ?? 'bg-slate-100 text-slate-600';
  }

  filterSearch(event: Event) {
    this.searchQuery.set((event.target as HTMLInputElement).value);
  }

  filterRole(role: string) {
    this.roleFilter.set(role);
  }
}
