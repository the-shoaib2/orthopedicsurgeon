import { Component, signal, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TranslateModule } from '@ngx-translate/core';
import { MatTableModule } from '@angular/material/table';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatTooltipModule } from '@angular/material/tooltip';
import { AdminApiService } from '@core/services/admin-api.service';

@Component({
  selector: 'app-blog-management',
  standalone: true,
  imports: [
    CommonModule, 
    TranslateModule,
    MatTableModule, 
    MatCardModule, 
    MatButtonModule, 
    MatIconModule, 
    MatChipsModule,
    MatProgressBarModule,
    MatTooltipModule
  ],
  template: `
    <div class="space-y-6">
      <div class="flex flex-col md:flex-row md:items-center justify-between gap-4 pb-4">
        <div class="flex items-center gap-4">
          <mat-icon color="primary" class="scale-150 ml-2">broadcast_on_home</mat-icon>
          <div>
            <h1 class="text-2xl font-medium m-0">{{ 'BLOG.TITLE' | translate }}</h1>
            <p class="text-sm text-slate-500 m-0">{{ 'BLOG.SUBTITLE' | translate }}</p>
          </div>
        </div>
        <button mat-flat-button color="primary">
           {{ 'BLOG.ADD_BUTTON' | translate }}
        </button>
      </div>

      <mat-card>
        @if (loading()) {
          <mat-progress-bar mode="query" color="primary"></mat-progress-bar>
        }
        
        <div class="overflow-x-auto">
          <table mat-table [dataSource]="posts()" class="w-full">
             <!-- Title Column -->
             <ng-container matColumnDef="title">
                <th mat-header-cell *matHeaderCellDef>{{ 'BLOG.COLUMNS.TITLE' | translate }}</th>
                <td mat-cell *matCellDef="let row">
                  <div class="flex items-center gap-4 py-2">
                    <div class="w-16 h-10 rounded overflow-hidden shrink-0 bg-slate-100 flex items-center justify-center">
                      @if (row.featuredImageUrl) {
                        <img [src]="row.featuredImageUrl" class="w-full h-full object-cover" />
                      } @else {
                        <mat-icon class="text-slate-400">image</mat-icon>
                      }
                    </div>
                    <div class="flex flex-col max-w-md">
                      <span class="font-medium truncate">{{row.title}}</span>
                      <span class="text-xs text-slate-500 truncate">/{{row.slug}}</span>
                     </div>
                  </div>
                </td>
             </ng-container>

             <!-- Category Column -->
             <ng-container matColumnDef="category">
                <th mat-header-cell *matHeaderCellDef>{{ 'BLOG.COLUMNS.CATEGORY' | translate }}</th>
                <td mat-cell *matCellDef="let row">
                  <span class="text-sm">{{row.categoryName || 'General'}}</span>
                </td>
             </ng-container>

             <!-- Stats Column -->
             <ng-container matColumnDef="stats">
                <th mat-header-cell *matHeaderCellDef>{{ 'BLOG.COLUMNS.STATS' | translate }}</th>
                <td mat-cell *matCellDef="let row">
                  <div class="flex items-center gap-4 text-slate-500">
                     <div class="flex items-center gap-1.5">
                       <mat-icon class="text-[18px] w-[18px] h-[18px]">visibility</mat-icon>
                       <span class="text-xs">{{row.viewCount || 0}}</span>
                     </div>
                     <div class="flex items-center gap-1.5">
                       <mat-icon class="text-[18px] w-[18px] h-[18px]">chat_bubble</mat-icon>
                       <span class="text-xs">{{row.commentCount || 0}}</span>
                     </div>
                  </div>
                </td>
             </ng-container>

             <!-- Status Column -->
             <ng-container matColumnDef="status">
                <th mat-header-cell *matHeaderCellDef>{{ 'BLOG.COLUMNS.STATUS' | translate }}</th>
                <td mat-cell *matCellDef="let row">
                  <mat-chip-set>
                    <mat-chip [color]="row.published ? 'primary' : 'accent'">
                      {{ (row.published ? 'BLOG.STATUS.PUBLISHED' : 'BLOG.STATUS.DRAFT') | translate }}
                    </mat-chip>
                  </mat-chip-set>
                </td>
             </ng-container>

             <!-- Actions Column -->
             <ng-container matColumnDef="actions">
                <th mat-header-cell *matHeaderCellDef class="text-right">{{ 'BLOG.COLUMNS.ACTIONS' | translate }}</th>
                <td mat-cell *matCellDef="let row" class="text-right">
                   <div class="flex justify-end gap-1">
                      <button mat-icon-button [matTooltip]="'Edit'" color="primary">
                        <mat-icon>edit_note</mat-icon>
                      </button>
                      <button mat-icon-button (click)="deletePost(row.id)" [matTooltip]="'Delete'" color="warn">
                        <mat-icon>delete</mat-icon>
                      </button>
                   </div>
                </td>
             </ng-container>

             <tr mat-header-row *matHeaderRowDef="displayedColumns" ></tr>
             <tr mat-row *matRowDef="let row; columns: displayedColumns;" class="hover:bg-slate-50 cursor-pointer"></tr>
          </table>

          @if (posts().length === 0 && !loading()) {
            <div class="py-12 text-center text-slate-500">
               <mat-icon class="scale-150 mb-4 text-slate-400">podcasts</mat-icon>
               <p class="font-medium text-sm">{{ 'BLOG.NO_DATA' | translate }}</p>
            </div>
          }
        </div>
      </mat-card>
    </div>
  `,
  styles: [`
    :host { display: block; }
  `]
})
export class BlogManagementComponent implements OnInit {
  private api = inject(AdminApiService);
  
  posts = signal<any[]>([]);
  loading = signal(false);
  
  displayedColumns = ['title', 'category', 'stats', 'status', 'actions'];

  ngOnInit() {
    this.loadPosts();
  }

  loadPosts() {
    this.loading.set(true);
    this.api.getBlogPosts().subscribe({
      next: (res) => {
        this.posts.set(res);
        this.loading.set(false);
      },
      error: (err) => {
        console.error('Failed to load blog posts', err);
        this.loading.set(false);
      }
    });
  }

  deletePost(id: string) {
    if (confirm('Delete this article?')) {
      this.api.deleteBlogPost(id).subscribe(() => this.loadPosts());
    }
  }
}
