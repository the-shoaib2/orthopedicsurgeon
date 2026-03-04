import { Injectable, signal, inject, InjectionToken } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap, catchError, of, map, finalize } from 'rxjs';
import { User, Role } from '@repo/types';

export const AUTH_API_URL = new InjectionToken<string>('AUTH_API_URL', {
  providedIn: 'root',
  factory: () => 'http://localhost:8080/api/v1/auth'
});

export interface AuthResponse {
  accessToken?: string;
  refreshToken?: string;
  user?: User;
  requiresTwoFactor?: boolean;
  tempToken?: string;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  private router = inject(Router);
  private apiUrl = inject(AUTH_API_URL);

  currentUser = signal<User | null>(null);
  loading = signal(false);
  error = signal<string | null>(null);

  login(credentials: any): Observable<AuthResponse> {
    this.loading.set(true);
    this.error.set(null);
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, credentials).pipe(
      tap((res: AuthResponse) => this.handleSuccess(res)),
      catchError(err => {
        this.error.set(err.error?.message || 'Login failed. Please check your credentials.');
        throw err;
      }),
      finalize(() => this.loading.set(false))
    );
  }

  googleLogin(idToken: string): Observable<AuthResponse> {
    this.loading.set(true);
    this.error.set(null);
    return this.http.post<AuthResponse>(`${this.apiUrl}/login/google`, { idToken }).pipe(
      tap((res: AuthResponse) => this.handleSuccess(res)),
      catchError(err => {
        this.error.set(err.error?.message || 'Google login failed.');
        throw err;
      }),
      finalize(() => this.loading.set(false))
    );
  }

  logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('refreshToken');
    this.currentUser.set(null);
    this.router.navigate(['/auth/login']);
  }

  verify2fa(data: { tempToken: string; totpCode: string }): Observable<AuthResponse> {
    this.loading.set(true);
    return this.http.post<AuthResponse>(`${this.apiUrl}/verify-2fa`, data).pipe(
      tap((res: AuthResponse) => {
        if (res.accessToken) {
          this.handleSuccess(res);
        }
      }),
      finalize(() => this.loading.set(false))
    );
  }

  register(userData: any): Observable<AuthResponse> {
    this.loading.set(true);
    this.error.set(null);
    return this.http.post<AuthResponse>(`${this.apiUrl}/register`, userData).pipe(
      finalize(() => this.loading.set(false))
    );
  }

  forgotPassword(email: string): Observable<any> {
    this.loading.set(true);
    return this.http.post(`${this.apiUrl}/forgot-password`, { email }).pipe(
      finalize(() => this.loading.set(false))
    );
  }

  resetPassword(data: any): Observable<any> {
    this.loading.set(true);
    return this.http.post(`${this.apiUrl}/reset-password`, data).pipe(
      finalize(() => this.loading.set(false))
    );
  }

  isLoggedIn(): boolean {
    return !!localStorage.getItem('token');
  }

  hasRole(role: Role): boolean {
    return this.currentUser()?.roles.includes(role) || false;
  }

  checkAuth(): Observable<boolean> {
    if (!this.isLoggedIn()) return of(false);
    return this.http.get<User>(`${this.apiUrl}/me`).pipe(
      tap(user => this.currentUser.set(user)),
      map(() => true),
      catchError(() => {
        this.logout();
        return of(false);
      })
    );
  }

  private handleSuccess(res: AuthResponse) {
    if (res.accessToken) localStorage.setItem('token', res.accessToken);
    if (res.refreshToken) localStorage.setItem('refreshToken', res.refreshToken);
    if (res.user) this.currentUser.set(res.user);
  }
}
