import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { ClinicApiService } from '../../../core/api/clinic-api.service';

@Component({
  selector: 'app-admin-clinic-form',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './admin-clinic-form.html',
  styleUrl: './admin-clinic-form.scss',
})
export class AdminClinicForm {
  private readonly fb = inject(FormBuilder);
  private readonly api = inject(ClinicApiService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  readonly error = signal<string | null>(null);
  readonly isCreate = signal(true);
  private clinicId: number | null = null;

  readonly form = this.fb.nonNullable.group({
    name: ['', Validators.required],
    location: [''],
    contactNumber: [''],
  });

  constructor() {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (!idParam || idParam === 'create') {
      this.isCreate.set(true);
      return;
    }
    const id = Number(idParam);
    if (Number.isNaN(id)) {
      this.error.set('Invalid clinic id');
      return;
    }
    this.clinicId = id;
    this.isCreate.set(false);
    this.api.getById(id).subscribe({
      next: (res) => {
        if (!res.success || !res.data) {
          this.error.set(res.message ?? 'Not found');
          return;
        }
        const d = res.data;
        this.form.patchValue({
          name: d.name,
          location: d.location ?? '',
          contactNumber: d.contactNumber ?? '',
        });
      },
      error: () => this.error.set('Could not load clinic.'),
    });
  }

  save(): void {
    if (this.form.invalid) return;
    const v = this.form.getRawValue();
    const body = {
      name: v.name.trim(),
      location: v.location?.trim() || null,
      contactNumber: v.contactNumber?.trim() || null,
    };
    this.error.set(null);
    if (this.isCreate()) {
      this.api.create(body).subscribe({
        next: (res) => {
          if (res.success && res.data?.id != null) {
            void this.router.navigateByUrl(`/admin/clinics/${res.data.id}`);
          } else {
            this.error.set(res.message ?? 'Create failed');
          }
        },
        error: (e) => this.error.set(e?.error?.message ?? 'Create failed'),
      });
      return;
    }
    if (this.clinicId == null) return;
    this.api.update(this.clinicId, body).subscribe({
      next: (res) => {
        if (res.success) {
          void this.router.navigateByUrl('/admin/clinics');
        } else {
          this.error.set(res.message ?? 'Update failed');
        }
      },
      error: (e) => this.error.set(e?.error?.message ?? 'Update failed'),
    });
  }

  remove(): void {
    if (this.isCreate() || this.clinicId == null) return;
    if (!confirm('Delete this clinic? Appointments referencing it may be affected.')) return;
    this.api.delete(this.clinicId).subscribe({
      next: (res) => {
        if (res.success) {
          void this.router.navigateByUrl('/admin/clinics');
        }
      },
      error: () => this.error.set('Delete failed'),
    });
  }
}
