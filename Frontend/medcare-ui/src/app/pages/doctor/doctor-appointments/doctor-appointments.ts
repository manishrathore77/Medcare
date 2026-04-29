import { Component, inject, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AppointmentApiService, type AppointmentResponseDto } from '../../../core/api/appointment-api.service';
import { DoctorPortalApiService } from '../../../core/api/doctor-portal-api.service';

@Component({
  selector: 'app-doctor-appointments',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './doctor-appointments.html',
  styleUrl: './doctor-appointments.scss',
})
export class DoctorAppointments implements OnInit {
  private readonly appointmentsApi = inject(AppointmentApiService);
  private readonly portal = inject(DoctorPortalApiService);

  readonly rows = signal<AppointmentResponseDto[]>([]);
  readonly patientNames = signal<Record<number, string>>({});
  readonly error = signal<string | null>(null);

  ngOnInit(): void {
    this.portal.listMyPatients().subscribe({
      next: (res) => {
        if (res.success && res.data) {
          const map: Record<number, string> = {};
          for (const p of res.data) {
            map[p.id] = `${p.firstName} ${p.lastName}`;
          }
          this.patientNames.set(map);
        }
        this.loadAppts();
      },
      error: () => this.loadAppts(),
    });
  }

  private loadAppts(): void {
    this.appointmentsApi.list(0, 200).subscribe({
      next: (p) => {
        const list = [...p.content].sort((a, b) => a.appointmentTime.localeCompare(b.appointmentTime));
        this.rows.set(list);
        this.error.set(null);
      },
      error: () => this.error.set('Could not load appointments.'),
    });
  }

  patientLabel(pid: number | null): string {
    if (pid == null) return '—';
    return this.patientNames()[pid] ?? `Patient #${pid}`;
  }

  formatWhen(iso: string): { date: string; time: string } {
    const d = new Date(iso);
    if (Number.isNaN(d.getTime())) return { date: iso, time: '' };
    return {
      date: d.toLocaleDateString(undefined, { weekday: 'short', month: 'short', day: 'numeric' }),
      time: d.toLocaleTimeString(undefined, { hour: '2-digit', minute: '2-digit' }),
    };
  }

  cancel(a: AppointmentResponseDto): void {
    if (a.status === 'CANCELLED') return;
    if (!confirm('Cancel this appointment?')) return;
    this.appointmentsApi.delete(a.id).subscribe({
      next: (res) => {
        if (res.success) this.loadAppts();
      },
      error: () => this.error.set('Cancel failed'),
    });
  }
}
