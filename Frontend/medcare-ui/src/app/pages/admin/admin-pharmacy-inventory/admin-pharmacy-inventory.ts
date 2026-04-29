import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import {
  PharmacyApiService,
  type InventoryLogDto,
  type MedicineDto,
} from '../../../core/api/pharmacy-api.service';

@Component({
  selector: 'app-admin-pharmacy-inventory',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './admin-pharmacy-inventory.html',
  styleUrl: './admin-pharmacy-inventory.scss',
})
export class AdminPharmacyInventory {
  private readonly api = inject(PharmacyApiService);
  private readonly fb = inject(FormBuilder);

  readonly rows = signal<InventoryLogDto[]>([]);
  readonly medicines = signal<MedicineDto[]>([]);
  readonly error = signal<string | null>(null);

  readonly form = this.fb.nonNullable.group({
    medicineId: ['', Validators.required],
    changeType: this.fb.nonNullable.control<'IN' | 'OUT'>('IN'),
    quantity: [1, [Validators.required, Validators.min(1)]],
    reason: [''],
  });

  constructor() {
    this.refresh();
    this.api.listMedicines(0, 500).subscribe({
      next: (p) => this.medicines.set(p.content),
      error: () => {},
    });
  }

  refresh(): void {
    this.api.listInventory(0, 200).subscribe({
      next: (p) => {
        this.rows.set(p.content);
        this.error.set(null);
      },
      error: () => this.error.set('Could not load inventory log.'),
    });
  }

  submit(): void {
    if (this.form.invalid) return;
    const v = this.form.getRawValue();
    const mid = Number(v.medicineId);
    if (Number.isNaN(mid)) {
      return;
    }
    this.api
      .createInventoryLog({
        medicineId: mid,
        changeType: v.changeType,
        quantity: v.quantity,
        reason: v.reason || null,
      })
      .subscribe({
        next: (res) => {
          if (res.success) {
            this.form.patchValue({ quantity: 1, reason: '' });
            this.refresh();
          }
        },
        error: () => this.error.set('Could not post movement.'),
      });
  }
}
