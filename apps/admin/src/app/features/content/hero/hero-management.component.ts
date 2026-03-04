import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatDividerModule } from '@angular/material/divider';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';

@Component({
  selector: 'app-hero-management',
  standalone: true,
  imports: [
    CommonModule, ReactiveFormsModule, MatButtonModule, MatIconModule,
    MatMenuModule, MatDividerModule, MatFormFieldModule, MatInputModule
  ],
  template: `
    <div class="space-y-6">
      <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 class="text-2xl font-semibold text-slate-900 m-0">Hero Section</h1>
          <p class="text-sm text-slate-500 mt-1 m-0">Manage the homepage hero banner and call-to-action content.</p>
        </div>
        <button mat-flat-button color="primary" (click)="editing.set(true)">
          <mat-icon class="text-[18px]">edit</mat-icon>
          Edit Hero
        </button>
      </div>

      <!-- Current Hero Preview -->
      @if (!editing()) {
        <div class="bg-white rounded-xl border border-slate-200 overflow-hidden">
          <div class="bg-slate-800 p-8 text-white">
            <div class="max-w-lg">
              <span class="text-xs font-semibold uppercase tracking-widest text-slate-300 bg-white/10 px-3 py-1 rounded-full">
                {{ hero().badge }}
              </span>
              <h2 class="text-3xl font-bold mt-4 mb-3">{{ hero().headline }}</h2>
              <p class="text-slate-300 mb-6">{{ hero().subheadline }}</p>
              <button mat-flat-button class="bg-white text-slate-900 font-semibold">
                {{ hero().ctaText }}
              </button>
            </div>
          </div>
          <div class="px-6 py-4 border-t border-slate-100 flex items-center justify-between">
            <span class="text-xs text-slate-400">Last updated: Oct 20, 2024</span>
            <button mat-button color="primary" (click)="editing.set(true)">Edit</button>
          </div>
        </div>
      } @else {
        <!-- Edit Form -->
        <div class="bg-white rounded-xl border border-slate-200 p-6">
          <h2 class="text-base font-semibold text-slate-800 mb-5">Edit Hero Content</h2>
          <form [formGroup]="heroForm" class="space-y-4">
            <mat-form-field appearance="outline" class="w-full" subscriptSizing="dynamic">
              <mat-label>Badge Text</mat-label>
              <input matInput formControlName="badge" />
            </mat-form-field>
            <mat-form-field appearance="outline" class="w-full" subscriptSizing="dynamic">
              <mat-label>Headline</mat-label>
              <input matInput formControlName="headline" />
            </mat-form-field>
            <mat-form-field appearance="outline" class="w-full" subscriptSizing="dynamic">
              <mat-label>Sub-headline</mat-label>
              <textarea matInput formControlName="subheadline" rows="3"></textarea>
            </mat-form-field>
            <mat-form-field appearance="outline" class="w-full" subscriptSizing="dynamic">
              <mat-label>CTA Button Text</mat-label>
              <input matInput formControlName="ctaText" />
            </mat-form-field>
            <div class="flex gap-3 pt-2">
              <button mat-flat-button color="primary" (click)="saveHero()">Save Changes</button>
              <button mat-stroked-button (click)="editing.set(false)">Cancel</button>
            </div>
          </form>
        </div>
      }
    </div>
  `,
  styles: [`:host { display: block; }`]
})
export class HeroManagementComponent {
  editing = signal(false);
  private fb = new FormBuilder();

  hero = signal({
    badge: 'Expert Orthopedic Care',
    headline: 'Advanced Bone & Joint Solutions',
    subheadline: 'Access world-class orthopedic specialists, book appointments online, and manage your complete bone and joint health journey.',
    ctaText: 'Book Consultation'
  });

  heroForm = this.fb.group({
    badge:        [this.hero().badge,        Validators.required],
    headline:     [this.hero().headline,     Validators.required],
    subheadline:  [this.hero().subheadline,  Validators.required],
    ctaText:      [this.hero().ctaText,      Validators.required],
  });

  saveHero() {
    if (this.heroForm.valid) {
      this.hero.set(this.heroForm.value as any);
      this.editing.set(false);
    }
  }
}
