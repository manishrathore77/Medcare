import { Component, computed, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { PatientApiService, type PatientResponseDto } from '../../../core/api/patient-api.service';

@Component({
  selector: 'app-admin-patients',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './admin-patients.html',
  styleUrl: './admin-patients.scss',
})
export class AdminPatients {
  private readonly api = inject(PatientApiService);
  readonly rows = signal<PatientResponseDto[]>([]);
  readonly query = signal('');
  readonly gender = signal('ALL');
  readonly error = signal<string | null>(null);
  readonly filteredRows = computed(() => {
    const q = this.query().trim().toLowerCase();
    const g = this.gender();
    return this.rows().filter((p) => {
      const matchesQuery =
        q.length === 0 ||
        `${p.firstName} ${p.lastName}`.toLowerCase().includes(q) ||
        String(p.id).includes(q);
      const matchesGender = g === 'ALL' || (p.gender ?? '').toUpperCase() === g;
      return matchesQuery && matchesGender;
    });
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
      error: () => this.error.set('Could not load patients (sign in with a role that has patient read access).'),
    });
  }

  setQuery(v: string): void {
    this.query.set(v);
  }

  setGender(v: string): void {
    this.gender.set(v);
  }
}
