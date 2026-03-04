import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-auth-layout',
  standalone: true,
  imports: [CommonModule, RouterModule, MatIconModule],
  template: `
    <div class="h-screen w-full flex overflow-hidden bg-white font-sans">
      <!-- Left Side: Visual Content (Desktop only) -->
      <div class="hidden lg:block relative flex-1 h-full overflow-hidden bg-slate-900">
         <img src="https://images.unsplash.com/photo-1579684385127-1ef15d508118?auto=format&fit=crop&q=80&w=1000" class="absolute inset-0 w-full h-full object-cover opacity-60 scale-105" alt="Background" />
         <div class="absolute inset-0 bg-gradient-to-l from-white via-transparent to-transparent"></div>
         
         <!-- Decorative Overlay -->
         <div class="absolute bottom-20 left-20 right-20 text-white z-20">
            <div class="backdrop-blur-xl bg-white/10 p-10 rounded-[40px] border border-white/20 shadow-2xl">
               <div class="flex items-center gap-4 mb-4">
                  <div class="w-12 h-12 rounded-2xl bg-white/20 flex items-center justify-center backdrop-blur-md">
                     <mat-icon class="text-white">medical_services</mat-icon>
                  </div>
                  <h2 class="text-3xl font-bold tracking-tight text-white">Clinical Precision</h2>
               </div>
               <p class="text-lg text-white/80 leading-relaxed max-w-lg mb-0 font-medium italic">
                 "Orchestrating surgical excellence with synchronized digital patient care."
               </p>
            </div>
         </div>
      </div>

      <!-- Right Side: Form Container -->
      <div class="w-full lg:w-[450px] xl:w-[550px] h-full flex flex-col justify-center z-10 bg-white shadow-2xl lg:shadow-none">
        <div class="px-8 sm:px-16 lg:px-20 overflow-y-auto py-12">
            <router-outlet></router-outlet>
            
            <div class="mt-16 text-slate-400 text-[10px] flex items-center justify-between border-t border-slate-50 pt-8 tracking-widest font-bold">
               <span>OrthoSync &copy; 2026</span>
               <div class="flex gap-4">
                  <a href="#" class="hover:text-primary-600 transition-colors text-slate-400">Help</a>
                  <a href="#" class="hover:text-primary-600 transition-colors text-slate-400">Privacy</a>
               </div>
            </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    :host { display: block; }
    /* Ensure responsiveness for smaller screens */
    @media (max-width: 1023px) {
      .h-screen {
        height: auto;
        min-height: 100vh;
        overflow-y: auto;
      }
    }
  `]
})
export class AuthLayoutComponent {}
