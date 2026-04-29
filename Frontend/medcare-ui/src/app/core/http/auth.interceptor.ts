import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { SessionAuthService } from '../auth/session-auth.service';

/** Attaches JWT from session when calling the Medcare API. */
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(SessionAuthService);
  const token = auth.getToken();
  if (token && !req.headers.has('Authorization')) {
    return next(req.clone({ setHeaders: { Authorization: `Bearer ${token}` } }));
  }
  return next(req);
};
