import { Component, inject, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { BaseChartDirective } from 'ng2-charts';
import type { ChartData, ChartOptions } from 'chart.js';
import { AppointmentApiService, type AppointmentResponseDto } from '../../../core/api/appointment-api.service';
import { DoctorPortalApiService } from '../../../core/api/doctor-portal-api.service';

@Component({
  selector: 'app-doctor-dashboard',
  standalone: true,
  imports: [RouterLink, BaseChartDirective],
  templateUrl: './doctor-dashboard.html',
  styleUrl: './doctor-dashboard.scss',
})
export class DoctorDashboard implements OnInit {
  private readonly appointmentsApi = inject(AppointmentApiService);
  private readonly portal = inject(DoctorPortalApiService);

  readonly todayRows = signal<AppointmentResponseDto[]>([]);
  readonly totalCount = signal(0);
  readonly upcomingCount = signal(0);
  readonly patientNames = signal<Record<number, string>>({});
  readonly error = signal<string | null>(null);
  readonly statusChart = signal<ChartData<'pie'>>({ labels: [], datasets: [{ data: [] }] });
  readonly upcomingChart = signal<ChartData<'bar'>>({ labels: [], datasets: [{ data: [] }] });
  readonly chartOptions: ChartOptions<'bar' | 'pie'> = {
    maintainAspectRatio: false,
    plugins: { legend: { position: 'bottom' } },
    scales: { y: { beginAtZero: true } },
  };

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
        this.loadAppointments();
      },
      error: () => this.loadAppointments(),
    });
  }

  private loadAppointments(): void {
    this.appointmentsApi.list(0, 200).subscribe({
      next: (p) => {
        const list = p.content;
        this.totalCount.set(list.length);
        const now = new Date();
        const start = new Date(now.getFullYear(), now.getMonth(), now.getDate()).getTime();
        const end = start + 86400000;
        const today = list.filter((a) => {
          const t = new Date(a.appointmentTime).getTime();
          return t >= start && t < end && a.status !== 'CANCELLED';
        });
        this.todayRows.set(today.sort((a, b) => a.appointmentTime.localeCompare(b.appointmentTime)));
        const up = list.filter(
          (a) =>
            a.status !== 'CANCELLED' && new Date(a.appointmentTime).getTime() >= now.getTime(),
        );
        this.upcomingCount.set(up.length);
        this.statusChart.set(this.groupPie(list.map((a) => a.status ?? 'Unknown')));
        this.upcomingChart.set(this.upcomingByDay(up.map((a) => a.appointmentTime)));
        this.error.set(null);
      },
      error: () => this.error.set('Could not load appointments. Please sign in again and retry.'),
    });
  }

  patientLabel(pid: number | null): string {
    if (pid == null) return '—';
    return this.patientNames()[pid] ?? `Patient #${pid}`;
  }

  formatTime(iso: string): string {
    const d = new Date(iso);
    return Number.isNaN(d.getTime()) ? iso : d.toLocaleTimeString(undefined, { hour: '2-digit', minute: '2-digit' });
  }

  private groupPie(labelsRaw: string[]): ChartData<'pie'> {
    const counts = new Map<string, number>();
    for (const key of labelsRaw) counts.set(key, (counts.get(key) ?? 0) + 1);
    return { labels: [...counts.keys()], datasets: [{ data: [...counts.values()] }] };
  }

  private upcomingByDay(times: string[]): ChartData<'bar'> {
    const counts = new Map<string, number>();
    for (const iso of times) {
      const d = new Date(iso);
      const key = d.toLocaleDateString(undefined, { month: 'short', day: 'numeric' });
      counts.set(key, (counts.get(key) ?? 0) + 1);
    }
    const labels = [...counts.keys()].slice(0, 10);
    return { labels, datasets: [{ label: 'Upcoming', data: labels.map((k) => counts.get(k) ?? 0) }] };
  }
}
