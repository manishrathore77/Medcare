import { Component, inject, OnInit, signal } from '@angular/core';
import {
  PatientPortalApiService,
  type PrescriptionDto,
} from '../../../core/api/patient-portal-api.service';

@Component({
  selector: 'app-patient-ehr-page',
  standalone: true,
  templateUrl: './patient-ehr-page.html',
  styleUrl: './patient-ehr-page.scss',
})
export class PatientEhrPage implements OnInit {
  private readonly portal = inject(PatientPortalApiService);
  readonly prescriptions = signal<PrescriptionDto[]>([]);
  readonly error = signal<string | null>(null);

  ngOnInit(): void {
    this.portal.listMyPrescriptions().subscribe({
      next: (res) => {
        if (res.success && res.data) {
          this.prescriptions.set(res.data);
          this.error.set(null);
        } else {
          this.error.set(res.message ?? 'No data');
        }
      },
      error: () =>
        this.error.set('Could not load prescriptions. Use API login as a patient.'),
    });
  }
}
