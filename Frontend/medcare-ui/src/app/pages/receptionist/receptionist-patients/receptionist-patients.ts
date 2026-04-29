import { Component, computed, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { PatientApiService, type PatientResponseDto } from '../../../core/api/patient-api.service';

@Component({
  selector: 'app-receptionist-patients',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './receptionist-patients.html',
  styleUrl: './receptionist-patients.scss',
})
export class ReceptionistPatients {
  private readonly api = inject(PatientApiService);
  readonly rows = signal<PatientResponseDto[]>([]);
  readonly query = signal('');
  readonly gender = signal('ALL');
  readonly error = signal<string | null>(null);
  readonly filteredRows = computed(() => {
    const q = this.query().trim().toLowerCase();
    const g = this.gender();
    return this.rows().filter((p) => {
      const queryMatch =
        q.length === 0 ||
        `${p.firstName} ${p.lastName}`.toLowerCase().includes(q) ||
        String(p.id).includes(q);
      const genderMatch = g === 'ALL' || (p.gender ?? '').toUpperCase() === g;
      return queryMatch && genderMatch;
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
      error: () =>
        this.error.set('Could not load patients. Please retry in a moment.'),
    });
  }

  setQuery(v: string): void {
    this.query.set(v);
  }

  setGender(v: string): void {
    this.gender.set(v);
  }
}
