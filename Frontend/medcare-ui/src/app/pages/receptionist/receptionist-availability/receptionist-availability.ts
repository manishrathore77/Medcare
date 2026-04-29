import { DatePipe } from '@angular/common';
import { Component, inject, OnInit, signal } from '@angular/core';
import { forkJoin } from 'rxjs';
import { AppointmentApiService, type AppointmentResponseDto } from '../../../core/api/appointment-api.service';
import { DoctorApiService, type DoctorResponseDto } from '../../../core/api/doctor-api.service';

@Component({
  selector: 'app-receptionist-availability',
  standalone: true,
  imports: [DatePipe],
  templateUrl: './receptionist-availability.html',
  styleUrl: './receptionist-availability.scss',
})
export class ReceptionistAvailability implements OnInit {
  private readonly doctorsApi = inject(DoctorApiService);
  private readonly appointmentsApi = inject(AppointmentApiService);

  readonly doctors = signal<DoctorResponseDto[]>([]);
  readonly byDoctor = signal<Map<number, AppointmentResponseDto[]>>(new Map());
  readonly error = signal<string | null>(null);
  readonly loading = signal(true);

  ngOnInit(): void {
    this.loading.set(true);
    forkJoin({
      doctors: this.doctorsApi.list(0, 100),
      appts: this.appointmentsApi.list(0, 500),
    }).subscribe({
      next: ({ doctors, appts }) => {
        this.doctors.set(doctors.content);
        const map = new Map<number, AppointmentResponseDto[]>();
        const now = Date.now();
        for (const a of appts.content) {
          const did = a.doctorId;
          if (did == null) continue;
          if (new Date(a.appointmentTime).getTime() < now) continue;
          if (a.status === 'CANCELLED') continue;
          const list = map.get(did) ?? [];
          list.push(a);
          map.set(did, list);
        }
        for (const [, list] of map) {
          list.sort((x, y) => new Date(x.appointmentTime).getTime() - new Date(y.appointmentTime).getTime());
        }
        this.byDoctor.set(map);
        this.error.set(null);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Could not load schedule preview.');
        this.loading.set(false);
      },
    });
  }

  upcomingFor(doctorId: number): AppointmentResponseDto[] {
    const list = this.byDoctor().get(doctorId) ?? [];
    return list.slice(0, 12);
  }
}
