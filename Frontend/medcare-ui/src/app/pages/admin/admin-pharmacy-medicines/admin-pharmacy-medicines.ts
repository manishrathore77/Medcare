import { DecimalPipe } from '@angular/common';
import { Component, computed, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { PharmacyApiService, type MedicineDto } from '../../../core/api/pharmacy-api.service';

@Component({
  selector: 'app-admin-pharmacy-medicines',
  standalone: true,
  imports: [RouterLink, DecimalPipe],
  templateUrl: './admin-pharmacy-medicines.html',
  styleUrl: './admin-pharmacy-medicines.scss',
})
export class AdminPharmacyMedicines {
  private readonly api = inject(PharmacyApiService);
  readonly rows = signal<MedicineDto[]>([]);
  readonly query = signal('');
  readonly error = signal<string | null>(null);
  readonly filteredRows = computed(() => {
    const q = this.query().trim().toLowerCase();
    return this.rows().filter((m) => q.length === 0 || m.name.toLowerCase().includes(q) || String(m.id).includes(q));
  });

  constructor() {
    this.refresh();
  }

  refresh(): void {
    this.api.listMedicines(0, 200).subscribe({
      next: (p) => {
        this.rows.set(p.content);
        this.error.set(null);
      },
      error: () => this.error.set('Could not load medicines right now.'),
    });
  }

  setQuery(v: string): void {
    this.query.set(v);
  }
}
