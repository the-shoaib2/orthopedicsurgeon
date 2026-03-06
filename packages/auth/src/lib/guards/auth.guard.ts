import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { map } from 'rxjs';

export const authGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (authService.isLoggedIn()) {
    // If we have a token but no user object (e.g. after refresh), fetch it
    if (!authService.currentUser()) {
      return authService.checkAuth().pipe(
        map(isAuth => isAuth ? true : router.createUrlTree(['/auth/login']))
      );
    }
    return true;
  }

  return router.createUrlTree(['/auth/login']);
};
