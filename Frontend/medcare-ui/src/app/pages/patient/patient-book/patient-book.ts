import { Component, inject, OnInit, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import {
  AppointmentApiService,
  type AppointmentTypeApi,
} from '../../../core/api/appointment-api.service';
import { ClinicApiService, type ClinicDto } from '../../../core/api/clinic-api.service';
import { DoctorApiService, type DoctorResponseDto } from '../../../core/api/doctor-api.service';

@Component({
  selector: 'app-patient-book',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './patient-book.html',
  styleUrl: './patient-book.scss',
})
export class PatientBook implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly doctorsApi = inject(DoctorApiService);
  private readonly clinicsApi = inject(ClinicApiService);
  private readonly appointmentsApi = inject(AppointmentApiService);

  readonly doctors = signal<DoctorResponseDto[]>([]);
  readonly clinics = signal<ClinicDto[]>([]);
  readonly error = signal<string | null>(null);
  readonly success = signal<string | null>(null);
  readonly loading = signal(true);
  readonly submitting = signal(false);

  readonly form = this.fb.nonNullable.group({
    doctorId: ['', Validators.required],
    clinicId: ['', Validators.required],
    /** ISO local datetime string from input type="datetime-local" */
    appointmentTime: ['', Validators.required],
    appointmentType: this.fb.nonNullable.control<AppointmentTypeApi>('IN_CLINIC', Validators.required),
  });

  ngOnInit(): void {
    this.loading.set(true);
    let loaded = 0;
    const done = (): void => {
      loaded += 1;
      if (loaded >= 2) this.loading.set(false);
    };
    this.doctorsApi.list(0, 100).subscribe({
      next: (p) => {
        this.doctors.set(p.content);
        done();
      },
      error: () => {
        this.error.set('Could not load doctors. Is the API running?');
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
  }

  submit(): void {
    this.error.set(null);
    this.success.set(null);
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const v = this.form.getRawValue();
    const doctorId = Number(v.doctorId);
    const clinicId = Number(v.clinicId);
    if (!Number.isFinite(doctorId) || !Number.isFinite(clinicId)) return;

    let time = v.appointmentTime.trim();
    if (time.length === 16) {
      time = `${time}:00`;
    }

    this.submitting.set(true);
    this.appointmentsApi
      .create({
        doctorId,
        patientId: 0,
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
          this.error.set(e?.error?.message ?? 'Booking failed. Sign in as a patient with API credentials.');
        },
      });
  }
}
