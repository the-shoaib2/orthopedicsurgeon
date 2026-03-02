import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ZrdCardComponent, ZrdButtonComponent, ZrdSearchInputComponent, ZrdAvatarComponent } from '@repo/ui';
import { PublicApiService } from '@core/services/public-api.service';
import { Doctor } from '@repo/types';

@Component({
  selector: 'app-doctor-list',
  standalone: true,
  imports: [CommonModule, RouterModule, ZrdCardComponent, ZrdButtonComponent, ZrdSearchInputComponent, ZrdAvatarComponent],
  template: `
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
      <div class="flex flex-col md:flex-row md:items-center justify-between gap-6 mb-12">
        <div>
          <h1 class="text-3xl font-bold text-secondary-900">Find a Specialist</h1>
          <p class="text-secondary-500 mt-1">Browse our network of expert orthopedic surgeons.</p>
        </div>
        <zrd-search-input 
          placeholder="Search by name or specialization..." 
          (search)="onSearch($event)"
          [loading]="loading()"
        ></zrd-search-input>
      </div>

      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
        <ng-container *ngIf="!loading(); else skeleton">
          <zrd-card *ngFor="let doctor of doctors()" class="hover:shadow-xl hover:translate-y-[-4px] transition-all">
            <div class="flex items-start gap-4 mb-6">
              <zrd-avatar [name]="doctor.user.firstName + ' ' + doctor.user.lastName" size="lg" status="online"></zrd-avatar>
              <div>
                <h3 class="text-lg font-bold text-secondary-900">{{ doctor.user.firstName }} {{ doctor.user.lastName }}</h3>
                <p class="text-sm text-primary-600 font-medium">{{ doctor.specialization }}</p>
                <div class="flex items-center gap-1 mt-1">
                   <i class="pi pi-star-fill text-amber-400 text-xs"></i>
                   <span class="text-xs font-bold text-secondary-900">{{ doctor.rating }}</span>
                   <span class="text-[10px] text-secondary-400">(450 reviews)</span>
                </div>
              </div>
            </div>

            <div class="space-y-3 mb-6">
               <div class="flex items-center gap-2 text-sm text-secondary-600">
                 <i class="pi pi-building text-secondary-400"></i>
                 <span>{{ doctor.hospital.name }}</span>
               </div>
               <div class="flex items-center gap-2 text-sm text-secondary-600">
                 <i class="pi pi-wallet text-secondary-400"></i>
                 <span>Consultation Fee: <span class="font-bold text-secondary-900">$\{{ doctor.consultationFee }}</span></span>
               </div>
            </div>

            <div class="flex gap-3">
              <button zrdButton variant="outline" class="flex-1" [routerLink]="['/doctors', doctor.id]">View Profile</button>
              <button zrdButton class="flex-1" [routerLink]="['/doctors', doctor.id]" [queryParams]="{ book: true }">Book Now</button>
            </div>
          </zrd-card>
        </ng-container>

        <ng-template #skeleton>
          <div *ngFor="let i of [1,2,3,4,5,6]" class="bg-white rounded-xl h-[280px] p-6 border border-secondary-100 space-y-4">
             <div class="flex gap-4">
                <div class="w-16 h-16 rounded-full bg-secondary-100 animate-pulse"></div>
                <div class="space-y-2 flex-1">
                   <div class="h-4 bg-secondary-100 rounded animate-pulse w-3/4"></div>
                   <div class="h-3 bg-secondary-100 rounded animate-pulse w-1/2"></div>
                </div>
             </div>
             <div class="h-4 bg-secondary-100 rounded animate-pulse w-full"></div>
             <div class="h-4 bg-secondary-100 rounded animate-pulse w-5/6"></div>
             <div class="flex gap-3 mt-6">
                <div class="h-10 bg-secondary-100 rounded-lg animate-pulse flex-1"></div>
                <div class="h-10 bg-secondary-100 rounded-lg animate-pulse flex-1"></div>
             </div>
          </div>
        </ng-template>
      </div>
    </div>
  `
})
export class DoctorListComponent {
  private api = inject(PublicApiService);
  
  doctors = signal<Doctor[]>([]);
  loading = signal(true);

  ngOnInit() {
    this.loadDoctors();
  }

  loadDoctors(search?: string) {
    this.loading.set(true);
    this.api.getDoctors({ search }).subscribe({
      next: (res) => {
        this.doctors.set(res.data.content);
        this.loading.set(false);
      },
      error: () => this.loading.set(false)
    });
  }

  onSearch(term: string) {
    this.loadDoctors(term);
  }
}
