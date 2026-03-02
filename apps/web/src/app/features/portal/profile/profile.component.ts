import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ZrdInputComponent, ZrdButtonComponent, ZrdCardComponent, ZrdAvatarComponent, ZrdSelectComponent } from '@repo/ui';
import { AuthService } from '@repo/auth';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, ZrdInputComponent, ZrdButtonComponent, ZrdCardComponent, ZrdAvatarComponent, ZrdSelectComponent],
  template: `
    <div class="space-y-8 animate-in fade-in slide-in-from-bottom-4 duration-500">
      <div class="flex items-center gap-6">
         <div class="relative group">
            <zrd-avatar [name]="(auth.currentUser()?.firstName || '') + ' ' + (auth.currentUser()?.lastName || '')" size="xl" border></zrd-avatar>
            <button class="absolute -bottom-2 -right-2 p-2 bg-primary-600 text-white rounded-full shadow-lg hover:bg-primary-700 transition-colors">
               <i class="pi pi-camera"></i>
            </button>
         </div>
         <div>
            <h1 class="text-2xl font-black text-secondary-900">{{ auth.currentUser()?.firstName }} {{ auth.currentUser()?.lastName }}</h1>
            <p class="text-secondary-500">Patient ID: #PAT-00{{ auth.currentUser()?.id }}</p>
         </div>
      </div>

      <div class="grid grid-cols-1 lg:grid-cols-2 gap-8">
         <zrd-card>
            <h3 slot="header" class="text-sm font-bold text-secondary-900 uppercase tracking-widest">Personal Information</h3>
            <form [formGroup]="profileForm" class="space-y-4">
               <div class="grid grid-cols-2 gap-4">
                  <zrd-input label="First Name" formControlName="firstName"></zrd-input>
                  <zrd-input label="Last Name" formControlName="lastName"></zrd-input>
               </div>
               <zrd-input label="Email Address" formControlName="email" type="email" [disabled]="true"></zrd-input>
               <zrd-input label="Phone Number" formControlName="phone"></zrd-input>
               
               <div class="flex justify-end pt-4">
                  <button zrdButton variant="primary">Save Changes</button>
               </div>
            </form>
         </zrd-card>

         <zrd-card>
            <h3 slot="header" class="text-sm font-bold text-secondary-900 uppercase tracking-widest">Medical Details</h3>
            <form [formGroup]="medicalForm" class="space-y-4">
               <div class="grid grid-cols-2 gap-4">
                  <zrd-select label="Blood Group" [options]="bloodGroups" formControlName="bloodGroup"></zrd-select>
                  <zrd-input label="Date of Birth" formControlName="dob" type="date"></zrd-input>
               </div>
               <zrd-textarea label="Allergies" formControlName="allergies" placeholder="List any known allergies..."></zrd-textarea>
               
               <div class="flex justify-end pt-4">
                  <button zrdButton variant="primary">Update Records</button>
               </div>
            </form>
         </zrd-card>
      </div>
    </div>
  `
})
export class ProfileComponent {
  auth = inject(AuthService);
  fb = inject(FormBuilder);

  profileForm: FormGroup = this.fb.group({
    firstName: [this.auth.currentUser()?.firstName, Validators.required],
    lastName: [this.auth.currentUser()?.lastName, Validators.required],
    email: [{ value: this.auth.currentUser()?.email, disabled: true }],
    phone: ['+880 1712 345678', Validators.required]
  });

  medicalForm: FormGroup = this.fb.group({
    bloodGroup: ['A+'],
    dob: ['1992-05-15'],
    allergies: ['Penicillin']
  });

  bloodGroups = [
    { label: 'A Positive (A+)', value: 'A+' },
    { label: 'A Negative (A-)', value: 'A-' },
    { label: 'B Positive (B+)', value: 'B+' },
    { label: 'B Negative (B-)', value: 'B-' },
    { label: 'O Positive (O+)', value: 'O+' },
    { label: 'O Negative (O-)', value: 'O-' },
    { label: 'AB Positive (AB+)', value: 'AB+' },
    { label: 'AB Negative (AB-)', value: 'AB-' }
  ];
}
