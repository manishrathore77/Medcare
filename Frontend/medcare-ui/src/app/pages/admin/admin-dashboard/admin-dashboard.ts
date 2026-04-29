import { Component, inject, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { BaseChartDirective } from 'ng2-charts';
import type { ChartData, ChartOptions } from 'chart.js';
import { forkJoin, of, type Observable } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { friendlyApiError } from '../../../core/http/api-error.util';
import { AppointmentApiService } from '../../../core/api/appointment-api.service';
import { BillingApiService } from '../../../core/api/billing-api.service';
import { ClinicApiService } from '../../../core/api/clinic-api.service';
import { DoctorApiService } from '../../../core/api/doctor-api.service';
import { PatientApiService } from '../../../core/api/patient-api.service';
import { PharmacyApiService } from '../../../core/api/pharmacy-api.service';
import { UserApiService } from '../../../core/api/user-api.service';
import type { PagedResponse } from '../../../core/api/pharmacy-api.service';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [RouterLink, BaseChartDirective],
  templateUrl: './admin-dashboard.html',
  styleUrl: './admin-dashboard.scss',
})
export class AdminDashboard implements OnInit {
  private readonly patientsApi = inject(PatientApiService);
  private readonly doctorsApi = inject(DoctorApiService);
  private readonly usersApi = inject(UserApiService);
  private readonly clinicsApi = inject(ClinicApiService);
  private readonly medicinesApi = inject(PharmacyApiService);
  private readonly appointmentsApi = inject(AppointmentApiService);
  private readonly billingApi = inject(BillingApiService);

  readonly patientCount = signal(0);
  readonly doctorCount = signal(0);
  readonly userCount = signal(0);
  readonly clinicCount = signal(0);
  readonly medicineCount = signal(0);
  readonly appointmentCount = signal(0);
  readonly pendingInvoiceCount = signal(0);
  readonly loadErrors = signal<string[]>([]);
  readonly loading = signal(true);
  readonly appointmentStatusChart = signal<ChartData<'pie'>>({ labels: [], datasets: [{ data: [] }] });
  readonly appointmentTypeChart = signal<ChartData<'pie'>>({ labels: [], datasets: [{ data: [] }] });
  readonly appointmentsPerDayChart = signal<ChartData<'bar'>>({ labels: [], datasets: [{ data: [] }] });
  readonly invoiceStatusChart = signal<ChartData<'pie'>>({ labels: [], datasets: [{ data: [] }] });
  readonly chartOptions: ChartOptions<'pie' | 'bar'> = {
    maintainAspectRatio: false,
    plugins: { legend: { position: 'bottom' } },
    scales: { y: { beginAtZero: true } },
  };

  ngOnInit(): void {
    this.loading.set(true);
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
      users: safe(this.usersApi.list(0, 1), 'Users', emptyPage()),
      clinics: safe(this.clinicsApi.list(0, 1), 'Clinics', emptyPage()),
      medicines: safe(this.medicinesApi.listMedicines(0, 1), 'Medicines', emptyPage()),
      appts: safe(this.appointmentsApi.list(0, 1), 'Appointments', emptyPage()),
      apptsFull: safe(this.appointmentsApi.list(0, 500), 'Appointments chart', emptyPage()),
      invoices: safe(this.billingApi.listInvoices(0, 200), 'Invoices', emptyPage()),
    }).subscribe({
      next: ({ patients, doctors, users, clinics, medicines, appts, apptsFull, invoices }) => {
        this.patientCount.set(patients.totalElements);
        this.doctorCount.set(doctors.totalElements);
        this.userCount.set(users.totalElements);
        this.clinicCount.set(clinics.totalElements);
        this.medicineCount.set(medicines.totalElements);
        this.appointmentCount.set(appts.totalElements);
        const pending = invoices.content.filter((i) => i.status === 'PENDING').length;
        this.pendingInvoiceCount.set(pending);
        this.appointmentStatusChart.set(this.groupPie(apptsFull.content, (a) => a.status ?? 'Unknown'));
        this.appointmentTypeChart.set(this.groupPie(apptsFull.content, (a) => a.appointmentType ?? 'Unknown'));
        this.invoiceStatusChart.set(this.groupPie(invoices.content, (i) => i.status ?? 'Unknown'));
        this.appointmentsPerDayChart.set(this.groupByDay(apptsFull.content.map((a) => a.appointmentTime)));
        this.loading.set(false);
      },
      error: () => {
        this.loadErrors.update((e) => [...e, 'Dashboard']);
        this.loading.set(false);
      },
    });
  }

  private groupPie<T>(rows: T[], pick: (row: T) => string): ChartData<'pie'> {
    const map = new Map<string, number>();
    for (const row of rows) {
      const key = pick(row);
      map.set(key, (map.get(key) ?? 0) + 1);
    }
    return {
      labels: [...map.keys()],
      datasets: [{ data: [...map.values()] }],
    };
  }

  private groupByDay(isoTimes: string[]): ChartData<'bar'> {
    const counts = new Map<string, number>();
    for (const iso of isoTimes) {
      const d = new Date(iso);
      if (Number.isNaN(d.getTime())) continue;
      const key = d.toLocaleDateString(undefined, { month: 'short', day: 'numeric' });
      counts.set(key, (counts.get(key) ?? 0) + 1);
    }
    const labels = [...counts.keys()].slice(-10);
    return {
      labels,
      datasets: [{ label: 'Appointments', data: labels.map((l) => counts.get(l) ?? 0) }],
    };
  }
}
