import { Routes } from '@angular/router';
import { PortalLayoutComponent } from '@core/layouts/portal-layout/portal-layout.component';
import { authGuard } from '@repo/auth';

export const PORTAL_ROUTES: Routes = [
  {
    path: '',
    component: PortalLayoutComponent,
    canActivate: [authGuard],
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      { 
        path: 'dashboard', 
        loadComponent: () => import('@features/portal/dashboard/dashboard.component').then(m => m.DashboardComponent) 
      },
      { 
        path: 'appointments', 
        loadComponent: () => import('@features/portal/appointments/appointment-list.component').then(m => m.AppointmentListComponent) 
      },
      { 
        path: 'history/prescriptions', 
        loadComponent: () => import('@features/portal/prescriptions/prescription-list.component').then(m => m.PrescriptionListComponent) 
      },
      { 
        path: 'history/reports', 
        loadComponent: () => import('@features/portal/reports/report-list.component').then(m => m.ReportListComponent) 
      },
      { 
        path: 'payments', 
        loadComponent: () => import('@features/portal/payments/payment-list.component').then(m => m.PaymentListComponent) 
      },
      { 
        path: 'settings', 
        loadComponent: () => import('@features/portal/profile/profile.component').then(m => m.ProfileComponent) 
      }
    ]
  }
];
