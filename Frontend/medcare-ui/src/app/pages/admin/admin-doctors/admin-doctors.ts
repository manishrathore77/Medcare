import { Component, computed, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { DoctorApiService, type DoctorResponseDto } from '../../../core/api/doctor-api.service';

@Component({
  selector: 'app-admin-doctors',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './admin-doctors.html',
  styleUrl: './admin-doctors.scss',
})
export class AdminDoctors {
  private readonly api = inject(DoctorApiService);
  readonly rows = signal<DoctorResponseDto[]>([]);
  readonly query = signal('');
  readonly specialty = signal('ALL');
  readonly error = signal<string | null>(null);
  readonly filteredRows = computed(() => {
    const q = this.query().trim().toLowerCase();
    const s = this.specialty();
    return this.rows().filter((d) => {
      const name = `${d.firstName} ${d.lastName}`.toLowerCase();
      const matchesQuery = q.length === 0 || name.includes(q) || String(d.id).includes(q);
      const matchesSpecialty = s === 'ALL' || (d.specialty ?? 'Unknown') === s;
      return matchesQuery && matchesSpecialty;
    });
  });
  readonly specialties = computed(() => {
    const values = new Set<string>();
    for (const row of this.rows()) values.add(row.specialty ?? 'Unknown');
    return [...values];
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
      error: () => this.error.set('Could not load doctors (sign in with a role that has doctor read access).'),
    });
  }

  setQuery(v: string): void {
    this.query.set(v);
  }

  setSpecialty(v: string): void {
    this.specialty.set(v);
  }
}
