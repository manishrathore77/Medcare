import { Component, inject, OnInit, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import {
  AppointmentApiService,
  type AppointmentTypeApi,
} from '../../../core/api/appointment-api.service';
import { ClinicApiService, type ClinicDto } from '../../../core/api/clinic-api.service';
import { DoctorApiService, type DoctorResponseDto } from '../../../core/api/doctor-api.service';
import { PatientApiService, type PatientResponseDto } from '../../../core/api/patient-api.service';

@Component({
  selector: 'app-receptionist-book',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './receptionist-book.html',
  styleUrl: './receptionist-book.scss',
})
export class ReceptionistBook implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly doctorsApi = inject(DoctorApiService);
  private readonly clinicsApi = inject(ClinicApiService);
  private readonly patientsApi = inject(PatientApiService);
  private readonly appointmentsApi = inject(AppointmentApiService);

  readonly doctors = signal<DoctorResponseDto[]>([]);
  readonly clinics = signal<ClinicDto[]>([]);
  readonly patients = signal<PatientResponseDto[]>([]);
  readonly error = signal<string | null>(null);
  readonly success = signal<string | null>(null);
  readonly loading = signal(true);
  readonly submitting = signal(false);

  readonly form = this.fb.nonNullable.group({
    patientId: ['', Validators.required],
    doctorId: ['', Validators.required],
    clinicId: ['', Validators.required],
    appointmentTime: ['', Validators.required],
    appointmentType: this.fb.nonNullable.control<AppointmentTypeApi>('IN_CLINIC', Validators.required),
  });

  ngOnInit(): void {
    this.loading.set(true);
    let n = 0;
    const done = (): void => {
      n += 1;
      if (n >= 3) this.loading.set(false);
    };
    this.doctorsApi.list(0, 100).subscribe({
      next: (p) => {
        this.doctors.set(p.content);
        done();
      },
      error: () => {
        this.error.set('Could not load doctors.');
        done();
      },
    });
    this.clinicsApi.list(0, 100).subscribe({
      next: (p) => {
        this.clinics.set(p.content);
        done();
      },
      error: () => {
        this.error.set('Could not load clinics.');
        done();
      },
    });
    this.patientsApi.list(0, 500).subscribe({
      next: (p) => {
        this.patients.set(p.content);
        done();
      },
      error: () => {
        this.error.set('Could not load patients.');
        done();
      },
    });
  }

  submit(): void {
    this.error.set(null);
    this.success.set(null);
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const v = this.form.getRawValue();
    const patientId = Number(v.patientId);
    const doctorId = Number(v.doctorId);
    const clinicId = Number(v.clinicId);
    if (!Number.isFinite(patientId) || !Number.isFinite(doctorId) || !Number.isFinite(clinicId)) return;

    let time = v.appointmentTime.trim();
    if (time.length === 16) {
      time = `${time}:00`;
    }

    this.submitting.set(true);
    this.appointmentsApi
      .create({
        doctorId,
        patientId,
        clinicId,
        appointmentTime: time,
        appointmentType: v.appointmentType,
      })
      .subscribe({
        next: (res) => {
          this.submitting.set(false);
          if (res.success) {
            this.success.set('Appointment booked.');
            this.form.patchValue({ appointmentTime: '' });
          } else {
            this.error.set(res.message ?? 'Booking failed');
          }
        },
        error: (e) => {
          this.submitting.set(false);
          this.error.set(e?.error?.message ?? 'Booking failed.');
        },
      });
  }
}
