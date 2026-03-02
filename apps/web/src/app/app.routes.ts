import { Routes } from '@angular/router';
import { MainLayoutComponent } from '@core/layouts/main-layout/main-layout.component';

export const routes: Routes = [
  {
    path: '',
    component: MainLayoutComponent,
    children: [
      {
        path: '',
        loadComponent: () => import('@features/home/home.component').then(m => m.HomeComponent)
      },
      {
        path: 'doctors',
        loadComponent: () => import('@features/doctors/doctor-list/doctor-list.component').then(m => m.DoctorListComponent)
      },
      {
        path: 'doctors/:id',
        loadComponent: () => import('@features/doctors/doctor-detail/doctor-detail.component').then(m => m.DoctorDetailComponent)
      },
      {
        path: 'doctors/:id/book',
        loadComponent: () => import('@features/doctors/booking/booking.component').then(m => m.BookingComponent)
      },
      {
        path: 'hospitals',
        loadComponent: () => import('@features/hospitals/hospital-list/hospital-list.component').then(m => m.HospitalListComponent)
      }
    ]
  },
  {
    path: 'auth',
    loadChildren: () => import('@features/auth/auth.routes').then(m => m.AUTH_ROUTES)
  },
  {
    path: 'portal',
    loadChildren: () => import('@features/portal/portal.routes').then(m => m.PORTAL_ROUTES)
  }
];
