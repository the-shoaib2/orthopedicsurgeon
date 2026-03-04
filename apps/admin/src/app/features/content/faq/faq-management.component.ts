import { Component, signal, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { AdminApiService } from '@core/services/admin-api.service';

@Component({
  selector: 'app-faq-management',
  standalone: true,
  imports: [
    CommonModule,
    MatExpansionModule,
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
          <mat-icon color="primary" class="scale-150 ml-2">psychology_alt</mat-icon>
          <div>
            <h1 class="text-2xl font-medium m-0">FAQ Management</h1>
            <p class="text-sm text-slate-500 m-0">Manage frequently asked questions and categories</p>
          </div>
        </div>
        <button mat-flat-button color="primary">
           Add New FAQ
        </button>
      </div>

      <mat-progress-bar *ngIf="loading()" mode="query" color="primary" class="h-1 rounded-full"></mat-progress-bar>

      <div class="space-y-4">
        <mat-accordion multi>
          <mat-expansion-panel *ngFor="let faq of faqs()">
            <mat-expansion-panel-header>
              <mat-panel-title class="flex items-center gap-4">
                <span class="text-xs font-medium px-2 py-1 rounded bg-slate-100 text-slate-600">
                  {{ faq.category }}
                </span>
                <span class="font-medium">{{ faq.question }}</span>
              </mat-panel-title>
              <mat-panel-description class="justify-end items-center gap-4 hidden md:flex">
                 <span [class]="faq.isActive ? 'bg-green-50 text-green-600' : 'bg-slate-50 text-slate-600'" 
                       class="px-2 py-1 rounded text-xs font-medium border">
                   {{ faq.isActive ? 'ACTIVE' : 'INACTIVE' }}
                 </span>
              </mat-panel-description>
            </mat-expansion-panel-header>

            <div class="pt-4 mt-2">
               <div class="flex gap-4 mb-6">
                  <div class="w-10 h-10 rounded-full bg-slate-50 flex items-center justify-center shrink-0">
                    <mat-icon class="text-slate-400">question_answer</mat-icon>
                  </div>
                  <p class="text-sm text-slate-600">{{ faq.answer }}</p>
               </div>
               
               <div class="flex items-center justify-between p-4 rounded bg-slate-50">
                  <div class="flex items-center gap-4">
                     <span class="text-xs text-slate-500">Display Order: {{ faq.displayOrder }}</span>
                  </div>
                  
                  <div class="flex gap-2">
                    <button mat-button color="primary">
                       <mat-icon>edit</mat-icon> Edit
                    </button>
                    <button mat-button color="warn" (click)="deleteFaq(faq.id)">
                       <mat-icon>delete</mat-icon> Delete
                    </button>
                  </div>
               </div>
            </div>
          </mat-expansion-panel>
        </mat-accordion>

        <div *ngIf="faqs().length === 0 && !loading()" class="py-12 text-center text-slate-500">
           <mat-icon class="scale-150 mb-4 text-slate-400">quiz</mat-icon>
           <p class="font-medium text-sm">No FAQs configured</p>
        </div>
      </div>
    </div>
  `,
  styles: [`
    :host { display: block; }
  `]
})
export class FaqManagementComponent implements OnInit {
  private api = inject(AdminApiService);
  
  faqs = signal<any[]>([]);
  loading = signal(false);

  ngOnInit() {
    this.loadFaqs();
  }

  loadFaqs() {
    this.loading.set(true);
    this.api.getFaqs().subscribe({
      next: (res) => {
        this.faqs.set(res);
        this.loading.set(false);
      },
      error: (err) => {
        console.error('Failed to load FAQs', err);
        this.loading.set(false);
      }
    });
  }

  deleteFaq(id: string) {
    if (confirm('Are you sure you want to delete this FAQ?')) {
      this.api.deleteFaq(id).subscribe(() => this.loadFaqs());
    }
  }
}
