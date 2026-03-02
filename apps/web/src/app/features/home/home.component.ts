import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ZrdButtonComponent, ZrdCardComponent, ZrdStatComponent, ZrdBadgeComponent } from '@repo/ui';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterModule, ZrdButtonComponent, ZrdCardComponent, ZrdStatComponent, ZrdBadgeComponent],
  template: `
    <div class="flex flex-col">
      <!-- Hero Section -->
      <section class="relative bg-white pt-20 pb-32 overflow-hidden">
        <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div class="grid grid-cols-1 lg:grid-cols-2 gap-12 items-center">
            <div class="animate-in fade-in slide-in-from-left duration-700">
               <zrd-badge variant="info" class="mb-6">Orthopedic Excellence</zrd-badge>
               <h1 class="text-5xl md:text-6xl font-black text-secondary-900 leading-tight mb-6">
                 Modern Care for Your <span class="text-primary-600">Bones & Joints</span>
               </h1>
               <p class="text-lg text-secondary-600 mb-8 max-w-lg leading-relaxed">
                 Connect with top orthopedic surgeons, book appointments online, and manage your recovery plan with our state-of-the-art platform.
               </p>
               <div class="flex flex-wrap gap-4">
                 <button zrdButton size="lg" routerLink="/doctors">Find a Specialist</button>
                 <button zrdButton variant="outline" size="lg">How it Works</button>
               </div>

               <div class="mt-12 flex items-center gap-8">
                 <div class="flex -space-x-3">
                    <img *ngFor="let i of [1,2,3,4]" class="w-10 h-10 rounded-full border-2 border-white" [src]="'https://i.pravatar.cc/100?img=' + i" />
                 </div>
                 <p class="text-sm font-medium text-secondary-500">
                   <span class="text-secondary-900 font-bold">10,000+</span> Successful surgeries
                 </p>
               </div>
            </div>

            <div class="relative animate-in fade-in slide-in-from-right duration-700">
               <div class="relative z-10 rounded-3xl overflow-hidden shadow-2xl border-8 border-white">
                  <img src="https://images.unsplash.com/photo-1579684385127-1ef15d508118?auto=format&fit=crop&q=80&w=800" alt="Surgeon" />
               </div>
               <!-- Floating UI Card -->
               <div class="absolute -bottom-10 -left-10 z-20 w-64">
                 <zrd-card class="shadow-2xl">
                    <div class="flex items-center gap-3">
                       <div class="p-2 bg-green-100 text-green-600 rounded-lg">
                         <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"/></svg>
                       </div>
                       <div>
                         <p class="text-[10px] text-secondary-400 font-bold uppercase">Next Available Slot</p>
                         <p class="text-sm font-bold text-secondary-900">Today, 4:30 PM</p>
                       </div>
                    </div>
                 </zrd-card>
               </div>
            </div>
          </div>
        </div>
      </section>

      <!-- Stats -->
      <section class="bg-secondary-50 py-20">
         <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
            <div class="grid grid-cols-1 md:grid-cols-4 gap-8">
               <zrd-stat label="Experience Surgeons" value="150+" description="Top-rated specialists" [icon]="true">
                  <span icon class="pi pi-users text-lg"></span>
               </zrd-stat>
               <zrd-stat label="Modern Hospitals" value="25" description="Across 10 cities" [icon]="true">
                  <span icon class="pi pi-building text-lg"></span>
               </zrd-stat>
               <zrd-stat label="Patient Rating" value="4.9/5" description="Based on 5k reviews" [icon]="true">
                  <span icon class="pi pi-star-fill text-lg"></span>
               </zrd-stat>
               <zrd-stat label="Success Rate" value="98%" description="In complex surgeries" [icon]="true">
                  <span icon class="pi pi-check-circle text-lg"></span>
               </zrd-stat>
            </div>
         </div>
      </section>
    </div>
  `
})
export class HomeComponent {}
