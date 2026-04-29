import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import type { ApiEnvelope, PagedResponse } from './pharmacy-api.service';

export interface DiagnosisDto {
  id: number | null;
  appointmentId: number | null;
  diagnosisName: string | null;
  severity: string | null;
  notes: string | null;
}

export interface PrescriptionDto {
  id: number | null;
  appointmentId: number | null;
  medicineName: string | null;
  dosage: string | null;
  frequency: string | null;
  duration: string | null;
}

@Injectable({ providedIn: 'root' })
export class EmrApiService {
  private readonly http = inject(HttpClient);
  private readonly base = `${environment.apiUrl}/api/emr`;

  listDiagnoses(page = 0, size = 50): Observable<PagedResponse<DiagnosisDto>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<PagedResponse<DiagnosisDto>>(`${this.base}/diagnoses`, { params });
  }

  createDiagnosis(body: Omit<DiagnosisDto, 'id'>): Observable<ApiEnvelope<DiagnosisDto>> {
    return this.http.post<ApiEnvelope<DiagnosisDto>>(`${this.base}/diagnoses`, body);
  }

  listPrescriptions(page = 0, size = 50): Observable<PagedResponse<PrescriptionDto>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<PagedResponse<PrescriptionDto>>(`${this.base}/prescriptions`, { params });
  }

  createPrescription(body: Omit<PrescriptionDto, 'id'>): Observable<ApiEnvelope<PrescriptionDto>> {
    return this.http.post<ApiEnvelope<PrescriptionDto>>(`${this.base}/prescriptions`, body);
  }
}
