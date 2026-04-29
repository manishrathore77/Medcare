import { DatePipe } from '@angular/common';
import { Component, inject, OnInit, signal } from '@angular/core';
import { forkJoin } from 'rxjs';
import { AppointmentApiService } from '../../../core/api/appointment-api.service';
import { BillingApiService } from '../../../core/api/billing-api.service';
import { LabApiService, type LabReportDto } from '../../../core/api/lab-api.service';
import { PatientApiService } from '../../../core/api/patient-api.service';

@Component({
  selector: 'app-receptionist-reports',
  standalone: true,
  imports: [DatePipe],
  templateUrl: './receptionist-reports.html',
  styleUrl: './receptionist-reports.scss',
})
export class ReceptionistReports implements OnInit {
  private readonly appointmentsApi = inject(AppointmentApiService);
  private readonly patientsApi = inject(PatientApiService);
  private readonly billingApi = inject(BillingApiService);
  private readonly labApi = inject(LabApiService);

  readonly totalPatients = signal(0);
  readonly pendingAppts = signal(0);
  readonly confirmedAppts = signal(0);
  readonly openInvoices = signal(0);
  readonly patients = signal<{ id: number; label: string }[]>([]);
  readonly selectedPatientId = signal<number | null>(null);
  readonly selectedFile = signal<File | null>(null);
  readonly reports = signal<LabReportDto[]>([]);
  readonly uploading = signal(false);
  readonly uploadMessage = signal<string | null>(null);
  readonly error = signal<string | null>(null);
  readonly loading = signal(true);

  ngOnInit(): void {
    this.loading.set(true);
    forkJoin({
      appts: this.appointmentsApi.list(0, 500),
      patients: this.patientsApi.list(0, 500),
      inv: this.billingApi.listInvoices(0, 200),
      reportList: this.labApi.listReports(0, 200),
    }).subscribe({
      next: ({ appts, patients, inv, reportList }) => {
        this.totalPatients.set(patients.totalElements);
        this.patients.set(
          patients.content.map((p) => ({ id: p.id, label: `${p.firstName} ${p.lastName}` })),
        );
        this.reports.set(reportList.content);
        let p = 0;
        let c = 0;
        for (const a of appts.content) {
          if (a.status === 'PENDING') p += 1;
          if (a.status === 'CONFIRMED') c += 1;
        }
        this.pendingAppts.set(p);
        this.confirmedAppts.set(c);
        const open = inv.content.filter((i) => i.status === 'PENDING').length;
        this.openInvoices.set(open);
        this.error.set(null);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Could not load report data.');
        this.loading.set(false);
      },
    });
  }

  setPatient(patientIdRaw: string): void {
    const id = Number(patientIdRaw);
    this.selectedPatientId.set(Number.isNaN(id) ? null : id);
  }

  setFile(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.selectedFile.set(input.files?.[0] ?? null);
  }

  upload(): void {
    const patientId = this.selectedPatientId();
    const file = this.selectedFile();
    if (patientId == null || file == null) {
      this.uploadMessage.set('Choose patient and file first.');
      return;
    }
    this.uploading.set(true);
    this.labApi.uploadReport(patientId, file).subscribe({
      next: (res) => {
        this.uploading.set(false);
        this.uploadMessage.set(res.success ? 'Report uploaded.' : (res.message ?? 'Upload failed.'));
        this.reloadReports();
      },
      error: () => {
        this.uploading.set(false);
        this.uploadMessage.set('Upload failed.');
      },
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
      error: () => {
        this.uploadMessage.set('Download failed.');
      },
    });
  }

  private reloadReports(): void {
    this.labApi.listReports(0, 200).subscribe({
      next: (p) => this.reports.set(p.content),
      error: () => {},
    });
  }
}
