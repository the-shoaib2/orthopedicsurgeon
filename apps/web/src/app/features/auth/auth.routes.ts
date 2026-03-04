import { Routes } from '@angular/router';
import { AuthLayoutComponent } from '@core/layouts/auth-layout/auth-layout.component';

export const AUTH_ROUTES: Routes = [
  {
    path: '',
    component: AuthLayoutComponent,
    children: [
      { path: '', redirectTo: 'login', pathMatch: 'full' },
      { 
        path: 'login', 
        loadComponent: () => import('@features/auth/login/login.component').then(m => m.LoginComponent) 
      },
      { 
        path: 'register', 
        loadComponent: () => import('@features/auth/register/register.component').then(m => m.RegisterComponent) 
      },
      {
        path: 'forgot-password',
        loadComponent: () => import('@features/auth/forgot-password/forgot-password.component').then(m => m.ForgotPasswordComponent)
      },
      {
        path: 'reset-password',
        loadComponent: () => import('@features/auth/reset-password/reset-password.component').then(m => m.ResetPasswordComponent)
      }
    ]
  }
];
