import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { PharmacyApiService } from '../../../core/api/pharmacy-api.service';

@Component({
  selector: 'app-admin-pharmacy-medicine-form',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './admin-pharmacy-medicine-form.html',
  styleUrl: './admin-pharmacy-medicine-form.scss',
})
export class AdminPharmacyMedicineForm {
  private readonly fb = inject(FormBuilder);
  private readonly api = inject(PharmacyApiService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  readonly error = signal<string | null>(null);
  readonly isCreate = signal(true);
  private medicineId: number | null = null;

  readonly form = this.fb.nonNullable.group({
    name: ['', Validators.required],
    batchNo: [''],
    expiryDate: [''],
    stockQuantity: [0, [Validators.required, Validators.min(0)]],
    reorderLevel: [10, [Validators.required, Validators.min(0)]],
    unitPrice: [0, [Validators.required, Validators.min(0)]],
  });

  constructor() {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (!idParam || idParam === 'create') {
      this.isCreate.set(true);
      return;
    }
    const id = Number(idParam);
    if (Number.isNaN(id)) {
      this.error.set('Invalid medicine id');
      return;
    }
    this.medicineId = id;
    this.isCreate.set(false);
    this.api.getMedicine(id).subscribe({
      next: (res) => {
        if (!res.success || !res.data) {
          this.error.set(res.message ?? 'Not found');
          return;
        }
        const d = res.data;
        this.form.patchValue({
          name: d.name,
          batchNo: d.batchNo ?? '',
          expiryDate: d.expiryDate ?? '',
          stockQuantity: d.stockQuantity ?? 0,
          reorderLevel: d.reorderLevel ?? 10,
          unitPrice: d.unitPrice ?? 0,
        });
      },
      error: () => this.error.set('Could not load medicine.'),
    });
  }

  save(): void {
    if (this.form.invalid) return;
    const v = this.form.getRawValue();
    const body = {
      name: v.name,
      batchNo: v.batchNo,
      expiryDate: v.expiryDate || null,
      stockQuantity: v.stockQuantity,
      reorderLevel: v.reorderLevel,
      unitPrice: v.unitPrice,
    };
    this.error.set(null);
    if (this.isCreate()) {
      this.api.createMedicine(body).subscribe({
        next: (res) => {
          if (res.success && res.data?.id) {
            void this.router.navigateByUrl(`/admin/pharmacy/medicines/${res.data.id}`);
          } else {
            this.error.set(res.message ?? 'Create failed');
          }
        },
        error: (e) => this.error.set(e?.error?.message ?? 'Create failed'),
      });
      return;
    }
    if (this.medicineId == null) return;
    this.api.updateMedicine(this.medicineId, body).subscribe({
      next: (res) => {
        if (res.success) {
          void this.router.navigateByUrl('/admin/pharmacy/medicines');
        } else {
          this.error.set(res.message ?? 'Update failed');
        }
      },
      error: (e) => this.error.set(e?.error?.message ?? 'Update failed'),
    });
  }

  remove(): void {
    if (this.isCreate() || this.medicineId == null) return;
    if (!confirm('Delete this medicine?')) return;
    this.api.deleteMedicine(this.medicineId).subscribe({
      next: (res) => {
        if (res.success) {
          void this.router.navigateByUrl('/admin/pharmacy/medicines');
        }
      },
      error: () => this.error.set('Delete failed'),
    });
  }
}
