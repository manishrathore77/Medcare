import { Component, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { PharmacyApiService } from '../../../core/api/pharmacy-api.service';
import { KpiCard } from '../../../shared/kpi-card/kpi-card';

@Component({
  selector: 'app-admin-pharmacy-hub',
  standalone: true,
  imports: [RouterLink, KpiCard],
  templateUrl: './admin-pharmacy-hub.html',
  styleUrl: './admin-pharmacy-hub.scss',
})
export class AdminPharmacyHub {
  private readonly api = inject(PharmacyApiService);
  readonly loadError = signal<string | null>(null);
  readonly skuCount = signal(0);
  readonly lowStock = signal(0);

  constructor() {
    this.api.listMedicines(0, 500).subscribe({
      next: (p) => {
        this.skuCount.set(p.content.length);
        this.lowStock.set(p.content.filter((m) => m.stockQuantity <= (m.reorderLevel ?? 10)).length);
        this.loadError.set(null);
      },
      error: () => this.loadError.set('Sign in with API (admin) and ensure the backend is running.'),
    });
  }
}
