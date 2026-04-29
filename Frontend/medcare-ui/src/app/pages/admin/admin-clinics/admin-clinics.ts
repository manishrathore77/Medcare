import { Component, computed, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ClinicApiService, type ClinicDto } from '../../../core/api/clinic-api.service';

@Component({
  selector: 'app-admin-clinics',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './admin-clinics.html',
  styleUrl: './admin-clinics.scss',
})
export class AdminClinics {
  private readonly api = inject(ClinicApiService);
  readonly rows = signal<ClinicDto[]>([]);
  readonly query = signal('');
  readonly error = signal<string | null>(null);
  readonly filteredRows = computed(() => {
    const q = this.query().trim().toLowerCase();
    return this.rows().filter((c) => q.length === 0 || c.name.toLowerCase().includes(q) || String(c.id).includes(q));
  });

  constructor() {
    this.refresh();
  }

  refresh(): void {
    this.api.list(0, 200).subscribe({
      next: (p) => {
        this.rows.set(p.content);
        this.error.set(null);
      },
      error: () =>
        this.error.set('Could not load clinics. Sign in as administrator with a JWT (MEDCARE_CLINICS_READ).'),
    });
  }

  setQuery(v: string): void {
    this.query.set(v);
  }
}
