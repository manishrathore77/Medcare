import { Component, inject, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { DoctorPortalApiService } from '../../../core/api/doctor-portal-api.service';
import type { PatientResponseDto } from '../../../core/api/patient-api.service';

@Component({
  selector: 'app-doctor-patients',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './doctor-patients.html',
  styleUrl: './doctor-patients.scss',
})
export class DoctorPatients implements OnInit {
  private readonly portal = inject(DoctorPortalApiService);

  readonly rows = signal<PatientResponseDto[]>([]);
  readonly error = signal<string | null>(null);

  ngOnInit(): void {
    this.portal.listMyPatients().subscribe({
      next: (res) => {
        if (res.success && res.data) {
          this.rows.set(res.data);
          this.error.set(null);
        } else {
          this.error.set(res.message ?? 'No data');
        }
      },
      error: () =>
        this.error.set('Could not load patients. Sign in with API (doctor / doctor123).'),
    });
  }
}
