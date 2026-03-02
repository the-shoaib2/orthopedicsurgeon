import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-footer',
  standalone: true,
  imports: [CommonModule],
  template: `
    <footer class="bg-foreground text-background py-24 font-sans border-t border-gray-100">
      <div class="max-w-7xl mx-auto px-10">
        <div class="grid grid-cols-1 md:grid-cols-4 gap-20 mb-24">
          <div class="col-span-1 md:col-span-1">
            <div class="flex items-center gap-4 mb-8">
              <div class="w-12 h-12 rounded-2xl bg-primary flex items-center justify-center">
                <span class="text-white font-black text-2xl italic">O</span>
              </div>
              <span class="text-2xl font-black italic uppercase tracking-tighter">OrthoSync</span>
            </div>
            <p class="text-background/40 text-sm leading-relaxed font-bold">
              Defining the standard of orthopedic excellence through precision engineering and compassionate care.
            </p>
          </div>

          <div>
            <h4 class="text-[10px] font-black mb-10 text-primary uppercase tracking-[0.4em]">Clinical Network</h4>
            <ul class="space-y-6 text-sm font-bold text-background/60">
              <li><a href="#" class="hover:text-primary transition-all uppercase tracking-widest">Specialist Search</a></li>
              <li><a href="#" class="hover:text-primary transition-all uppercase tracking-widest">Medical Facilities</a></li>
              <li><a href="#" class="hover:text-primary transition-all uppercase tracking-widest">Telehealth Engine</a></li>
              <li><a href="#" class="hover:text-primary transition-all uppercase tracking-widest">Emergency Care</a></li>
            </ul>
          </div>

          <div>
            <h4 class="text-[10px] font-black mb-10 text-primary uppercase tracking-[0.4em]">Governance</h4>
            <ul class="space-y-6 text-sm font-bold text-background/60">
              <li><a href="#" class="hover:text-primary transition-all uppercase tracking-widest">Patient Rights</a></li>
              <li><a href="#" class="hover:text-primary transition-all uppercase tracking-widest">Cyber Security</a></li>
              <li><a href="#" class="hover:text-primary transition-all uppercase tracking-widest">Ethics Board</a></li>
              <li><a href="#" class="hover:text-primary transition-all uppercase tracking-widest">Institutional Contact</a></li>
            </ul>
          </div>

          <div>
            <h4 class="text-[10px] font-black mb-10 text-primary uppercase tracking-[0.4em]">Transmission</h4>
            <div class="flex flex-col gap-6">
              <input type="email" placeholder="SECURE CHANNEL (EMAIL)" class="bg-background/5 border border-white/10 rounded-xl text-xs font-black px-6 py-4 w-full focus:border-primary/50 outline-none uppercase tracking-widest" />
              <button class="bg-primary hover:bg-primary/90 text-white font-black py-4 rounded-xl transition-all shadow-xl shadow-primary/20 uppercase italic tracking-tighter">
                 Establish Link
              </button>
            </div>
          </div>
        </div>

        <div class="pt-12 border-t border-white/5 flex flex-col md:flex-row justify-between items-center gap-8">
          <p class="text-[10px] font-black text-white/20 uppercase tracking-[0.5em]">© 2026 OrthoSync Intel / Institutional Grade</p>
          <div class="flex gap-8">
             <i class="pi pi-twitter text-white/20 hover:text-primary cursor-pointer transition-all text-xl"></i>
             <i class="pi pi-discord text-white/20 hover:text-primary cursor-pointer transition-all text-xl"></i>
             <i class="pi pi-github text-white/20 hover:text-primary cursor-pointer transition-all text-xl"></i>
          </div>
        </div>
      </div>
    </footer>
  `
})
export class FooterComponent {}
