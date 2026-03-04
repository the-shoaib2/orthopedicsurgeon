import { Component, signal, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { AdminApiService } from '@core/services/admin-api.service';

@Component({
  selector: 'app-partner-management',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatChipsModule,
    MatTooltipModule,
    MatProgressBarModule
  ],
  template: `
    <div class="space-y-6">
      <div class="flex flex-col lg:flex-row lg:items-center justify-between gap-4 pb-4">
        <div class="flex items-center gap-4">
          <mat-icon color="primary" class="scale-150 ml-2">handshake</mat-icon>
          <div>
            <h1 class="text-2xl font-medium m-0">Partner Management</h1>
            <p class="text-sm text-slate-500 m-0">Manage partner logos and affiliations</p>
          </div>
        </div>
        <button mat-flat-button color="primary">
           Add Partner
        </button>
      </div>

      <mat-progress-bar *ngIf="loading()" mode="query" color="primary" class="h-1 rounded-full"></mat-progress-bar>

      <div class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 2xl:grid-cols-6 gap-6">
        <mat-card *ngFor="let partner of partners()" class="overflow-hidden group">
           <div class="h-40 flex items-center justify-center p-6 bg-slate-50 relative">
              <img [src]="partner.logoUrl" class="max-w-full max-h-full object-contain filter grayscale group-hover:grayscale-0 transition-all duration-300" />
              
              <div class="absolute top-2 right-2 opacity-0 group-hover:opacity-100 transition-opacity">
                 <button mat-icon-button color="warn" class="bg-white/90" (click)="deletePartner(partner.id)">
                   <mat-icon>delete</mat-icon>
                 </button>
              </div>
           </div>
           
           <mat-card-content class="pt-4 border-t border-slate-100">
              <p class="text-sm font-medium truncate mb-2">{{ partner.name }}</p>
              <div class="flex items-center justify-between text-xs text-slate-500">
                 <div class="flex items-center gap-1">
                    <span>Order:</span>
                    <span class="font-medium">{{ partner.displayOrder }}</span>
                 </div>
                 <span [class]="partner.isActive ? 'text-green-600 font-medium' : 'text-slate-500'">
                    {{ partner.isActive ? 'ACTIVE' : 'INACTIVE' }}
                 </span>
              </div>
           </mat-card-content>
        </mat-card>
        
        <div *ngIf="partners().length === 0 && !loading()" class="col-span-full py-12 text-center text-slate-500">
           <mat-icon class="scale-150 mb-4 text-slate-400">handshake</mat-icon>
           <p class="font-medium text-sm">No partners configured</p>
        </div>
      </div>
    </div>
  `,
  styles: [`
    :host { display: block; }
  `]
})
export class PartnerManagementComponent implements OnInit {
  private api = inject(AdminApiService);
  
  partners = signal<any[]>([]);
  loading = signal(false);

  ngOnInit() {
    this.loadPartners();
  }

  loadPartners() {
    this.loading.set(true);
    this.api.getPartners().subscribe({
      next: (res) => {
        this.partners.set(res);
        this.loading.set(false);
      },
      error: (err) => {
        console.error('Failed to load partners', err);
        this.loading.set(false);
      }
    });
  }

  deletePartner(id: string) {
    if (confirm('Are you sure you want to delete this partner?')) {
      this.api.deletePartner(id).subscribe(() => this.loadPartners());
    }
  }
}
