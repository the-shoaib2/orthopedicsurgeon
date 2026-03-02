import { Component, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { FormsModule, ReactiveFormsModule, FormBuilder, Validators, FormGroup } from '@angular/forms';
import { 
  ZrdButtonComponent, ZrdCardComponent, ZrdStepperComponent, ZrdStep, 
  ZrdDatePickerComponent, ZrdSelectComponent, ZrdInputComponent, ZrdToastComponent,
  ZrdConfirmDialogComponent, ZrdBadgeComponent, ZrdTextareaComponent
} from '@repo/ui';
import { PublicApiService } from '../../../core/services/public-api.service';
import { Doctor, AppointmentType, ApiResponse } from '@repo/types';

@Component({
  selector: 'app-booking',
  standalone: true,
  imports: [
    CommonModule, RouterModule, FormsModule, ReactiveFormsModule,
    ZrdButtonComponent, ZrdCardComponent, ZrdStepperComponent,
    ZrdDatePickerComponent, ZrdSelectComponent, ZrdInputComponent,
    ZrdConfirmDialogComponent, ZrdBadgeComponent, ZrdTextareaComponent
  ],
  template: `
    <div class="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
      <zrd-card class="overflow-visible">
        <div header>
          <div class="flex items-center gap-4">
             <button (click)="cancel()" class="p-2 hover:bg-secondary-50 rounded-lg transition-colors">
               <i class="pi pi-arrow-left text-secondary-500"></i>
             </button>
             <div>
               <h1 class="text-xl font-bold text-secondary-900">Book Appointment</h1>
               <p class="text-xs text-secondary-500">Step {{ currentStep() + 1 }} of 4</p>
             </div>
          </div>
        </div>

        <!-- Stepper -->
        <div class="px-8 pt-4 pb-8 border-b border-secondary-100 bg-secondary-50/30">
          <zrd-stepper [steps]="bookingSteps" [currentStep]="currentStep()"></zrd-stepper>
        </div>

        <!-- Step Content -->
        <div class="p-8">
           <form [formGroup]="bookingForm">
             
             <!-- Step 1: Select Type & Date -->
             <div *ngIf="currentStep() === 0" class="space-y-6 animate-in fade-in duration-300">
                <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                   <zrd-select 
                     label="Appointment Type" 
                     [options]="appointmentTypes" 
                     formControlName="type"
                     [required]="true"
                   ></zrd-select>
                   <zrd-datepicker 
                     label="Preferred Date" 
                     formControlName="date"
                     [minDate]="todayStr"
                     [required]="true"
                   ></zrd-datepicker>
                </div>
                <div class="p-4 bg-primary-50 rounded-xl border border-primary-100 flex gap-4">
                   <i class="pi pi-info-circle text-primary-600 mt-1"></i>
                   <p class="text-sm text-primary-700">Available time slots will be fetched based on your selected date. We only show dates where the doctor is active.</p>
                </div>
             </div>

             <!-- Step 2: Select Time Slot -->
             <div *ngIf="currentStep() === 1" class="space-y-6 animate-in fade-in duration-300">
                <h3 class="text-sm font-bold text-secondary-900 uppercase tracking-widest mb-4">Select an Available Slot</h3>
                <div class="grid grid-cols-3 sm:grid-cols-4 md:grid-cols-6 gap-3">
                   <button 
                     *ngFor="let slot of availableSlots"
                     type="button"
                     (click)="bookingForm.patchValue({ timeSlot: slot })"
                     class="px-3 py-2.5 rounded-xl border-2 text-sm font-bold transition-all"
                     [class]="bookingForm.get('timeSlot')?.value === slot 
                        ? 'border-primary-600 bg-primary-50 text-primary-600 shadow-md' 
                        : 'border-secondary-100 hover:border-secondary-300 text-secondary-600'"
                   >
                     {{ slot }}
                   </button>
                </div>
             </div>

             <!-- Step 3: Patient Information -->
             <div *ngIf="currentStep() === 2" class="space-y-6 animate-in fade-in duration-300">
                <div class="space-y-4">
                   <zrd-textarea 
                     label="Reason for Visit" 
                     placeholder="Briefly describe your symptoms or concern..."
                     formControlName="reason"
                     [rows]="4"
                     [required]="true"
                   ></zrd-textarea>
                   <zrd-input 
                     label="Notes for Doctor (Optional)" 
                     formControlName="notes"
                   ></zrd-input>
                </div>
             </div>

             <!-- Step 4: Confirmation -->
             <div *ngIf="currentStep() === 3" class="space-y-6 animate-in fade-in duration-300">
                <div class="bg-secondary-50 rounded-2xl p-6 border border-secondary-100 space-y-4">
                   <div class="flex justify-between items-center pb-4 border-b border-secondary-200">
                      <span class="text-secondary-500 text-sm">Consultation with</span>
                      <span class="font-bold text-secondary-900">Dr. {{ doctor()?.user?.firstName }} {{ doctor()?.user?.lastName }}</span>
                   </div>
                   <div class="flex justify-between items-center pb-4 border-b border-secondary-200">
                      <span class="text-secondary-500 text-sm">Type</span>
                      <zrd-badge variant="info">{{ bookingForm.get('type')?.value }}</zrd-badge>
                   </div>
                   <div class="flex justify-between items-center pb-4 border-b border-secondary-200">
                      <span class="text-secondary-500 text-sm">Date & Time</span>
                      <span class="font-bold text-secondary-900">{{ bookingForm.get('date')?.value }} at {{ bookingForm.get('timeSlot')?.value }}</span>
                   </div>
                   <div class="flex justify-between items-center">
                      <span class="text-secondary-500 text-sm">Total Fee</span>
                      <span class="text-xl font-black text-primary-600">$\{{ doctor()?.consultationFee }}</span>
                   </div>
                </div>
             </div>

           </form>
        </div>

        <div footer class="flex justify-between items-center p-8 bg-secondary-50/50">
           <button zrdButton variant="ghost" (click)="prevStep()" [disabled]="currentStep() === 0">Previous</button>
           
           <div class="flex gap-3">
              <button zrdButton variant="outline" (click)="cancel()">Cancel</button>
              <button 
                *ngIf="currentStep() < 3"
                zrdButton 
                [disabled]="!isStepValid()"
                (click)="nextStep()"
              >
                Continue
              </button>
              <button 
                *ngIf="currentStep() === 3"
                zrdButton 
                variant="primary"
                [loading]="submitting()"
                (click)="submit()"
              >
                Confirm & Book
              </button>
           </div>
        </div>
      </zrd-card>
    </div>

    <!-- Success Feedback Overlay (simplified) -->
    <div *ngIf="success()" class="fixed inset-0 z-[100] flex items-center justify-center bg-secondary-900/80 backdrop-blur-sm animate-in fade-in duration-500">
       <div class="bg-white rounded-3xl p-10 max-w-md w-full text-center shadow-2xl animate-in zoom-in-95 duration-500">
          <div class="w-20 h-20 bg-green-100 text-green-600 rounded-full flex items-center justify-center mx-auto mb-6">
             <i class="pi pi-check text-4xl"></i>
          </div>
          <h2 class="text-2xl font-bold text-secondary-900 mb-2">Booking Confirmed!</h2>
          <p class="text-secondary-500 mb-8">Your appointment with Dr. {{ doctor()?.user?.firstName }} has been successfully scheduled. Check your portal for details.</p>
          <button zrdButton class="w-full" (click)="finish()">Go to Portal</button>
       </div>
    </div>
  `
})
export class BookingComponent {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private fb = inject(FormBuilder);
  private api = inject(PublicApiService);

  doctor = signal<Doctor | null>(null);
  currentStep = signal(0);
  submitting = signal(false);
  success = signal(false);

  todayStr = new Date().toISOString().split('T')[0];

  bookingForm: FormGroup = this.fb.group({
    type: ['', Validators.required],
    date: ['', Validators.required],
    timeSlot: ['', Validators.required],
    reason: ['', Validators.required],
    notes: ['']
  });

  bookingSteps: ZrdStep[] = [
    { label: 'Schedule', description: 'Type & Date' },
    { label: 'Time', description: 'Select Slot' },
    { label: 'Reason', description: 'Details' },
    { label: 'Finish', description: 'Confirmation' }
  ];

  appointmentTypes = [
    { label: 'First Consultation', value: 'FIRST_CONSULTATION' },
    { label: 'Follow Up', value: 'FOLLOW_UP' },
    { label: 'Surgery Consultation', value: 'SURGERY_CONSULTATION' },
    { label: 'Emergency', value: 'EMERGENCY' }
  ];

  availableSlots = [
    '09:00 AM', '09:30 AM', '10:00 AM', '10:30 AM', 
    '11:00 AM', '11:30 AM', '02:00 PM', '02:30 PM',
    '03:00 PM', '03:30 PM', '04:00 PM', '04:30 PM'
  ];

  ngOnInit() {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.api.getDoctorById(id).subscribe((res: ApiResponse<Doctor>) => this.doctor.set(res.data));
    }
  }

  isStepValid(): boolean {
    const s = this.currentStep();
    if (s === 0) return !!this.bookingForm.get('type')?.value && !!this.bookingForm.get('date')?.value;
    if (s === 1) return !!this.bookingForm.get('timeSlot')?.value;
    if (s === 2) return !!this.bookingForm.get('reason')?.value;
    return true;
  }

  nextStep() {
    this.currentStep.update(s => s + 1);
  }

  prevStep() {
    this.currentStep.update(s => s - 1);
  }

  cancel() {
    const id = this.doctor()?.id;
    this.router.navigate(['/doctors', id]);
  }

  submit() {
    this.submitting.set(true);
    const data = {
      doctorId: this.doctor()?.id,
      ...this.bookingForm.value
    };

    this.api.bookAppointment(data).subscribe({
      next: () => {
        this.submitting.set(false);
        this.success.set(true);
      },
      error: () => this.submitting.set(false)
    });
  }

  finish() {
    this.router.navigate(['/portal/appointments']);
  }
}
