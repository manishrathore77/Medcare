import { Component, inject, OnInit, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { SessionAuthService } from '../../../core/auth/session-auth.service';
import { PatientPortalApiService } from '../../../core/api/patient-portal-api.service';

@Component({
  selector: 'app-patient-profile',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './patient-profile.html',
  styleUrl: './patient-profile.scss',
})
export class PatientProfile implements OnInit {
  private readonly fb = inject(FormBuilder);
  protected readonly auth = inject(SessionAuthService);
  private readonly portal = inject(PatientPortalApiService);

  readonly error = signal<string | null>(null);
  readonly saved = signal(false);
  readonly loading = signal(true);

  readonly form = this.fb.nonNullable.group({
    firstName: ['', Validators.required],
    lastName: ['', Validators.required],
    gender: [''],
    dob: [''],
    address: [''],
    emergencyContact: [''],
    insuranceProvider: [''],
    insuranceNumber: [''],
  });

  ngOnInit(): void {
    this.portal.getMe().subscribe({
      next: (res) => {
        this.loading.set(false);
        if (!res.success || !res.data) {
          this.error.set(res.message ?? 'Could not load profile.');
          return;
        }
        const d = res.data;
        const dobStr =
          d.dob == null ? '' : typeof d.dob === 'string' ? String(d.dob).slice(0, 10) : '';
        this.form.patchValue({
          firstName: d.firstName,
          lastName: d.lastName,
          gender: d.gender ?? '',
          dob: dobStr,
          address: d.address ?? '',
          emergencyContact: d.emergencyContact ?? '',
          insuranceProvider: d.insuranceProvider ?? '',
          insuranceNumber: d.insuranceNumber ?? '',
        });
      },
      error: () => {
        this.loading.set(false);
        this.error.set('API error. Use API login as a registered patient.');
      },
    });
  }

  save(): void {
    this.error.set(null);
    this.saved.set(false);
    if (this.form.invalid) return;
    const v = this.form.getRawValue();
    this.portal
      .updateMe({
        firstName: v.firstName.trim(),
        lastName: v.lastName.trim(),
        gender: v.gender?.trim() || null,
        dob: v.dob?.trim() || null,
        address: v.address?.trim() || null,
        emergencyContact: v.emergencyContact?.trim() || null,
        insuranceProvider: v.insuranceProvider?.trim() || null,
        insuranceNumber: v.insuranceNumber?.trim() || null,
      })
      .subscribe({
        next: (res) => {
          if (res.success) {
            this.saved.set(true);
          } else {
            this.error.set(res.message ?? 'Update failed');
          }
        },
        error: (e) => this.error.set(e?.error?.message ?? 'Update failed'),
      });
  }
}
