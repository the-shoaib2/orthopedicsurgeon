import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatDividerModule } from '@angular/material/divider';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';

@Component({
  selector: 'app-faq-management',
  standalone: true,
  imports: [
    CommonModule, MatButtonModule, MatIconModule,
    MatMenuModule, MatDividerModule, MatFormFieldModule, MatInputModule
  ],
  template: `
    <div class="space-y-6">
      <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 class="text-2xl font-semibold text-slate-900 m-0">FAQ Management</h1>
          <p class="text-sm text-slate-500 mt-1 m-0">Manage frequently asked questions shown on the public website.</p>
        </div>
        <button mat-flat-button color="primary">
          <mat-icon class="text-[18px]">add</mat-icon>
          Add FAQ
        </button>
      </div>

      <div class="bg-white rounded-xl border border-slate-200 overflow-hidden">
        <div class="divide-y divide-slate-100">
          @for (faq of faqs(); track faq.id; let i = $index) {
            <div class="px-6 py-4">
              <div class="flex items-start gap-4">
                <span class="w-7 h-7 rounded-full bg-slate-100 flex items-center justify-center text-xs font-bold text-slate-500 shrink-0 mt-0.5">
                  {{ i + 1 }}
                </span>
                <div class="flex-1 min-w-0">
                  <p class="font-semibold text-sm text-slate-900 m-0">{{ faq.question }}</p>
                  <p class="text-sm text-slate-500 m-0 mt-1">{{ faq.answer }}</p>
                </div>
                <button mat-icon-button [matMenuTriggerFor]="menu"
                        class="text-slate-400 hover:text-slate-700 hover:bg-slate-100 rounded-lg shrink-0">
                  <mat-icon class="text-[18px]">more_vert</mat-icon>
                </button>
                <mat-menu #menu="matMenu">
                  <button mat-menu-item><mat-icon>edit</mat-icon> Edit</button>
                  <button mat-menu-item><mat-icon>swap_vert</mat-icon> Reorder</button>
                  <mat-divider></mat-divider>
                  <button mat-menu-item class="text-red-600"><mat-icon class="text-red-500">delete</mat-icon> Delete</button>
                </mat-menu>
              </div>
            </div>
          }
        </div>
        <div class="px-6 py-3 border-t border-slate-100 bg-slate-50/50">
          <span class="text-xs text-slate-400">{{ faqs().length }} FAQ(s)</span>
        </div>
      </div>
    </div>
  `,
  styles: [`:host { display: block; }`]
})
export class FaqManagementComponent {
  faqs = signal([
    { id: 1, question: 'What types of orthopedic conditions do you treat?', answer: 'We treat a wide range of conditions including joint pain, fractures, sports injuries, spine disorders, and degenerative bone diseases.' },
    { id: 2, question: 'How do I book an appointment?', answer: 'You can book an appointment online through our portal, by calling our reception desk, or visiting any of our hospital locations.' },
    { id: 3, question: 'What should I bring to my first consultation?', answer: 'Please bring a valid ID, any previous medical records or imaging results, and a list of current medications.' },
    { id: 4, question: 'Do you accept insurance?', answer: 'Yes, we accept most major insurance providers. Please contact us to verify your specific coverage before your appointment.' },
  ]);
}
