import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-auth-layout',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <div class="min-h-screen flex bg-white">
      <!-- Left: Form -->
      <div class="flex-1 flex flex-col justify-center py-12 px-4 sm:px-6 lg:flex-none lg:px-20 xl:px-24">
        <div class="mx-auto w-full max-w-sm lg:w-96">
           <div class="flex items-center gap-2 mb-10 cursor-pointer" routerLink="/">
              <div class="w-10 h-10 rounded-xl bg-primary-600 flex items-center justify-center">
                <span class="text-white font-bold text-xl">O</span>
              </div>
              <span class="text-xl font-bold text-secondary-900 tracking-tight">ORTHO<span class="text-primary-600">SYNC</span></span>
           </div>
           
           <div class="animate-in fade-in slide-in-from-bottom-4 duration-500">
             <router-outlet></router-outlet>
           </div>

           <div class="mt-10 border-t border-secondary-100 pt-6">
              <p class="text-xs text-secondary-400 text-center">
                © 2024 OrthoSync Platform. Secure & Verified Medical Services.
              </p>
           </div>
        </div>
      </div>

      <!-- Right: Image/Marketing -->
      <div class="hidden lg:block relative w-0 flex-1">
        <img class="absolute inset-0 h-full w-full object-cover" src="https://images.unsplash.com/photo-1519494026892-80bbd2d6fd0d?auto=format&fit=crop&q=80&w=1200" alt="Medical Background">
        <div class="absolute inset-0 bg-primary-900/60 mix-blend-multiply flex items-center justify-center p-20">
           <div class="text-white max-w-md">
              <h2 class="text-4xl font-black mb-6 leading-tight">Better Infrastructure for Better Medical Care.</h2>
              <p class="text-lg text-primary-100 leading-relaxed">Join thousands of patients who trust OrthoSync for their orthopedic health. Seamless booking, digital history, and expert care.</p>
           </div>
        </div>
      </div>
    </div>
  `
})
export class AuthLayoutComponent {}
