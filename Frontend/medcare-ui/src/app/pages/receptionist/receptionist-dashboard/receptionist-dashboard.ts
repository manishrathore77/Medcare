import { DecimalPipe } from '@angular/common';
import { Component, inject, OnInit, signal } from '@angular/core';
import { BaseChartDirective } from 'ng2-charts';
import type { ChartData, ChartOptions } from 'chart.js';
import { forkJoin, type Observable, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { AppointmentApiService } from '../../../core/api/appointment-api.service';
import { BillingApiService } from '../../../core/api/billing-api.service';
import { DoctorApiService } from '../../../core/api/doctor-api.service';
import { PatientApiService } from '../../../core/api/patient-api.service';
import type { PagedResponse } from '../../../core/api/pharmacy-api.service';
import { friendlyApiError } from '../../../core/http/api-error.util';

@Component({
  selector: 'app-receptionist-dashboard',
  standalone: true,
  imports: [DecimalPipe, BaseChartDirective],
  templateUrl: './receptionist-dashboard.html',
  styleUrl: './receptionist-dashboard.scss',
})
export class ReceptionistDashboard implements OnInit {
  private readonly patientsApi = inject(PatientApiService);
  private readonly doctorsApi = inject(DoctorApiService);
  private readonly appointmentsApi = inject(AppointmentApiService);
  private readonly billingApi = inject(BillingApiService);

  readonly totalPatients = signal(0);
  readonly todayAppointments = signal(0);
  readonly doctorCount = signal(0);
  /** Sum of payment amounts recorded today (when paidAt is set). */
  readonly revenueToday = signal(0);
  readonly loading = signal(true);
  readonly loadErrors = signal<string[]>([]);
  readonly appointmentDistChart = signal<ChartData<'bar'>>({ labels: [], datasets: [{ data: [] }] });
  readonly paymentModeChart = signal<ChartData<'pie'>>({ labels: [], datasets: [{ data: [] }] });
  readonly paymentStatusChart = signal<ChartData<'pie'>>({ labels: [], datasets: [{ data: [] }] });
  readonly chartOptions: ChartOptions<'bar' | 'pie'> = {
    maintainAspectRatio: false,
    plugins: { legend: { position: 'bottom' } },
    scales: { y: { beginAtZero: true } },
  };

  ngOnInit(): void {
    this.loading.set(true);
    this.loadErrors.set([]);

    const safe = <T,>(obs: Observable<T>, label: string, fallback: T) =>
      obs.pipe(
        catchError((err) => {
          this.loadErrors.update((e) => [...e, `${label}: ${friendlyApiError(err, 'Could not load data.')}`]);
          return of(fallback);
        }),
      );

    const emptyPage = <T,>(): PagedResponse<T> => ({
      content: [],
      totalElements: 0,
      page: 0,
      size: 0,
      totalPages: 0,
      last: true,
    });

    forkJoin({
      patients: safe(this.patientsApi.list(0, 1), 'Patients', emptyPage()),
      doctors: safe(this.doctorsApi.list(0, 1), 'Doctors', emptyPage()),
      appts: safe(this.appointmentsApi.list(0, 500), 'Appointments', emptyPage()),
      payments: safe(this.billingApi.listPayments(0, 200), 'Payments', emptyPage()),
    }).subscribe({
      next: ({ patients, doctors, appts, payments }) => {
        this.totalPatients.set(patients.totalElements);
        this.doctorCount.set(doctors.totalElements);
        const now = new Date();
        const y = now.getFullYear();
        const m = now.getMonth();
        const day = now.getDate();
        const today = appts.content.filter((a) => {
          const t = new Date(a.appointmentTime);
          return t.getFullYear() === y && t.getMonth() === m && t.getDate() === day;
        });
        this.todayAppointments.set(today.length);
        let rev = 0;
        for (const p of payments.content) {
          if (!p.paidAt || p.amount == null) continue;
          const pd = new Date(p.paidAt);
          if (pd.getFullYear() === y && pd.getMonth() === m && pd.getDate() === day) {
            rev += p.amount;
          }
        }
        this.revenueToday.set(rev);
        this.appointmentDistChart.set(this.byUpcomingDays(appts.content));
        this.paymentModeChart.set(this.groupPie(payments.content, (p) => p.paymentMode ?? 'Unknown'));
        this.paymentStatusChart.set(this.groupPie(payments.content, (p) => p.paymentStatus ?? 'Unknown'));
        this.loading.set(false);
      },
      error: (err) => {
        this.loadErrors.update((e) => [...e, friendlyApiError(err, 'Dashboard failed.')]);
        this.loading.set(false);
      },
    });
  }

  private groupPie<T>(rows: T[], pick: (row: T) => string): ChartData<'pie'> {
    const counts = new Map<string, number>();
    for (const row of rows) {
      const k = pick(row);
      counts.set(k, (counts.get(k) ?? 0) + 1);
    }
    return { labels: [...counts.keys()], datasets: [{ data: [...counts.values()] }] };
  }

  private byUpcomingDays(rows: { appointmentTime: string }[]): ChartData<'bar'> {
    const start = new Date();
    const labels: string[] = [];
    const dayKeys: string[] = [];
    for (let i = 0; i < 7; i += 1) {
      const d = new Date(start);
      d.setDate(start.getDate() + i);
      labels.push(d.toLocaleDateString(undefined, { weekday: 'short' }));
      dayKeys.push(`${d.getFullYear()}-${d.getMonth()}-${d.getDate()}`);
    }
    const counts = new Map(dayKeys.map((k) => [k, 0]));
    for (const row of rows) {
      const d = new Date(row.appointmentTime);
      const key = `${d.getFullYear()}-${d.getMonth()}-${d.getDate()}`;
      if (counts.has(key)) counts.set(key, (counts.get(key) ?? 0) + 1);
    }
    return {
      labels,
      datasets: [{ label: 'Appointments', data: dayKeys.map((k) => counts.get(k) ?? 0) }],
    };
  }
}
