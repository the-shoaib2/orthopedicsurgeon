import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatDividerModule } from '@angular/material/divider';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';

@Component({
  selector: 'app-partner-management',
  standalone: true,
  imports: [
    CommonModule, MatButtonModule, MatIconModule,
    MatMenuModule, MatDividerModule, MatFormFieldModule, MatInputModule
  ],
  template: `
    <div class="space-y-6">
      <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 class="text-2xl font-semibold text-slate-900 m-0">Partners</h1>
          <p class="text-sm text-slate-500 mt-1 m-0">Manage partner organizations and their logos displayed on the website.</p>
        </div>
        <button mat-flat-button color="primary">
          <mat-icon class="text-[18px]">add</mat-icon>
          Add Partner
        </button>
      </div>

      <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
        @for (partner of partners(); track partner.id) {
          <div class="bg-white rounded-xl border border-slate-200 p-5 hover:shadow-md transition-shadow">
            <div class="flex items-start justify-between gap-3">
              <div class="w-12 h-12 rounded-xl bg-slate-100 flex items-center justify-center shrink-0">
                <mat-icon class="text-slate-500 text-[24px]">handshake</mat-icon>
              </div>
              <button mat-icon-button [matMenuTriggerFor]="menu"
                      class="text-slate-400 hover:text-slate-700 hover:bg-slate-100 rounded-lg -mt-1 -mr-1">
                <mat-icon class="text-[18px]">more_vert</mat-icon>
              </button>
              <mat-menu #menu="matMenu">
                <button mat-menu-item><mat-icon>edit</mat-icon> Edit</button>
                <button mat-menu-item><mat-icon>upload</mat-icon> Update Logo</button>
                <mat-divider></mat-divider>
                <button mat-menu-item class="text-red-600"><mat-icon class="text-red-500">delete</mat-icon> Remove</button>
              </mat-menu>
            </div>
            <h3 class="font-semibold text-slate-900 text-sm mt-3 mb-1">{{ partner.name }}</h3>
            <p class="text-xs text-slate-500 m-0">{{ partner.category }}</p>
            <div class="flex items-center gap-2 mt-3">
              <span class="text-xs font-semibold px-2 py-0.5 rounded-full"
                    [class]="partner.active
                      ? 'bg-green-50 text-green-700 border border-green-200'
                      : 'bg-slate-100 text-slate-500 border border-slate-200'">
                {{ partner.active ? 'Active' : 'Hidden' }}
              </span>
              <span class="text-xs text-slate-400">Since {{ partner.since }}</span>
            </div>
          </div>
        }
      </div>
    </div>
  `,
  styles: [`:host { display: block; }`]
})
export class PartnerManagementComponent {
  partners = signal([
    { id: 1, name: 'Metro Health Group',    category: 'Hospital Network',     active: true,  since: '2022' },
    { id: 2, name: 'PhysioFirst',           category: 'Rehabilitation Center', active: true,  since: '2023' },
    { id: 3, name: 'BioMed Innovations',    category: 'Medical Technology',    active: true,  since: '2021' },
    { id: 4, name: 'Global Health Fund',    category: 'Healthcare Foundation', active: false, since: '2023' },
    { id: 5, name: 'SurgeTech Labs',        category: 'Research & Development', active: true, since: '2024' },
    { id: 6, name: 'CareBridge Insurance',  category: 'Insurance Provider',    active: true,  since: '2022' },
  ]);
}
