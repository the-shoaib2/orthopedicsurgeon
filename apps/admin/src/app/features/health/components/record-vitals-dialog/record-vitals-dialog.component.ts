import { Component, Inject, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatDialogModule, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { HealthService } from '@core/services/health.service';
import { RecordVitalsRequest } from '@repo/types';
 
@Component({
  selector: 'app-record-vitals-dialog',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule
  ],
  template: `
    <h2 mat-dialog-title class="flex items-center gap-2">
      <mat-icon color="primary">monitor_heart</mat-icon>
      Record Vital Signs
    </h2>
    <mat-dialog-content>
      <form [formGroup]="vitalsForm" class="grid grid-cols-1 md:grid-cols-2 gap-4 pt-2">
        <mat-form-field appearance="outline">
          <mat-label>Systolic BP (mmHg)</mat-label>
          <input matInput type="number" formControlName="bloodPressureSystolic" placeholder="e.g. 120">
        </mat-form-field>
 
        <mat-form-field appearance="outline">
          <mat-label>Diastolic BP (mmHg)</mat-label>
          <input matInput type="number" formControlName="bloodPressureDiastolic" placeholder="e.g. 80">
        </mat-form-field>
 
        <mat-form-field appearance="outline">
          <mat-label>Heart Rate (bpm)</mat-label>
          <input matInput type="number" formControlName="heartRate" placeholder="e.g. 72">
        </mat-form-field>
 
        <mat-form-field appearance="outline">
          <mat-label>Temperature (°C)</mat-label>
          <input matInput type="number" step="0.1" formControlName="temperature" placeholder="e.g. 36.6">
        </mat-form-field>
 
        <mat-form-field appearance="outline">
          <mat-label>Weight (kg)</mat-label>
          <input matInput type="number" step="0.1" formControlName="weight" placeholder="e.g. 70.0">
        </mat-form-field>
 
        <mat-form-field appearance="outline">
          <mat-label>Height (cm)</mat-label>
          <input matInput type="number" formControlName="height" placeholder="e.g. 175">
        </mat-form-field>
 
        <mat-form-field appearance="outline">
          <mat-label>Oxygen Saturation (%)</mat-label>
          <input matInput type="number" formControlName="oxygenSaturation" placeholder="e.g. 98">
        </mat-form-field>
 
        <mat-form-field appearance="outline" class="md:col-span-2">
          <mat-label>Notes</mat-label>
          <textarea matInput formControlName="notes" rows="3" placeholder="Any additional observations..."></textarea>
        </mat-form-field>
      </form>
    </mat-dialog-content>
    <mat-dialog-actions align="end" class="pb-4 pr-4">
      <button mat-button (click)="onCancel()">Cancel</button>
      <button mat-flat-button color="primary" [disabled]="vitalsForm.invalid || isSubmitting" (click)="onSubmit()">
        {{ isSubmitting ? 'Saving...' : 'Save Vitals' }}
      </button>
    </mat-dialog-actions>
  `,
  styles: [`
    mat-form-field { width: 100%; }
    :host { display: block; }
  `]
})
export class RecordVitalsDialogComponent {
  private fb = inject(FormBuilder);
  private healthService = inject(HealthService);
  private dialogRef = inject(MatDialogRef<RecordVitalsDialogComponent>);
  
  isSubmitting = false;
  vitalsForm: FormGroup;
 
  constructor(@Inject(MAT_DIALOG_DATA) public data: { patientId: string, appointmentId?: string }) {
    this.vitalsForm = this.fb.group({
      bloodPressureSystolic: [null, [Validators.required, Validators.min(50), Validators.max(250)]],
      bloodPressureDiastolic: [null, [Validators.required, Validators.min(30), Validators.max(150)]],
      heartRate: [null, [Validators.required, Validators.min(30), Validators.max(250)]],
      temperature: [null, [Validators.required, Validators.min(30), Validators.max(45)]],
      weight: [null, [Validators.required, Validators.min(1), Validators.max(500)]],
      height: [null, [Validators.required, Validators.min(30), Validators.max(300)]],
      oxygenSaturation: [null, [Validators.required, Validators.min(50), Validators.max(100)]],
      notes: ['']
    });
  }
 
  onCancel(): void {
    this.dialogRef.close();
  }
 
  onSubmit(): void {
    if (this.vitalsForm.valid) {
      this.isSubmitting = true;
      const request: RecordVitalsRequest = {
        ...this.vitalsForm.value,
        appointmentId: this.data.appointmentId
      };
 
      this.healthService.recordPatientVitals(this.data.patientId, request).subscribe({
        next: (response) => {
          this.isSubmitting = false;
          if (response.success) {
            this.dialogRef.close(true);
          }
        },
        error: (err) => {
          this.isSubmitting = false;
          console.error('Error recording vitals:', err);
        }
      });
    }
  }
}
