import { Component, inject, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { BaseChartDirective } from 'ng2-charts';
import type { ChartData, ChartOptions } from 'chart.js';
import { AppointmentApiService, type AppointmentResponseDto } from '../../../core/api/appointment-api.service';
import { DoctorApiService } from '../../../core/api/doctor-api.service';
import { PatientPortalApiService } from '../../../core/api/patient-portal-api.service';
import { KpiCard } from '../../../shared/kpi-card/kpi-card';

@Component({
  selector: 'app-patient-dashboard',
  standalone: true,
  imports: [RouterLink, KpiCard, BaseChartDirective],
  templateUrl: './patient-dashboard.html',
  styleUrl: './patient-dashboard.scss',
})
export class PatientDashboard implements OnInit {
  private readonly appointmentsApi = inject(AppointmentApiService);
  private readonly doctorsApi = inject(DoctorApiService);
  private readonly portalApi = inject(PatientPortalApiService);

  readonly rows = signal<AppointmentResponseDto[]>([]);
  readonly doctorNames = signal<Record<number, string>>({});
  readonly upcomingCount = signal(0);
  readonly rxCount = signal(0);
  readonly reportCount = signal(0);
  readonly error = signal<string | null>(null);
  readonly statusChart = signal<ChartData<'pie'>>({ labels: [], datasets: [{ data: [] }] });
  readonly timelineChart = signal<ChartData<'bar'>>({ labels: [], datasets: [{ data: [] }] });
  readonly typeChart = signal<ChartData<'pie'>>({ labels: [], datasets: [{ data: [] }] });
  readonly chartOptions: ChartOptions<'bar' | 'pie'> = {
    maintainAspectRatio: false,
    plugins: { legend: { position: 'bottom' } },
    scales: { y: { beginAtZero: true } },
  };

  ngOnInit(): void {
    this.appointmentsApi.list(0, 50).subscribe({
      next: (p) => {
        const list = p.content;
        this.rows.set(list);
        const now = Date.now();
        const upcoming = list.filter((a) => {
          if (!a.appointmentTime || a.status === 'CANCELLED') return false;
          const t = new Date(a.appointmentTime).getTime();
          if (t < now) return false;
          return a.status === 'CONFIRMED' || a.status === 'PENDING';
        });
        this.upcomingCount.set(upcoming.length);
        this.statusChart.set(this.groupPie(list.map((a) => a.status ?? 'Unknown')));
        this.typeChart.set(this.groupPie(list.map((a) => a.appointmentType ?? 'Unknown')));
        this.timelineChart.set(this.byTimeline(list.map((a) => a.appointmentTime)));
        this.error.set(null);
        this.loadDoctorNames(list);
      },
      error: () => this.error.set('Could not load appointments.'),
    });

    this.portalApi.listMyPrescriptions().subscribe({
      next: (res) => {
        if (res.success && res.data) {
          this.rxCount.set(res.data.length);
        }
      },
      error: () => {},
    });
  }

  private loadDoctorNames(apps: AppointmentResponseDto[]): void {
    const ids = new Set<number>();
    for (const a of apps) {
      if (a.doctorId != null) ids.add(a.doctorId);
    }
    if (ids.size === 0) return;
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
  }

  doctorLabel(id: number | null): string {
    if (id == null) return '—';
    return this.doctorNames()[id] ?? `Doctor #${id}`;
  }

  formatWhen(iso: string): { date: string; time: string } {
    const d = new Date(iso);
    if (Number.isNaN(d.getTime())) return { date: iso, time: '' };
    return {
      date: d.toLocaleDateString(undefined, { month: 'short', day: 'numeric' }),
      time: d.toLocaleTimeString(undefined, { hour: '2-digit', minute: '2-digit' }),
    };
  }

  private groupPie(labelsRaw: string[]): ChartData<'pie'> {
    const map = new Map<string, number>();
    for (const label of labelsRaw) map.set(label, (map.get(label) ?? 0) + 1);
    return { labels: [...map.keys()], datasets: [{ data: [...map.values()] }] };
  }

  private byTimeline(isoRows: string[]): ChartData<'bar'> {
    const map = new Map<string, number>();
    for (const iso of isoRows) {
      const d = new Date(iso);
      const key = d.toLocaleDateString(undefined, { month: 'short', day: 'numeric' });
      map.set(key, (map.get(key) ?? 0) + 1);
    }
    const labels = [...map.keys()].slice(-10);
    return { labels, datasets: [{ label: 'Appointments', data: labels.map((k) => map.get(k) ?? 0) }] };
  }

}
