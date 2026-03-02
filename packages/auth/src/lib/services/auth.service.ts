import { Injectable, signal, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap, catchError, of, map, finalize } from 'rxjs';
import { User, Role } from '@repo/types';

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  user: User;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  private router = inject(Router);
  private apiUrl = 'http://localhost:8080/api/v1/auth';

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

  logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('refreshToken');
    this.currentUser.set(null);
    this.router.navigate(['/auth/login']);
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
    localStorage.setItem('token', res.accessToken);
    localStorage.setItem('refreshToken', res.refreshToken);
    this.currentUser.set(res.user);
  }
}
