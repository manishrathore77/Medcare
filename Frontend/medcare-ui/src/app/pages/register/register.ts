import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Component, inject, signal } from '@angular/core';
import {
  AbstractControl,
  FormBuilder,
  ReactiveFormsModule,
  ValidationErrors,
  Validators,
} from '@angular/forms';
import { RouterLink } from '@angular/router';
import { SessionAuthService } from '../../core/auth/session-auth.service';
import { friendlyApiError } from '../../core/http/api-error.util';
import { environment } from '../../../environments/environment';

/** Backend {@code ApiResponse<T>} shape */
interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T | null;
  status: number;
}

interface AuthResponse {
  accessToken: string;
  tokenType: string;
  role: string;
  scopes: string[];
  patientId?: number | null;
  doctorId?: number | null;
}

function passwordsMatch(control: AbstractControl): ValidationErrors | null {
  const p = control.get('password')?.value;
  const c = control.get('confirmPassword')?.value;
  if (p == null || c == null || p === '') {
    return null;
  }
  return p === c ? null : { passwordMismatch: true };
}

@Component({
  selector: 'app-register',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './register.html',
  styleUrl: './register.scss',
})
export class Register {
  private readonly http = inject(HttpClient);
  private readonly fb = inject(FormBuilder);
  private readonly sessionAuth = inject(SessionAuthService);

  readonly submitting = signal(false);
  readonly serverMessage = signal<string | null>(null);
  readonly success = signal(false);

  readonly form = this.fb.group(
    {
      username: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(64)]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(8), Validators.maxLength(128)]],
      confirmPassword: ['', [Validators.required]],
      firstName: ['', [Validators.required, Validators.maxLength(120)]],
      lastName: ['', [Validators.required, Validators.maxLength(120)]],
      phone: [''],
      gender: [''],
      dob: [''],
      address: [''],
      emergencyContact: [''],
    },
    { validators: passwordsMatch },
  );

  submit(): void {
    this.serverMessage.set(null);
    this.success.set(false);
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const v = this.form.getRawValue();
    const body: Record<string, string | undefined> = {
      username: v.username?.trim(),
      email: v.email?.trim(),
      password: v.password ?? '',
      firstName: v.firstName?.trim(),
      lastName: v.lastName?.trim(),
    };
    const phone = v.phone?.trim();
    if (phone) body['phone'] = phone;
    const gender = v.gender?.trim();
    if (gender) body['gender'] = gender;
    if (v.dob) body['dob'] = v.dob;
    const address = v.address?.trim();
    if (address) body['address'] = address;
    const ec = v.emergencyContact?.trim();
    if (ec) body['emergencyContact'] = ec;

    this.submitting.set(true);
    this.http
      .post<ApiResponse<AuthResponse>>(`${environment.apiUrl}/api/auth/register`, body)
      .subscribe({
        next: (res) => {
          this.submitting.set(false);
          if (res.success && res.data?.accessToken) {
            this.success.set(true);
            this.serverMessage.set(res.message ?? 'Account created. You can go to your dashboard.');
            const u = this.form.get('username')?.value?.trim() ?? 'Patient';
            this.sessionAuth.setPatientSession(res.data.accessToken, u, res.data.patientId);
          } else {
            this.serverMessage.set(res.message ?? 'Registration failed.');
          }
        },
        error: (err: HttpErrorResponse) => {
          this.submitting.set(false);
          const api = err.error as ApiResponse<Record<string, string> | null> | undefined;
          if (api?.message === 'Validation failed' && api.data && typeof api.data === 'object') {
            const first = Object.values(api.data)[0];
            this.serverMessage.set(first ?? api.message);
            return;
          }
          if (api?.message) {
            this.serverMessage.set(api.message);
            return;
          }
          this.serverMessage.set(friendlyApiError(err, 'Registration could not be completed.'));
        },
      });
  }

  fieldError(field: string): string | null {
    const c = this.form.get(field);
    if (!c?.touched && !c?.dirty) {
      return null;
    }
    if (c.hasError('required')) return 'Required';
    if (c.hasError('email')) return 'Enter a valid email';
    if (c.hasError('minlength')) {
      const min = c.errors?.['minlength']?.requiredLength;
      return `At least ${min} characters`;
    }
    if (c.hasError('maxlength')) return 'Too long';
    return null;
  }

  passwordMismatch(): boolean {
    const g = this.form;
    if (!g.touched && !g.dirty) return false;
    return g.hasError('passwordMismatch');
  }
}
