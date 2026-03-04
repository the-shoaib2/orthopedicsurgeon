import { Routes } from '@angular/router';
import { AdminLayoutComponent } from '@core/layouts/admin-layout/admin-layout.component';
import { authGuard } from '@repo/auth';

export const routes: Routes = [
  {
    path: '',
    component: AdminLayoutComponent,
    canActivate: [authGuard],
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      { 
        path: 'dashboard', 
        loadComponent: () => import('@features/dashboard/admin.component').then(m => m.AdminComponent) 
      },
      { 
        path: 'users', 
        loadComponent: () => import('@features/users/user-management.component').then(m => m.UserManagementComponent) 
      },
      { 
        path: 'doctors', 
        loadComponent: () => import('@features/doctors/doctor-management.component').then(m => m.DoctorManagementComponent) 
      },
      { 
        path: 'patients', 
        loadComponent: () => import('@features/patients/patient-management.component').then(m => m.PatientManagementComponent) 
      },
      {
        path: 'patients/:id/health',
        loadComponent: () => import('@features/health/patient-health-detail/patient-health-detail.component').then(m => m.PatientHealthDetailComponent)
      },
      { 
        path: 'appointments', 
        loadComponent: () => import('@features/appointments/appointment-management.component').then(m => m.AppointmentManagementComponent) 
      },
      { 
        path: 'records/prescriptions', 
        loadComponent: () => import('@features/records/prescription-management.component').then(m => m.PrescriptionManagementComponent) 
      },
      { 
        path: 'records/reports', 
        loadComponent: () => import('@features/records/report-management.component').then(m => m.ReportManagementComponent) 
      },
      { 
        path: 'finance', 
        loadComponent: () => import('@features/finance/finance-management.component').then(m => m.FinanceManagementComponent) 
      },
      { 
        path: 'hospitals', 
        loadComponent: () => import('@features/hospitals/hospital-management.component').then(m => m.HospitalManagementComponent) 
      },
      {
        path: 'content/hero',
        loadComponent: () => import('@features/content/hero/hero-management.component').then(m => m.HeroManagementComponent)
      },
      {
        path: 'content/faq',
        loadComponent: () => import('@features/content/faq/faq-management.component').then(m => m.FaqManagementComponent)
      },
      {
        path: 'content/partners',
        loadComponent: () => import('@features/content/partners/partner-management.component').then(m => m.PartnerManagementComponent)
      },
      {
        path: 'blog',
        loadComponent: () => import('@features/blog/blog-management.component').then(m => m.BlogManagementComponent)
      }
    ]
  },
  {
    path: 'auth/login',
    loadComponent: () => import('@features/auth/admin-login.component').then(m => m.AdminLoginComponent)
  },
  {
    path: 'auth/forgot-password',
    loadComponent: () => import('@features/auth/forgot-password/forgot-password.component').then(m => m.ForgotPasswordComponent)
  },
  {
    path: 'auth/reset-password',
    loadComponent: () => import('@features/auth/reset-password/reset-password.component').then(m => m.ResetPasswordComponent)
  }
];
