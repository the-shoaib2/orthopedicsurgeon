import { Component, signal, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MatDialogModule } from '@angular/material/dialog';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { AdminApiService } from '@core/services/admin-api.service';

@Component({
  selector: 'app-hero-management',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatChipsModule,
    MatDialogModule,
    MatTooltipModule,
    MatProgressBarModule
  ],
  template: `
    <div class="space-y-6">
      <div class="flex flex-col lg:flex-row lg:items-center justify-between gap-4 pb-4">
        <div class="flex items-center gap-4">
          <mat-icon color="primary" class="scale-150 ml-2">view_carousel</mat-icon>
          <div>
            <h1 class="text-2xl font-medium m-0">Hero Slide Configuration</h1>
            <p class="text-sm text-slate-500 m-0">Manage landing page slides and promotional banners</p>
          </div>
        </div>
        <button mat-flat-button color="primary">
           Add New Slide
        </button>
      </div>

      <mat-progress-bar *ngIf="loading()" mode="query" color="primary" class="h-1 rounded-full"></mat-progress-bar>

      <div class="grid grid-cols-1 xl:grid-cols-2 gap-6">
        <mat-card *ngFor="let slide of slides()" class="overflow-hidden">
           <div class="relative h-64 overflow-hidden bg-slate-100">
              <img [src]="slide.imageUrl" class="w-full h-full object-cover" />
              <div class="absolute inset-0 bg-gradient-to-t from-black/60 to-transparent"></div>
              
              <div class="absolute top-4 left-4 flex gap-2">
                 <span class="px-2 py-1 bg-slate-50/90 text-slate-800 rounded text-xs font-medium">Order: {{ slide.displayOrder }}</span>
                 <span [class]="slide.isActive ? 'bg-green-500 text-white' : 'bg-slate-500 text-white'" 
                       class="px-2 py-1 rounded text-xs font-medium">
                    {{ slide.isActive ? 'ACTIVE' : 'INACTIVE' }}
                 </span>
              </div>

              <div class="absolute top-4 right-4 flex gap-2">
                 <button mat-icon-button class="bg-white/90">
                   <mat-icon>edit</mat-icon>
                 </button>
                 <button mat-icon-button color="warn" class="bg-white/90" (click)="deleteSlide(slide.id)">
                   <mat-icon>delete</mat-icon>
                 </button>
              </div>

              <div class="absolute bottom-4 left-4 right-4">
                 <h3 class="text-2xl font-medium text-white mb-1">{{ slide.title }}</h3>
                 <p class="text-sm text-white/90 truncate">{{ slide.subtitle }}</p>
              </div>
           </div>
           
           <mat-card-content class="pt-4">
              <p class="text-sm text-slate-600 mb-4">{{ slide.description }}</p>
              
              <div class="flex items-center justify-between">
                 <div class="flex items-center gap-3">
                    <mat-icon class="text-slate-400">ads_click</mat-icon>
                    <div class="flex flex-col">
                       <span class="text-xs text-slate-500">Call to action</span>
                       <span class="text-sm font-medium">{{ slide.buttonText }}</span>
                    </div>
                 </div>
              </div>
           </mat-card-content>
        </mat-card>

        <div *ngIf="slides().length === 0 && !loading()" class="xl:col-span-2 py-12 text-center text-slate-500">
           <mat-icon class="scale-150 mb-4 text-slate-400">view_carousel</mat-icon>
           <p class="font-medium text-sm">No slides currently configured</p>
        </div>
      </div>
    </div>
  `,
  styles: [`
    :host { display: block; }
  `]
})
export class HeroManagementComponent implements OnInit {
  private api = inject(AdminApiService);
  
  slides = signal<any[]>([]);
  loading = signal(false);

  ngOnInit() {
    this.loadSlides();
  }

  loadSlides() {
    this.loading.set(true);
    this.api.getHeroSlides().subscribe({
      next: (res) => {
        this.slides.set(res);
        this.loading.set(false);
      },
      error: (err) => {
        console.error('Failed to load hero slides', err);
        this.loading.set(false);
      }
    });
  }

  deleteSlide(id: string) {
    if (confirm('Are you sure you want to delete this slide?')) {
      this.api.deleteHeroSlide(id).subscribe(() => this.loadSlides());
    }
  }
}
