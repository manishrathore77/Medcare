import { Component, inject, OnInit, signal } from '@angular/core';
import { BaseChartDirective } from 'ng2-charts';
import type { ChartData, ChartOptions } from 'chart.js';
import { forkJoin, type Observable, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { AuditLogApiService } from '../../../core/api/audit-log-api.service';
import { BillingApiService } from '../../../core/api/billing-api.service';
import { UserApiService } from '../../../core/api/user-api.service';
import type { PagedResponse } from '../../../core/api/pharmacy-api.service';
import { friendlyApiError } from '../../../core/http/api-error.util';

@Component({
  selector: 'app-it-support-dashboard',
  standalone: true,
  imports: [BaseChartDirective],
  templateUrl: './it-support-dashboard.html',
  styleUrl: './it-support-dashboard.scss',
})
export class ItSupportDashboard implements OnInit {
  private readonly usersApi = inject(UserApiService);
  private readonly auditApi = inject(AuditLogApiService);
  private readonly billingApi = inject(BillingApiService);

  readonly userCount = signal(0);
  readonly auditCount = signal(0);
  readonly invoiceCount = signal(0);
  readonly paymentCount = signal(0);
  readonly loading = signal(true);
  readonly loadErrors = signal<string[]>([]);
  readonly usersByRoleChart = signal<ChartData<'pie'>>({ labels: [], datasets: [{ data: [] }] });
  readonly auditActionChart = signal<ChartData<'pie'>>({ labels: [], datasets: [{ data: [] }] });
  readonly auditEntityChart = signal<ChartData<'bar'>>({ labels: [], datasets: [{ data: [] }] });
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

    const empty = <T,>(): PagedResponse<T> => ({
      content: [],
      totalElements: 0,
      page: 0,
      size: 0,
      totalPages: 0,
      last: true,
    });

    forkJoin({
      users: safe(this.usersApi.list(0, 500), 'Users', empty()),
      audit: safe(this.auditApi.list(0, 500), 'Audit log', empty()),
      inv: safe(this.billingApi.listInvoices(0, 1), 'Invoices', empty()),
      pay: safe(this.billingApi.listPayments(0, 1), 'Payments', empty()),
    }).subscribe({
      next: ({ users, audit, inv, pay }) => {
        this.userCount.set(users.totalElements);
        this.auditCount.set(audit.totalElements);
        this.invoiceCount.set(inv.totalElements);
        this.paymentCount.set(pay.totalElements);
        this.usersByRoleChart.set(this.groupPie(users.content.map((u) => u.role ?? 'Unknown')));
        this.auditActionChart.set(this.groupPie(audit.content.map((a) => a.action ?? 'Unknown')));
        this.auditEntityChart.set(this.groupBar(audit.content.map((a) => a.entityName ?? 'Unknown')));
        this.loading.set(false);
      },
      error: (err) => {
        this.loadErrors.update((e) => [...e, friendlyApiError(err, 'Dashboard failed.')]);
        this.loading.set(false);
      },
    });
  }

  private groupPie(labelsRaw: string[]): ChartData<'pie'> {
    const counts = new Map<string, number>();
    for (const label of labelsRaw) counts.set(label, (counts.get(label) ?? 0) + 1);
    return { labels: [...counts.keys()], datasets: [{ data: [...counts.values()] }] };
  }

  private groupBar(labelsRaw: string[]): ChartData<'bar'> {
    const counts = new Map<string, number>();
    for (const label of labelsRaw) counts.set(label, (counts.get(label) ?? 0) + 1);
    const labels = [...counts.keys()];
    return { labels, datasets: [{ label: 'Count', data: labels.map((l) => counts.get(l) ?? 0) }] };
  }
}
