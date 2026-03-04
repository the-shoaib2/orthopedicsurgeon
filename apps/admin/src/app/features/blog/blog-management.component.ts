import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatDividerModule } from '@angular/material/divider';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';

@Component({
  selector: 'app-blog-management',
  standalone: true,
  imports: [
    CommonModule, MatButtonModule, MatIconModule,
    MatMenuModule, MatDividerModule, MatFormFieldModule, MatInputModule
  ],
  template: `
    <div class="space-y-6">
      <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 class="text-2xl font-semibold text-slate-900 m-0">Blog</h1>
          <p class="text-sm text-slate-500 mt-1 m-0">Manage blog posts, articles, and news updates.</p>
        </div>
        <button mat-flat-button color="primary">
          <mat-icon class="text-[18px]">add</mat-icon>
          New Post
        </button>
      </div>

      <div class="bg-white rounded-xl border border-slate-200 overflow-hidden">
        <div class="flex flex-col sm:flex-row gap-3 px-6 py-4 border-b border-slate-100">
          <mat-form-field appearance="outline" class="flex-1 sm:max-w-sm" subscriptSizing="dynamic">
            <mat-icon matPrefix class="text-slate-400 text-[18px] mr-2">search</mat-icon>
            <input matInput placeholder="Search posts…" />
          </mat-form-field>
        </div>

        <div class="divide-y divide-slate-100">
          @for (post of posts(); track post.id) {
            <div class="flex items-start gap-4 px-6 py-5 hover:bg-slate-50 transition-colors">
              <div class="w-10 h-10 rounded-lg bg-slate-100 flex items-center justify-center shrink-0 mt-0.5">
                <mat-icon class="text-slate-500 text-[20px]">article</mat-icon>
              </div>
              <div class="flex-1 min-w-0">
                <div class="flex items-start justify-between gap-3">
                  <div>
                    <p class="font-semibold text-sm text-slate-900 m-0">{{ post.title }}</p>
                    <p class="text-xs text-slate-400 m-0 mt-0.5">By {{ post.author }} · {{ post.date }}</p>
                    <p class="text-sm text-slate-500 m-0 mt-1 line-clamp-2">{{ post.excerpt }}</p>
                  </div>
                  <div class="flex items-center gap-2 shrink-0">
                    <span class="text-xs font-semibold px-2.5 py-1 rounded-full"
                          [class]="post.status === 'Published'
                            ? 'bg-green-50 text-green-700 border border-green-200'
                            : 'bg-amber-50 text-amber-700 border border-amber-200'">
                      {{ post.status }}
                    </span>
                    <button mat-icon-button [matMenuTriggerFor]="menu"
                            class="text-slate-400 hover:text-slate-700 hover:bg-slate-100 rounded-lg w-8 h-8">
                      <mat-icon class="text-[18px]">more_vert</mat-icon>
                    </button>
                    <mat-menu #menu="matMenu">
                      <button mat-menu-item><mat-icon>edit</mat-icon> Edit</button>
                      <button mat-menu-item><mat-icon>visibility</mat-icon> Preview</button>
                      <button mat-menu-item><mat-icon>public</mat-icon> Publish</button>
                      <mat-divider></mat-divider>
                      <button mat-menu-item class="text-red-600"><mat-icon class="text-red-500">delete</mat-icon> Delete</button>
                    </mat-menu>
                  </div>
                </div>
              </div>
            </div>
          }
        </div>

        <div class="px-6 py-3 border-t border-slate-100 bg-slate-50/50">
          <span class="text-xs text-slate-400">{{ posts().length }} post(s)</span>
        </div>
      </div>
    </div>
  `,
  styles: [`:host { display: block; }`]
})
export class BlogManagementComponent {
  posts = signal([
    { id: 1, title: 'Understanding Orthopedic Implants', author: 'Dr. Sarah Johnson', date: 'Oct 15, 2024', excerpt: 'A comprehensive guide to modern orthopedic implant technologies and their applications in joint replacement surgery.', status: 'Published' },
    { id: 2, title: 'Post-Surgery Rehabilitation Tips', author: 'Dr. Mike Ross',     date: 'Oct 18, 2024', excerpt: 'Essential exercises and recovery milestones for patients recovering from orthopedic procedures.', status: 'Published' },
    { id: 3, title: 'Bone Health After 50',              author: 'Dr. Lisa Chen',     date: 'Oct 22, 2024', excerpt: 'Lifestyle and nutritional recommendations to maintain strong bones as you age.', status: 'Draft' },
  ]);
}
