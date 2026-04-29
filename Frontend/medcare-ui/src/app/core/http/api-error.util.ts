import { HttpErrorResponse } from '@angular/common/http';
import { environment } from '../../../environments/environment';

/**
 * Turns HTTP/API failures into short, user-facing text (no stack traces).
 */
export function friendlyApiError(err: unknown, fallback: string): string {
  if (err instanceof HttpErrorResponse) {
    const body = err.error as { message?: string } | null | undefined;
    if (body && typeof body.message === 'string' && body.message.trim().length > 0) {
      return body.message;
    }
    if (err.status === 403) {
      return 'You do not have permission for this action. Use an account with the right role, or ask an administrator.';
    }
    if (err.status === 401) {
      return 'Your session has expired or you are not signed in. Please sign in again.';
    }
    if (err.status === 404) {
      return 'The requested item was not found or may have been removed.';
    }
    if (err.status === 409) {
      return 'This action conflicts with existing data (for example a duplicate username).';
    }
    if (err.status === 0) {
      const origin = typeof document !== 'undefined' ? document.location.origin : '';
      const backend = environment.apiBackendOrigin || 'http://localhost:8081';
      return `Cannot reach the Medcare API (backend should be at ${backend}). In folder Frontend/medcare-ui run: npm run dev — or open a second terminal and run: npm run start:api. Use the app at ${origin || 'http://127.0.0.1:4200'} (UI port), not the API URL directly in the address bar.`;
    }
    if (err.status >= 500) {
      return 'The server had a problem. Try again in a moment or contact support if it continues.';
    }
    return err.status ? `Request failed (HTTP ${err.status}). ${fallback}` : fallback;
  }
  return fallback;
}
