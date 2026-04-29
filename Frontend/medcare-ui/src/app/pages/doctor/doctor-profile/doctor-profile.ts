import { Component, inject, OnInit, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { SessionAuthService } from '../../../core/auth/session-auth.service';
import { DoctorPortalApiService } from '../../../core/api/doctor-portal-api.service';

@Component({
  selector: 'app-doctor-profile',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './doctor-profile.html',
  styleUrl: './doctor-profile.scss',
})
export class DoctorProfile implements OnInit {
  private readonly fb = inject(FormBuilder);
  protected readonly auth = inject(SessionAuthService);
  private readonly portal = inject(DoctorPortalApiService);

  readonly error = signal<string | null>(null);
  readonly saved = signal(false);
  readonly loading = signal(true);

  readonly form = this.fb.nonNullable.group({
    firstName: ['', Validators.required],
    lastName: ['', Validators.required],
    specialty: [''],
    licenseNumber: [''],
    contactNumber: [''],
    email: [''],
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
        this.form.patchValue({
          firstName: d.firstName,
          lastName: d.lastName,
          specialty: d.specialty ?? '',
          licenseNumber: d.licenseNumber ?? '',
          contactNumber: d.contactNumber ?? '',
          email: d.email ?? '',
        });
      },
      error: () => {
        this.loading.set(false);
        this.error.set('Use API login as doctor (e.g. doctor / doctor123).');
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
        specialty: v.specialty?.trim() || null,
        licenseNumber: v.licenseNumber?.trim() || null,
        contactNumber: v.contactNumber?.trim() || null,
        email: v.email?.trim() || null,
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
