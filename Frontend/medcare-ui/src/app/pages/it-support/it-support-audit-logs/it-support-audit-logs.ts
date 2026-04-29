import { Component, inject, signal } from '@angular/core';
import { AuditLogApiService, type AuditLogDto } from '../../../core/api/audit-log-api.service';

@Component({
  selector: 'app-it-support-audit-logs',
  standalone: true,
  templateUrl: './it-support-audit-logs.html',
  styleUrl: './it-support-audit-logs.scss',
})
export class ItSupportAuditLogs {
  private readonly api = inject(AuditLogApiService);

  readonly rows = signal<AuditLogDto[]>([]);
  readonly error = signal<string | null>(null);
  readonly loading = signal(true);
  readonly deletingId = signal<number | null>(null);

  constructor() {
    this.refresh();
  }

  refresh(): void {
    this.loading.set(true);
    this.api.list(0, 200).subscribe({
      next: (p) => {
        this.rows.set(p.content);
        this.error.set(null);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Could not load audit logs (MEDCARE_AUDIT_LOGS_READ required).');
        this.loading.set(false);
      },
    });
  }

  remove(row: AuditLogDto): void {
    if (!confirm(`Permanently delete audit row #${row.id}?`)) return;
    this.deletingId.set(row.id);
    this.api.delete(row.id).subscribe({
      next: (res) => {
        this.deletingId.set(null);
        if (res.success) {
          this.refresh();
        } else {
          this.error.set(res.message ?? 'Delete failed');
        }
      },
      error: (e) => {
        this.deletingId.set(null);
        this.error.set(e?.error?.message ?? 'Delete failed');
      },
    });
  }
}
