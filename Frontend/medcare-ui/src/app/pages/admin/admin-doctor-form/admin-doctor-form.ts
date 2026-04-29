import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { DoctorApiService } from '../../../core/api/doctor-api.service';

@Component({
  selector: 'app-admin-doctor-form',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './admin-doctor-form.html',
  styleUrl: './admin-doctor-form.scss',
})
export class AdminDoctorForm {
  private readonly fb = inject(FormBuilder);
  private readonly api = inject(DoctorApiService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  readonly error = signal<string | null>(null);
  readonly isCreate = signal(true);
  private doctorId: number | null = null;

  readonly form = this.fb.nonNullable.group({
    firstName: ['', Validators.required],
    lastName: ['', Validators.required],
    specialty: [''],
    licenseNumber: [''],
    contactNumber: [''],
    email: [''],
  });

  constructor() {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (!idParam || idParam === 'create') {
      this.isCreate.set(true);
      return;
    }
    const id = Number(idParam);
    if (Number.isNaN(id)) {
      this.error.set('Invalid doctor id');
      return;
    }
    this.doctorId = id;
    this.isCreate.set(false);
    this.api.getById(id).subscribe({
      next: (res) => {
        if (!res.success || !res.data) {
          this.error.set(res.message ?? 'Not found');
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
      error: () => this.error.set('Could not load doctor.'),
    });
  }

  private bodyFromForm() {
    const v = this.form.getRawValue();
    return {
      firstName: v.firstName.trim(),
      lastName: v.lastName.trim(),
      specialty: v.specialty?.trim() || null,
      licenseNumber: v.licenseNumber?.trim() || null,
      contactNumber: v.contactNumber?.trim() || null,
      email: v.email?.trim() || null,
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
            void this.router.navigateByUrl(`/admin/doctors/${res.data.id}`);
          } else {
            this.error.set(res.message ?? 'Create failed');
          }
        },
        error: (e) => this.error.set(e?.error?.message ?? 'Create failed'),
      });
      return;
    }
    if (this.doctorId == null) return;
    this.api.update(this.doctorId, body).subscribe({
      next: (res) => {
        if (res.success) {
          void this.router.navigateByUrl('/admin/doctors');
        } else {
          this.error.set(res.message ?? 'Update failed');
        }
      },
      error: (e) => this.error.set(e?.error?.message ?? 'Update failed'),
    });
  }

  remove(): void {
    if (this.isCreate() || this.doctorId == null) return;
    if (!confirm('Delete this doctor?')) return;
    this.api.delete(this.doctorId).subscribe({
      next: (res) => {
        if (res.success) {
          void this.router.navigateByUrl('/admin/doctors');
        }
      },
      error: () => this.error.set('Delete failed'),
    });
  }
}
