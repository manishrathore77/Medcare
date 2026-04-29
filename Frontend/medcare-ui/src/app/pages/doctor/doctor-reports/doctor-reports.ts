import { DatePipe } from '@angular/common';
import { Component, inject, OnInit, signal } from '@angular/core';
import { LabApiService, type LabReportDto } from '../../../core/api/lab-api.service';

@Component({
  selector: 'app-doctor-reports',
  standalone: true,
  imports: [DatePipe],
  templateUrl: './doctor-reports.html',
  styleUrl: './doctor-reports.scss',
})
export class DoctorReports implements OnInit {
  private readonly labApi = inject(LabApiService);

  readonly rows = signal<LabReportDto[]>([]);
  readonly error = signal<string | null>(null);

  ngOnInit(): void {
    this.labApi.listReports(0, 200).subscribe({
      next: (p) => {
        this.rows.set(p.content);
        this.error.set(null);
      },
      error: () => this.error.set('Could not load lab reports.'),
    });
  }

  download(report: LabReportDto): void {
    this.labApi.downloadReport(report.id).subscribe({
      next: (blob) => {
        const fileName = report.fileName ?? `lab-report-${report.id}`;
        const url = URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = fileName;
        a.click();
        URL.revokeObjectURL(url);
      },
      error: () => this.error.set('Download failed.'),
    });
  }
}
