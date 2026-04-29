import { Component, inject, OnInit, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { AppointmentApiService, type AppointmentResponseDto } from '../../../core/api/appointment-api.service';
import {
  EmrApiService,
  type DiagnosisDto,
  type PrescriptionDto,
} from '../../../core/api/emr-api.service';

@Component({
  selector: 'app-doctor-ehr',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './doctor-ehr.html',
  styleUrl: './doctor-ehr.scss',
})
export class DoctorEhr implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly emr = inject(EmrApiService);
  private readonly apptsApi = inject(AppointmentApiService);

  readonly prescriptions = signal<PrescriptionDto[]>([]);
  readonly diagnoses = signal<DiagnosisDto[]>([]);
  readonly appointments = signal<AppointmentResponseDto[]>([]);
  readonly error = signal<string | null>(null);
  readonly emrMsg = signal<string | null>(null);

  readonly rxForm = this.fb.nonNullable.group({
    appointmentId: ['', Validators.required],
    medicineName: ['', Validators.required],
    dosage: [''],
    frequency: [''],
    duration: [''],
  });

  readonly dxForm = this.fb.nonNullable.group({
    appointmentId: ['', Validators.required],
    diagnosisName: ['', Validators.required],
    severity: [''],
    notes: [''],
  });

  ngOnInit(): void {
    this.refreshLists();
    this.apptsApi.list(0, 100).subscribe({
      next: (p) => this.appointments.set(p.content),
      error: () => {},
    });
  }

  refreshLists(): void {
    this.emr.listPrescriptions(0, 100).subscribe({
      next: (p) => this.prescriptions.set(p.content),
      error: () => this.error.set('Could not load prescriptions'),
    });
    this.emr.listDiagnoses(0, 100).subscribe({
      next: (p) => this.diagnoses.set(p.content),
      error: () => {},
    });
  }

  saveRx(): void {
    this.emrMsg.set(null);
    if (this.rxForm.invalid) return;
    const v = this.rxForm.getRawValue();
    const aid = Number(v.appointmentId);
    if (!Number.isFinite(aid)) return;
    this.emr
      .createPrescription({
        appointmentId: aid,
        medicineName: v.medicineName.trim(),
        dosage: v.dosage?.trim() || null,
        frequency: v.frequency?.trim() || null,
        duration: v.duration?.trim() || null,
      })
      .subscribe({
        next: (res) => {
          if (res.success) {
            this.emrMsg.set('Prescription saved.');
            this.rxForm.reset({ appointmentId: '', medicineName: '', dosage: '', frequency: '', duration: '' });
            this.refreshLists();
          } else {
            this.emrMsg.set(res.message ?? 'Failed');
          }
        },
        error: () => this.emrMsg.set('Request failed'),
      });
  }

  saveDx(): void {
    this.emrMsg.set(null);
    if (this.dxForm.invalid) return;
    const v = this.dxForm.getRawValue();
    const aid = Number(v.appointmentId);
    if (!Number.isFinite(aid)) return;
    this.emr
      .createDiagnosis({
        appointmentId: aid,
        diagnosisName: v.diagnosisName.trim(),
        severity: v.severity?.trim() || null,
        notes: v.notes?.trim() || null,
      })
      .subscribe({
        next: (res) => {
          if (res.success) {
            this.emrMsg.set('Diagnosis saved.');
            this.dxForm.reset({ appointmentId: '', diagnosisName: '', severity: '', notes: '' });
            this.refreshLists();
          } else {
            this.emrMsg.set(res.message ?? 'Failed');
          }
        },
        error: () => this.emrMsg.set('Request failed'),
      });
  }
}
