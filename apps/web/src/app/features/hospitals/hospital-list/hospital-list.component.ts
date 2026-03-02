import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ZrdCardComponent, ZrdBadgeComponent, ZrdButtonComponent, ZrdPageHeaderComponent } from '@repo/ui';
import { PublicApiService } from '@core/services/public-api.service';
import { HospitalSummary } from '@repo/types';

@Component({
  selector: 'app-hospital-list',
  standalone: true,
  imports: [CommonModule, RouterModule, ZrdCardComponent, ZrdBadgeComponent, ZrdButtonComponent, ZrdPageHeaderComponent],
  template: `
    <zrd-page-header title="Partner Hospitals" subtitle="Explore our network of state-of-the-art medical facilities."></zrd-page-header>

    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
        <zrd-card *ngFor="let hospital of hospitals()" class="flex flex-col h-full hover:shadow-lg transition-shadow">
          <div header class="flex justify-between items-start">
            <div>
              <h3 class="text-xl font-bold text-secondary-900">{{ hospital.name }}</h3>
              <p class="text-secondary-500 text-sm mt-1">{{ hospital.city }}</p>
            </div>
            <zrd-badge variant="success">Active</zrd-badge>
          </div>

          <div class="flex-1 py-4 space-y-4">
            <div class="p-4 bg-secondary-50 rounded-xl border border-secondary-100 flex items-center justify-between">
               <div class="text-xs font-bold uppercase tracking-wider text-secondary-400">Availability</div>
               <div class="text-sm font-bold text-green-600">Open 24/7</div>
            </div>
          </div>

          <div footer>
            <button zrdButton variant="outline" class="w-full" [routerLink]="['/doctors']" [queryParams]="{ hospitalId: hospital.id }">
              View Doctors
            </button>
          </div>
        </zrd-card>
      </div>
    </div>
  `
})
export class HospitalListComponent {
  private api = inject(PublicApiService);
  hospitals = signal<HospitalSummary[]>([]);
  loading = signal(true);

  ngOnInit() {
    this.api.getHospitals().subscribe({
      next: (res) => {
        this.hospitals.set(res.data);
        this.loading.set(false);
      },
      error: () => this.loading.set(false)
    });
  }
}
