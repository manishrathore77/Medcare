import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import type { ApiEnvelope } from './pharmacy-api.service';
import type { DoctorRequestBody, DoctorResponseDto } from './doctor-api.service';
import type { PatientResponseDto } from './patient-api.service';

/** Self-service endpoints under {@code /api/doctor} (role DOCTOR). */
@Injectable({ providedIn: 'root' })
export class DoctorPortalApiService {
  private readonly http = inject(HttpClient);
  private readonly base = `${environment.apiUrl}/api/doctor`;

  getMe(): Observable<ApiEnvelope<DoctorResponseDto>> {
    return this.http.get<ApiEnvelope<DoctorResponseDto>>(`${this.base}/me`);
  }

  updateMe(body: DoctorRequestBody): Observable<ApiEnvelope<DoctorResponseDto>> {
    return this.http.put<ApiEnvelope<DoctorResponseDto>>(`${this.base}/me`, body);
  }

  listMyPatients(): Observable<ApiEnvelope<PatientResponseDto[]>> {
    return this.http.get<ApiEnvelope<PatientResponseDto[]>>(`${this.base}/patients`);
  }

  getMyPatientById(id: number): Observable<ApiEnvelope<PatientResponseDto>> {
    return this.http.get<ApiEnvelope<PatientResponseDto>>(`${this.base}/patients/${id}`);
  }
}
