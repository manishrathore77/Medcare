import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { PatientApiService } from '../../../core/api/patient-api.service';

@Component({
  selector: 'app-receptionist-register-patient',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './receptionist-register-patient.html',
  styleUrl: './receptionist-register-patient.scss',
})
export class ReceptionistRegisterPatient {
  private readonly fb = inject(FormBuilder);
  private readonly api = inject(PatientApiService);

  readonly submitting = signal(false);
  readonly error = signal<string | null>(null);
  readonly createdId = signal<number | null>(null);

  readonly form = this.fb.nonNullable.group({
    firstName: ['', Validators.required],
    lastName: ['', Validators.required],
    gender: [''],
    dob: [''],
    address: [''],
    emergencyContact: [''],
    insuranceProvider: [''],
    insuranceNumber: [''],
  });

  submit(): void {
    this.error.set(null);
    this.createdId.set(null);
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const v = this.form.getRawValue();
    this.submitting.set(true);
    this.api
      .create({
        firstName: v.firstName.trim(),
        lastName: v.lastName.trim(),
        gender: v.gender?.trim() || null,
        dob: v.dob?.trim() || null,
        address: v.address?.trim() || null,
        emergencyContact: v.emergencyContact?.trim() || null,
        insuranceProvider: v.insuranceProvider?.trim() || null,
        insuranceNumber: v.insuranceNumber?.trim() || null,
      })
      .subscribe({
        next: (res) => {
          this.submitting.set(false);
          if (res.success && res.data?.id != null) {
            this.createdId.set(res.data.id);
            this.form.reset();
          } else {
            this.error.set(res.message ?? 'Could not create patient.');
          }
        },
        error: (e) => {
          this.submitting.set(false);
          this.error.set(e?.error?.message ?? 'Request failed. Check you are signed in as receptionist or admin.');
        },
      });
  }
}
