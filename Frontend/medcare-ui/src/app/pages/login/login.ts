import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { SessionAuthService, type AppRole } from '../../core/auth/session-auth.service';
import { ConfigHubDataService } from '../../core/rbac/config-hub-data.service';
import { environment } from '../../../environments/environment';

interface AuthResponseBody {
  accessToken: string;
  tokenType: string;
  role: string;
  scopes: string[];
  patientId?: number | null;
  doctorId?: number | null;
}

interface ApiLoginEnvelope {
  success: boolean;
  data?: AuthResponseBody;
  message?: string;
}

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule],
  templateUrl: './login.html',
  styleUrl: './login.scss',
})
export class Login {
  private readonly fb = inject(FormBuilder);
  private readonly router = inject(Router);
  private readonly auth = inject(SessionAuthService);
  private readonly http = inject(HttpClient);
  private readonly configHub = inject(ConfigHubDataService);

  /** Where Spring Boot runs; UI calls `/api` via dev proxy when apiUrl is ''. */
  protected readonly apiHint = environment.apiBackendOrigin;

  readonly demoNote = signal<string | null>(null);
  readonly showAdmin = signal(false);

  readonly form = this.fb.group({
    username: [''],
    password: [''],
    portal: this.fb.nonNullable.control<
      'PATIENT' | 'DOCTOR' | 'RECEPTIONIST' | 'ADMIN' | 'IT_SUPPORT'
    >('PATIENT'),
  });

  submit(): void {
    this.demoNote.set(null);
    const portal = this.form.get('portal')?.value ?? 'PATIENT';
    const u = this.form.get('username')?.value?.trim() ?? '';
    const p = this.form.get('password')?.value ?? '';

    if (u && p) {
      this.http
        .post<ApiLoginEnvelope>(`${environment.apiUrl}/api/auth/login`, { username: u, password: p })
        .subscribe({
          next: (res) => {
            if (!res.success || !res.data?.accessToken) {
              this.demoNote.set(res.message ?? 'Login failed');
              return;
            }
            const mapped = mapBackendRole(res.data.role);
            if (portal === 'DOCTOR' && mapped !== 'DOCTOR') {
              this.demoNote.set(
                `This account is role "${res.data.role}". Doctor portal is only for doctor accounts (e.g. doctor or doctor02–doctor05 / doctor123).`,
              );
              return;
            }
            if (portal === 'RECEPTIONIST' && mapped !== 'RECEPTIONIST') {
              this.demoNote.set(
                `This account is role "${res.data.role}". Use Receptionist portal with receptionist, receptionist2, or staff01–staff05 (passwords reception123 / staff123).`,
              );
              return;
            }
            if (portal === 'ADMIN' && mapped !== 'ADMIN') {
              this.demoNote.set(
                `This account is role "${res.data.role}". Use Administrator portal for admin only, or IT Support for itsupport.`,
              );
              return;
            }
            if (portal === 'IT_SUPPORT' && mapped !== 'IT_SUPPORT') {
              this.demoNote.set(
                `This account is role "${res.data.role}". Use IT Support portal with itsupport or itsupport2 / itsupport123.`,
              );
              return;
            }
            if (portal === 'PATIENT' && mapped !== 'PATIENT') {
              this.demoNote.set(
                `This account is role "${res.data.role}". Use Patient portal only for patient accounts (e.g. patient01 / patient123), or switch portal.`,
              );
              return;
            }
            this.auth.setAuthenticatedSession(res.data.accessToken, mapped, u, res.data.patientId, res.data.doctorId);
            this.configHub.reload();
            void this.router.navigateByUrl(routeForRole(mapped));
          },
          error: (err: HttpErrorResponse) => {
            const body = err.error as ApiLoginEnvelope | undefined;
            if (body?.message) {
              this.demoNote.set(body.message);
            } else if (err.status === 401) {
              this.demoNote.set('Invalid username or password.');
            } else if (err.status === 0 || err.status === undefined) {
              this.demoNote.set(
                `Cannot reach the API. Start the stack with "npm run dev" in medcare-ui, or run "npm run start:api" then keep this page on http://127.0.0.1:4200 (backend at ${environment.apiBackendOrigin}).`,
              );
            } else {
              this.demoNote.set(
                err.status === 403
                  ? 'Sign-in was rejected (forbidden). Check username, password, and portal.'
                  : `Login failed (HTTP ${err.status}). Try again or confirm the API is running.`,
              );
            }
          },
        });
      return;
    }

    if (portal === 'DOCTOR') {
      this.demoNote.set(
        'Doctor workspace needs API login. Use doctor / doctor123 or doctor02–doctor05 / doctor123.',
      );
      return;
    }

    if (portal === 'ADMIN') {
      this.demoNote.set(
        'Administrator workspace needs API login. Use username admin and password admin123 (seeded when the backend starts), with the API running.',
      );
      return;
    }
    if (portal === 'RECEPTIONIST') {
      this.demoNote.set(
        'Receptionist workspace needs API login. Use receptionist / reception123, receptionist2 / reception123, or staff01–staff05 / staff123.',
      );
      return;
    }

    if (portal === 'IT_SUPPORT') {
      this.demoNote.set(
        'IT Support needs API login. Use itsupport or itsupport2 with password itsupport123.',
      );
      return;
    }

    this.demoNote.set(
      'Enter username and password for patient API login. Doctor, receptionist, IT support, and administrator require API credentials.',
    );
  }

  setPortal(p: 'PATIENT' | 'DOCTOR' | 'RECEPTIONIST' | 'ADMIN' | 'IT_SUPPORT'): void {
    this.form.patchValue({ portal: p });
  }
}

function mapBackendRole(r: string): AppRole {
  switch (r) {
    case 'ADMIN':
      return 'ADMIN';
    case 'IT_SUPPORT':
      return 'IT_SUPPORT';
    case 'DOCTOR':
      return 'DOCTOR';
    case 'RECEPTIONIST':
      return 'RECEPTIONIST';
    case 'PATIENT':
    default:
      return 'PATIENT';
  }
}

function routeForRole(role: AppRole): string {
  switch (role) {
    case 'ADMIN':
      return '/admin/dashboard';
    case 'IT_SUPPORT':
      return '/it-support/dashboard';
    case 'DOCTOR':
      return '/doctor/dashboard';
    case 'RECEPTIONIST':
      return '/receptionist/dashboard';
    case 'PATIENT':
    default:
      return '/patient/dashboard';
  }
}
