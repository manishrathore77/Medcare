import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { SessionAuthService, type AppRole } from '../auth/session-auth.service';

/** Allow route when current session role is one of `allowed` and a JWT is present (matches secured APIs). */
export function roleGuard(...allowed: AppRole[]): CanActivateFn {
  return () => {
    const auth = inject(SessionAuthService);
    const router = inject(Router);
    const r = auth.role();
    if (!r || !allowed.includes(r)) return router.createUrlTree(['/login']);
    if (!auth.getToken()) return router.createUrlTree(['/login']);
    return true;
  };
}

/** Full administration: role ADMIN and a valid JWT (all secured admin APIs). */
export const adminGuard: CanActivateFn = () => {
  const auth = inject(SessionAuthService);
  const router = inject(Router);
  if (auth.role() !== 'ADMIN') return router.createUrlTree(['/login']);
  if (!auth.getToken()) return router.createUrlTree(['/login']);
  return true;
};

/** IT Support workspace: JWT required (same pattern as receptionist). */
export const itSupportGuard: CanActivateFn = () => {
  const auth = inject(SessionAuthService);
  const router = inject(Router);
  if (auth.role() !== 'IT_SUPPORT') return router.createUrlTree(['/login']);
  if (!auth.getToken()) return router.createUrlTree(['/login']);
  return true;
};
export const patientGuard: CanActivateFn = roleGuard('PATIENT');
export const doctorGuard: CanActivateFn = roleGuard('DOCTOR');
/** Receptionist routes require a JWT (secured APIs). */
export const receptionistGuard: CanActivateFn = () => {
  const auth = inject(SessionAuthService);
  const router = inject(Router);
  if (auth.role() !== 'RECEPTIONIST') return router.createUrlTree(['/login']);
  if (!auth.getToken()) return router.createUrlTree(['/login']);
  return true;
};
