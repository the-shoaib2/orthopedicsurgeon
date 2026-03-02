import { ChangeDetectionStrategy, Component, ContentChild, EventEmitter, Input, Output, TemplateRef, booleanAttribute } from '@angular/core';
import { CommonModule } from '@angular/common';

export interface ZrdColumnDef<T> {
  key: keyof T | string;
  header: string;
  sortable?: boolean;
  align?: 'left' | 'center' | 'right';
  width?: string;
  cellTemplate?: TemplateRef<any>;
}

@Component({
  selector: 'zrd-table',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="relative overflow-x-auto border border-secondary-200 rounded-xl bg-white">
      <table class="w-full text-sm text-left text-secondary-500">
        <thead class="text-xs text-secondary-700 uppercase bg-secondary-50/50 border-b border-secondary-200">
          <tr>
            <th *ngFor="let col of columns" 
                scope="col" 
                class="px-6 py-4 font-semibold"
                [style.width]="col.width"
                [class.text-center]="col.align === 'center'"
                [class.text-right]="col.align === 'right'"
                [class.cursor-pointer]="col.sortable"
                (click)="toggleSort(col)"
            >
              <div class="flex items-center gap-2" [class.justify-center]="col.align === 'center'" [class.justify-end]="col.align === 'right'">
                {{ col.header }}
                <span *ngIf="col.sortable" class="text-secondary-400">
                  <svg *ngIf="sortKey !== col.key" class="w-3 h-3" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 16V4m0 0L3 8m4-4l4 4m6 0v12m0 0l4-4m-4 4l-4-4" />
                  </svg>
                  <svg *ngIf="sortKey === col.key && sortDirection === 'ASC'" class="w-3 h-3 text-primary-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 15l7-7 7 7" />
                  </svg>
                  <svg *ngIf="sortKey === col.key && sortDirection === 'DESC'" class="w-3 h-3 text-primary-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7" />
                  </svg>
                </span>
              </div>
            </th>
          </tr>
        </thead>
        <tbody>
          <!-- Loading State -->
          <ng-container *ngIf="loading">
            <tr *ngFor="let i of [1,2,3,4,5]" class="border-b border-secondary-50 last:border-0">
              <td *ngFor="let col of columns" class="px-6 py-4">
                <div class="h-4 bg-secondary-100 rounded animate-pulse w-full"></div>
              </td>
            </tr>
          </ng-container>

          <!-- Empty State -->
          <ng-container *ngIf="!loading && data.length === 0">
            <tr>
              <td [attr.colspan]="columns.length" class="px-6 py-12 text-center text-secondary-400">
                <div class="flex flex-col items-center gap-2">
                  <svg class="w-12 h-12 text-secondary-200" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M20 13V6a2 2 0 00-2-2H6a2 2 0 00-2 2v7m16 0v5a2 2 0 01-2 2H6a2 2 0 01-2-2v-5m16 0h-2.586a1 1 0 00-.707.293l-2.414 2.414a1 1 0 01-.707.293h-3.172a1 1 0 01-.707-.293l-2.414-2.414a1 1 0 00-.707-.293H4" />
                  </svg>
                  <span class="text-sm">No data found</span>
                </div>
              </td>
            </tr>
          </ng-container>

          <!-- Data -->
          <ng-container *ngIf="!loading">
            <tr *ngFor="let item of data" class="border-b border-secondary-100 last:border-0 hover:bg-secondary-50/50 transition-colors">
              <td *ngFor="let col of columns" class="px-6 py-4 text-secondary-800"
                  [class.text-center]="col.align === 'center'"
                  [class.text-right]="col.align === 'right'"
              >
                <ng-container *ngIf="col.cellTemplate; else defaultValue">
                  <ng-container *ngTemplateOutlet="col.cellTemplate; context: { $implicit: getCellValue(item, col.key), row: item }"></ng-container>
                </ng-container>
                <ng-template #defaultValue>
                  {{ getCellValue(item, col.key) }}
                </ng-template>
              </td>
            </tr>
          </ng-container>
        </tbody>
      </table>
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ZrdTableComponent<T> {
  @Input() columns: ZrdColumnDef<T>[] = [];
  @Input() data: T[] = [];
  @Input({ transform: booleanAttribute }) loading = false;
  
  @Input() sortKey?: string;
  @Input() sortDirection?: 'ASC' | 'DESC';
  
  @Output() sortChange = new EventEmitter<{ key: string, direction: 'ASC' | 'DESC' }>();

  toggleSort(col: ZrdColumnDef<T>) {
    if (!col.sortable) return;
    
    let direction: 'ASC' | 'DESC' = 'ASC';
    if (this.sortKey === col.key) {
      direction = this.sortDirection === 'ASC' ? 'DESC' : 'ASC';
    }
    
    this.sortChange.emit({ key: col.key as string, direction });
  }

  getCellValue(item: any, key: any): any {
    return item[key];
  }
}
