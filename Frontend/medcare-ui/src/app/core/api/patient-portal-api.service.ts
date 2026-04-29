import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import type { ApiEnvelope } from './pharmacy-api.service';
import type { PatientRequestBody, PatientResponseDto } from './patient-api.service';

export interface PrescriptionDto {
  id: number | null;
  appointmentId: number | null;
  medicineName: string | null;
  dosage: string | null;
  frequency: string | null;
  duration: string | null;
}

/** Self-service endpoints under {@code /api/patient} (role PATIENT). */
@Injectable({ providedIn: 'root' })
export class PatientPortalApiService {
  private readonly http = inject(HttpClient);
  private readonly base = `${environment.apiUrl}/api/patient`;

  getMe(): Observable<ApiEnvelope<PatientResponseDto>> {
    return this.http.get<ApiEnvelope<PatientResponseDto>>(`${this.base}/me`);
  }

  updateMe(body: PatientRequestBody): Observable<ApiEnvelope<PatientResponseDto>> {
    return this.http.put<ApiEnvelope<PatientResponseDto>>(`${this.base}/me`, body);
  }

  listMyPrescriptions(): Observable<ApiEnvelope<PrescriptionDto[]>> {
    return this.http.get<ApiEnvelope<PrescriptionDto[]>>(`${this.base}/prescriptions`);
  }
}
