import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { NavbarComponent } from '@core/components/navbar/navbar.component';
import { FooterComponent } from '@core/components/footer/footer.component';

@Component({
  selector: 'app-main-layout',
  standalone: true,
  imports: [
    CommonModule, 
    RouterModule, 
    NavbarComponent, 
    FooterComponent
  ],
  template: `
    <div class="min-h-screen flex flex-col bg-gray-50/30">
      <app-navbar></app-navbar>
      
      <main class="flex-1 pt-[136px]">
        <router-outlet></router-outlet>
      </main>

      <app-footer></app-footer>
    </div>
  `,
  styles: [`
    :host { display: block; }
    main { width: 100%; }
  `]
})
export class MainLayoutComponent {
}
