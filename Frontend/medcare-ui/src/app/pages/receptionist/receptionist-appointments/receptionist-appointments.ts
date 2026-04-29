import { DatePipe } from '@angular/common';
import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';
import { AppointmentApiService, type AppointmentResponseDto } from '../../../core/api/appointment-api.service';
import { DoctorApiService, type DoctorResponseDto } from '../../../core/api/doctor-api.service';
import { PatientApiService, type PatientResponseDto } from '../../../core/api/patient-api.service';

@Component({
  selector: 'app-receptionist-appointments',
  standalone: true,
  imports: [DatePipe, RouterLink],
  templateUrl: './receptionist-appointments.html',
  styleUrl: './receptionist-appointments.scss',
})
export class ReceptionistAppointments implements OnInit {
  private readonly appointmentsApi = inject(AppointmentApiService);
  private readonly doctorsApi = inject(DoctorApiService);
  private readonly patientsApi = inject(PatientApiService);

  readonly rows = signal<AppointmentResponseDto[]>([]);
  readonly query = signal('');
  readonly status = signal('ALL');
  readonly doctorMap = signal<Map<number, string>>(new Map());
  readonly patientMap = signal<Map<number, string>>(new Map());
  readonly error = signal<string | null>(null);
  readonly loading = signal(true);
  readonly cancellingId = signal<number | null>(null);

  readonly sortedRows = computed(() => {
    const q = this.query().trim().toLowerCase();
    const s = this.status();
    const list = this.rows().filter((a) => {
      const patient = this.patientLabel(a.patientId).toLowerCase();
      const doctor = this.doctorLabel(a.doctorId).toLowerCase();
      const matchesQuery = q.length === 0 || patient.includes(q) || doctor.includes(q) || String(a.id).includes(q);
      const matchesStatus = s === 'ALL' || (a.status ?? 'Unknown') === s;
      return matchesQuery && matchesStatus;
    });
    list.sort((a, b) => new Date(b.appointmentTime).getTime() - new Date(a.appointmentTime).getTime());
    return list;
  });
  readonly statuses = computed(() => {
    const set = new Set<string>();
    for (const row of this.rows()) set.add(row.status ?? 'Unknown');
    return [...set];
  });

  ngOnInit(): void {
    this.refresh();
  }

  refresh(): void {
    this.loading.set(true);
    forkJoin({
      appts: this.appointmentsApi.list(0, 500),
      doctors: this.doctorsApi.list(0, 200),
      patients: this.patientsApi.list(0, 500),
    }).subscribe({
      next: ({ appts, doctors, patients }) => {
        this.rows.set(appts.content);
        const dm = new Map<number, string>();
        for (const d of doctors.content) {
          dm.set(d.id, `${d.firstName} ${d.lastName}`.trim());
        }
        this.doctorMap.set(dm);
        const pm = new Map<number, string>();
        for (const p of patients.content) {
          pm.set(p.id, `${p.firstName} ${p.lastName}`.trim());
        }
        this.patientMap.set(pm);
        this.error.set(null);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Could not load appointments.');
        this.loading.set(false);
      },
    });
  }

  doctorLabel(id: number | null): string {
    if (id == null) return '—';
    return this.doctorMap().get(id) ?? `#${id}`;
  }

  patientLabel(id: number | null): string {
    if (id == null) return '—';
    return this.patientMap().get(id) ?? `#${id}`;
  }

  cancel(id: number): void {
    if (!confirm('Cancel this appointment?')) return;
    this.cancellingId.set(id);
    this.appointmentsApi.delete(id).subscribe({
      next: (res) => {
        this.cancellingId.set(null);
        if (res.success) {
          this.refresh();
        } else {
          this.error.set(res.message ?? 'Cancel failed');
        }
      },
      error: (e) => {
        this.cancellingId.set(null);
        this.error.set(e?.error?.message ?? 'Cancel failed');
      },
    });
  }

  setQuery(v: string): void {
    this.query.set(v);
  }

  setStatus(v: string): void {
    this.status.set(v);
  }
}
