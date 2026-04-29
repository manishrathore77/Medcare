import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { PatientApiService } from '../../../core/api/patient-api.service';

@Component({
  selector: 'app-admin-patient-form',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './admin-patient-form.html',
  styleUrl: './admin-patient-form.scss',
})
export class AdminPatientForm {
  private readonly fb = inject(FormBuilder);
  private readonly api = inject(PatientApiService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  readonly error = signal<string | null>(null);
  readonly isCreate = signal(true);
  private patientId: number | null = null;

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

  constructor() {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (!idParam || idParam === 'create') {
      this.isCreate.set(true);
      return;
    }
    const id = Number(idParam);
    if (Number.isNaN(id)) {
      this.error.set('Invalid patient id');
      return;
    }
    this.patientId = id;
    this.isCreate.set(false);
    this.api.getById(id).subscribe({
      next: (res) => {
        if (!res.success || !res.data) {
          this.error.set(res.message ?? 'Not found');
          return;
        }
        const d = res.data;
        const dobStr =
          d.dob == null
            ? ''
            : typeof d.dob === 'string'
              ? String(d.dob).slice(0, 10)
              : '';
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
      error: () => this.error.set('Could not load patient.'),
    });
  }

  private bodyFromForm() {
    const v = this.form.getRawValue();
    return {
      firstName: v.firstName.trim(),
      lastName: v.lastName.trim(),
      gender: v.gender?.trim() || null,
      dob: v.dob?.trim() || null,
      address: v.address?.trim() || null,
      emergencyContact: v.emergencyContact?.trim() || null,
      insuranceProvider: v.insuranceProvider?.trim() || null,
      insuranceNumber: v.insuranceNumber?.trim() || null,
    };
  }

  save(): void {
    if (this.form.invalid) return;
    const body = this.bodyFromForm();
    this.error.set(null);
    if (this.isCreate()) {
      this.api.create(body).subscribe({
        next: (res) => {
          if (res.success && res.data?.id != null) {
            void this.router.navigateByUrl(`/admin/patients/${res.data.id}`);
          } else {
            this.error.set(res.message ?? 'Create failed');
          }
        },
        error: (e) => this.error.set(e?.error?.message ?? 'Create failed'),
      });
      return;
    }
    if (this.patientId == null) return;
    this.api.update(this.patientId, body).subscribe({
      next: (res) => {
        if (res.success) {
          void this.router.navigateByUrl('/admin/patients');
        } else {
          this.error.set(res.message ?? 'Update failed');
        }
      },
      error: (e) => this.error.set(e?.error?.message ?? 'Update failed'),
    });
  }

  remove(): void {
    if (this.isCreate() || this.patientId == null) return;
    if (!confirm('Delete this patient record?')) return;
    this.api.delete(this.patientId).subscribe({
      next: (res) => {
        if (res.success) {
          void this.router.navigateByUrl('/admin/patients');
        }
      },
      error: () => this.error.set('Delete failed'),
    });
  }
}
