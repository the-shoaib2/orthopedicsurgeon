import { Component, inject, signal, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { ZrdCardComponent, ZrdButtonComponent, ZrdBadgeComponent, ZrdAvatarComponent, ZrdTabsComponent, ZrdTab } from '@repo/ui';
import { PublicApiService } from '@core/services/public-api.service';
import { Doctor } from '@repo/types';

@Component({
  selector: 'app-doctor-detail',
  standalone: true,
  imports: [CommonModule, RouterModule, ZrdCardComponent, ZrdButtonComponent, ZrdBadgeComponent, ZrdAvatarComponent, ZrdTabsComponent],
  template: `
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
      <ng-container *ngIf="!loading(); else skeleton">
        <div *ngIf="doctor() as d" class="grid grid-cols-1 lg:grid-cols-3 gap-8">
          
          <!-- Left: Profile Info -->
          <div class="lg:col-span-2 space-y-8">
            <zrd-card>
               <div class="flex flex-col md:flex-row gap-8 items-start">
                  <zrd-avatar [name]="d.user.firstName + ' ' + d.user.lastName" size="xl" status="online"></zrd-avatar>
                  <div class="flex-1">
                     <div class="flex items-center justify-between mb-2">
                        <h1 class="text-3xl font-bold text-secondary-900">{{ d.user.firstName }} {{ d.user.lastName }}</h1>
                        <zrd-badge variant="info">Verified Specialist</zrd-badge>
                     </div>
                     <p class="text-lg text-primary-600 font-medium mb-4">{{ d.specialization }}</p>
                     
                     <div class="flex flex-wrap gap-6 text-sm text-secondary-500">
                        <div class="flex items-center gap-2">
                           <i class="pi pi-briefcase text-secondary-400"></i>
                           <span>{{ d.experienceYears }} Years Experience</span>
                        </div>
                        <div class="flex items-center gap-2">
                           <i class="pi pi-language text-secondary-400"></i>
                           <span>English, Bengali</span>
                        </div>
                        <div class="flex items-center gap-2">
                           <i class="pi pi-star-fill text-amber-400"></i>
                           <span class="text-secondary-900 font-bold">{{ d.rating }}</span>
                           <span>(450 Reviews)</span>
                        </div>
                     </div>
                  </div>
               </div>
            </zrd-card>

            <zrd-tabs [tabs]="detailTabs" [activeTabId]="activeTab()" (tabChange)="activeTab.set($any($event))">
               <div *ngIf="activeTab() === 'about'" class="space-y-6 animate-in fade-in duration-300">
                  <div>
                    <h3 class="text-lg font-bold text-secondary-900 mb-3">Professional Biography</h3>
                    <p class="text-secondary-600 leading-relaxed">{{ d.bio || 'No biography provided.' }}</p>
                  </div>
                  <div>
                    <h3 class="text-lg font-bold text-secondary-900 mb-3">Specializations</h3>
                    <div class="flex flex-wrap gap-2">
                       <zrd-badge *ngFor="let s of d.specialization.split(',')" variant="outline">{{ s.trim() }}</zrd-badge>
                    </div>
                  </div>
               </div>

               <div *ngIf="activeTab() === 'reviews'" class="space-y-6 animate-in fade-in duration-300">
                  <p class="text-secondary-500 italic text-center py-10">Patient reviews will be implemented in the next update.</p>
               </div>
            </zrd-tabs>
          </div>

          <!-- Right: Booking Sidebar -->
          <div class="space-y-6">
             <zrd-card class="sticky top-24">
                <div header class="flex items-center justify-between">
                   <span class="font-bold text-secondary-900">Consultation Fee</span>
                   <span class="text-2xl font-black text-primary-600">$\{{ d.consultationFee }}</span>
                </div>
                
                <div class="space-y-4">
                   <div class="p-4 bg-secondary-50 rounded-xl border border-secondary-100 italic text-xs text-secondary-500">
                     Includes 15 minutes of direct consultation and prescription.
                   </div>
                   
                   <button zrdButton size="lg" class="w-full shadow-lg shadow-primary-500/20" [routerLink]="['/doctors', d.id, 'book']">
                      Book Appointment
                   </button>
                   
                   <div class="flex flex-col gap-3 pt-4 border-t border-secondary-100">
                      <div class="flex justify-between text-sm">
                        <span class="text-secondary-500">Wait Time</span>
                        <span class="font-medium text-secondary-900">~15 mins</span>
                      </div>
                      <div class="flex justify-between text-sm">
                        <span class="text-secondary-500">Next Slot</span>
                        <span class="font-medium text-green-600">Available Today</span>
                      </div>
                   </div>
                </div>
             </zrd-card>

             <zrd-card>
                <h4 class="font-bold text-secondary-900 mb-4">Location</h4>
                <div class="flex items-start gap-3">
                   <i class="pi pi-map-marker text-primary-600 mt-1"></i>
                   <div>
                      <p class="font-bold text-sm text-secondary-900">{{ d.hospital.name }}</p>
                      <p class="text-xs text-secondary-500 mt-1">{{ d.hospital.city }}</p>
                   </div>
                </div>
             </zrd-card>
          </div>

        </div>
      </ng-container>

      <ng-template #skeleton>
         <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">
            <div class="lg:col-span-2 space-y-8">
               <div class="bg-white rounded-xl h-48 animate-pulse border border-secondary-100"></div>
               <div class="bg-white rounded-xl h-64 animate-pulse border border-secondary-100"></div>
            </div>
            <div class="space-y-6">
               <div class="bg-white rounded-xl h-64 animate-pulse border border-secondary-100"></div>
               <div class="bg-white rounded-xl h-32 animate-pulse border border-secondary-100"></div>
            </div>
         </div>
      </ng-template>
    </div>
  `
})
export class DoctorDetailComponent {
  private route = inject(ActivatedRoute);
  private api = inject(PublicApiService);

  doctor = signal<Doctor | null>(null);
  loading = signal(true);
  activeTab = signal('about');

  detailTabs: ZrdTab[] = [
    { id: 'about', label: 'About Doctor' },
    { id: 'reviews', label: 'Patient Reviews' }
  ];

  ngOnInit() {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.api.getDoctorById(id).subscribe({
        next: (res) => {
          this.doctor.set(res.data);
          this.loading.set(false);
        },
        error: () => this.loading.set(false)
      });
    }
  }
}
