import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatDividerModule } from '@angular/material/divider';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatChipsModule } from '@angular/material/chips';
import { HealthService } from '@core/services/health.service';
import { PatientDashboard, PatientTimeline, VitalSigns } from '@repo/types';
import { RecordVitalsDialogComponent } from '../components/record-vitals-dialog/record-vitals-dialog.component';
 
@Component({
  selector: 'app-patient-health-detail',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatButtonModule,
    MatIconModule,
    MatCardModule,
    MatDividerModule,
    MatDialogModule,
    MatChipsModule
  ],
  template: `
    <div class="space-y-6">
      <!-- Breadcrumbs & Header -->
      <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <nav class="flex items-center gap-2 mb-2 text-sm text-slate-400">
            <a routerLink="/patients" class="hover:text-blue-600 transition-colors">Patients</a>
            <mat-icon class="text-[14px] w-auto h-auto">chevron_right</mat-icon>
            <span class="text-slate-600 font-medium">Clinical Records</span>
          </nav>
          <h1 class="text-2xl font-bold text-slate-900 m-0">Patient Clinical Health</h1>
          <p class="text-sm text-slate-500 mt-1 m-0">Consolidated medical history and vitals for patient ID: {{ patientId() }}</p>
        </div>
        <button mat-flat-button color="primary" (click)="openRecordVitals()">
          <mat-icon class="mr-2">monitor_heart</mat-icon>
          Record Vitals
        </button>
      </div>
 
      <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
        
        <!-- Left: Dashboard Stats & Latest Vitals -->
        <div class="lg:col-span-1 space-y-6">
          <mat-card class="border border-slate-200 shadow-none rounded-xl overflow-hidden">
            <div class="bg-slate-50 px-6 py-4 border-b border-slate-100 flex items-center justify-between">
              <span class="font-semibold text-slate-700">Latest Vital Signs</span>
              <mat-icon class="text-slate-400">analytics</mat-icon>
            </div>
            <mat-card-content class="p-6">
              @if (dashboard()?.latestVitals; as vitals) {
                <div class="space-y-4">
                  <div class="flex items-center justify-between">
                    <span class="text-slate-500 text-sm">Blood Pressure</span>
                    <span class="font-bold text-slate-900">{{ vitals.systolic }}/{{ vitals.diastolic }} mmHg</span>
                  </div>
                  <div class="flex items-center justify-between">
                    <span class="text-slate-500 text-sm">Heart Rate</span>
                    <span class="font-bold text-slate-900">{{ vitals.heartRate }} bpm</span>
                  </div>
                  <div class="flex items-center justify-between">
                    <span class="text-slate-500 text-sm">Temperature</span>
                    <span class="font-bold text-slate-900">{{ vitals.temperature }} °C</span>
                  </div>
                  <div class="flex items-center justify-between">
                    <span class="text-slate-500 text-sm">SpO2</span>
                    <span class="font-bold text-blue-600">{{ vitals.oxygenSaturation }}%</span>
                  </div>
                  <div class="flex items-center justify-between pt-2 border-t border-slate-100">
                    <span class="text-slate-500 text-sm">BMI</span>
                    <mat-chip-set>
                      <mat-chip class="font-bold" [color]="getBmiColor(vitals.bmi)" selected>{{ vitals.bmi | number:'1.1-1' }}</mat-chip>
                    </mat-chip-set>
                  </div>
                  <p class="text-[10px] text-slate-400 mt-4 italic">Recorded at: {{ vitals.recordedAt | date:'medium' }}</p>
                </div>
              } @else {
                <div class="py-10 text-center">
                  <mat-icon class="text-slate-200 text-[40px] w-10 h-10 mb-2">history</mat-icon>
                  <p class="text-sm text-slate-400">No vitals recorded yet.</p>
                </div>
              }
            </mat-card-content>
          </mat-card>
 
          <mat-card class="border border-slate-200 shadow-none rounded-xl overflow-hidden">
            <div class="bg-slate-50 px-6 py-4 border-b border-slate-100 font-semibold text-slate-700">Health Overview</div>
            <mat-card-content class="p-6">
              <div class="grid grid-cols-2 gap-4 text-center">
                <div class="bg-blue-50/50 p-4 rounded-lg border border-blue-100">
                  <p class="text-xs text-blue-600 font-medium uppercase mb-1">Prescriptions</p>
                  <p class="text-2xl font-bold text-blue-900">{{ dashboard()?.activePrescriptions || 0 }}</p>
                </div>
                <div class="bg-amber-50/50 p-4 rounded-lg border border-amber-100">
                  <p class="text-xs text-amber-600 font-medium uppercase mb-1">Pending Fees</p>
                  <p class="text-2xl font-bold text-amber-900">{{ dashboard()?.pendingPayments || 0 }}</p>
                </div>
              </div>
            </mat-card-content>
          </mat-card>
        </div>
 
        <!-- Right: Timeline -->
        <div class="lg:col-span-2">
          <mat-card class="border border-slate-200 shadow-none rounded-xl h-full flex flex-col overflow-hidden">
            <div class="bg-slate-50 px-6 py-4 border-b border-slate-100 flex items-center justify-between">
              <span class="font-semibold text-slate-700">Clinical Timeline</span>
              <button mat-button class="text-xs text-blue-600">View Full History</button>
            </div>
            <mat-card-content class="p-6 flex-1 overflow-y-auto">
              @if (timeline().length > 0) {
                <div class="relative pl-8 space-y-8 before:content-[''] before:absolute before:left-[11px] before:top-2 before:bottom-2 before:w-[2px] before:bg-slate-100">
                  @for (event of timeline(); track event.id) {
                    <div class="relative">
                      <div class="absolute -left-[37px] top-1 w-6 h-6 rounded-full bg-white border-2 border-blue-500 z-10 flex items-center justify-center">
                        <mat-icon class="text-[12px] text-blue-600 w-auto h-auto">{{ getEventIcon(event.eventType) }}</mat-icon>
                      </div>
                      <div class="flex flex-col">
                        <div class="flex items-center justify-between">
                          <span class="text-xs font-bold text-slate-400 uppercase tracking-tighter">{{ event.eventDate | date:'mediumDate' }}</span>
                          <span class="text-[10px] text-slate-400">{{ event.eventDate | date:'shortTime' }}</span>
                        </div>
                        <h4 class="font-semibold text-slate-900 mt-1 mb-1">{{ event.eventType.replace('_', ' ') }}</h4>
                        <p class="text-sm text-slate-500 leading-relaxed">{{ event.description }}</p>
                      </div>
                    </div>
                  }
                </div>
              } @else {
                <div class="py-20 text-center">
                  <mat-icon class="text-slate-200 text-[60px] w-16 h-16 mb-4">timeline</mat-icon>
                  <p class="text-slate-400">No timeline events found for this patient.</p>
                </div>
              }
            </mat-card-content>
          </mat-card>
        </div>
      </div>
    </div>
  `,
  styles: [`:host { display: block; }`]
})
export class PatientHealthDetailComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private healthService = inject(HealthService);
  private dialog = inject(MatDialog);
 
  patientId = signal<string>('');
  dashboard = signal<PatientDashboard | null>(null);
  timeline = signal<PatientTimeline[]>([]);
 
  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.patientId.set(id);
      this.loadData();
    }
  }
 
  loadData(): void {
    const id = this.patientId();
    this.healthService.getPatientDashboard(id).subscribe(res => {
      if (res.success) this.dashboard.set(res.data);
    });
 
    this.healthService.getPatientTimelineForAdmin(id).subscribe(res => {
      if (res.success) this.timeline.set(res.data.content);
    });
  }
 
  openRecordVitals(): void {
    const dialogRef = this.dialog.open(RecordVitalsDialogComponent, {
      width: '600px',
      data: { patientId: this.patientId() }
    });
 
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loadData(); // Refresh data after update
      }
    });
  }
 
  getEventIcon(type: string): string {
    switch (type) {
      case 'APPOINTMENT': return 'event';
      case 'PRESCRIPTION': return 'history_edu';
      case 'LAB_REPORT': return 'biotech';
      case 'VITAL_SIGNS': return 'monitor_heart';
      case 'PAYMENT': return 'payments';
      default: return 'radio_button_checked';
    }
  }
 
  getBmiColor(bmi: number): 'primary' | 'warn' | 'accent' {
    if (bmi < 18.5) return 'accent';
    if (bmi < 25) return 'primary';
    return 'warn';
  }
}
