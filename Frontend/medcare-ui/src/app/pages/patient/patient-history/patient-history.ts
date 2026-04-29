import { Component, inject, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AppointmentApiService, type AppointmentResponseDto } from '../../../core/api/appointment-api.service';
import { ClinicApiService } from '../../../core/api/clinic-api.service';
import { DoctorApiService, type DoctorResponseDto } from '../../../core/api/doctor-api.service';

@Component({
  selector: 'app-patient-history',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './patient-history.html',
  styleUrl: './patient-history.scss',
})
export class PatientHistory implements OnInit {
  private readonly appointmentsApi = inject(AppointmentApiService);
  private readonly doctorsApi = inject(DoctorApiService);
  private readonly clinicsApi = inject(ClinicApiService);

  readonly rows = signal<AppointmentResponseDto[]>([]);
  readonly error = signal<string | null>(null);
  readonly doctorNames = signal<Record<number, string>>({});
  readonly clinicNames = signal<Record<number, string>>({});

  ngOnInit(): void {
    this.refresh();
  }

  refresh(): void {
    this.appointmentsApi.list(0, 100).subscribe({
      next: (p) => {
        this.rows.set(p.content);
        this.error.set(null);
        this.loadLabels(p.content);
      },
      error: () => this.error.set('Could not load appointments. Sign in with API as a patient.'),
    });
  }

  private loadLabels(apps: AppointmentResponseDto[]): void {
    const docIds = new Set<number>();
    const clinIds = new Set<number>();
    for (const a of apps) {
      if (a.doctorId != null) docIds.add(a.doctorId);
      if (a.clinicId != null) clinIds.add(a.clinicId);
    }
    if (docIds.size === 0 && clinIds.size === 0) return;

    this.doctorsApi.list(0, 200).subscribe({
      next: (d) => {
        const map: Record<number, string> = {};
        for (const x of d.content) {
          map[x.id] = `${x.firstName} ${x.lastName}`;
        }
        this.doctorNames.set(map);
      },
      error: () => {},
    });
    this.clinicsApi.list(0, 200).subscribe({
      next: (c) => {
        const map: Record<number, string> = {};
        for (const x of c.content) {
          map[x.id] = x.name;
        }
        this.clinicNames.set(map);
      },
      error: () => {},
    });
  }

  formatWhen(iso: string): { date: string; time: string } {
    const d = new Date(iso);
    if (Number.isNaN(d.getTime())) return { date: iso, time: '' };
    return {
      date: d.toLocaleDateString(undefined, { year: 'numeric', month: 'short', day: 'numeric' }),
      time: d.toLocaleTimeString(undefined, { hour: '2-digit', minute: '2-digit' }),
    };
  }

  doctorLabel(id: number | null): string {
    if (id == null) return '—';
    return this.doctorNames()[id] ?? `Doctor #${id}`;
  }

  clinicLabel(id: number | null): string {
    if (id == null) return '—';
    return this.clinicNames()[id] ?? `Clinic #${id}`;
  }

  cancel(a: AppointmentResponseDto): void {
    if (a.status === 'CANCELLED') return;
    if (!confirm('Cancel this appointment?')) return;
    this.appointmentsApi.delete(a.id).subscribe({
      next: (res) => {
        if (res.success) this.refresh();
      },
      error: () => this.error.set('Could not cancel.'),
    });
  }
}
