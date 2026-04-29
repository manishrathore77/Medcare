import { Component, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { DoctorPortalApiService } from '../../../core/api/doctor-portal-api.service';
import type { PatientResponseDto } from '../../../core/api/patient-api.service';

@Component({
  selector: 'app-doctor-patient-detail',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './doctor-patient-detail.html',
  styleUrl: './doctor-patient-detail.scss',
})
export class DoctorPatientDetail implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly portal = inject(DoctorPortalApiService);

  readonly row = signal<PatientResponseDto | null>(null);
  readonly error = signal<string | null>(null);

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (!Number.isFinite(id)) {
      this.error.set('Invalid patient id');
      return;
    }
    this.portal.getMyPatientById(id).subscribe({
      next: (res) => {
        if (res.success && res.data) {
          this.row.set(res.data);
          this.error.set(null);
        } else {
          this.error.set(res.message ?? 'Not found');
        }
      },
      error: () => this.error.set('Could not load patient (check API login as doctor).'),
    });
  }
}
