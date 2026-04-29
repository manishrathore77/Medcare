import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import type { ApiEnvelope, PagedResponse } from './pharmacy-api.service';

/** Matches {@code PatientRequest} in medcare-api. */
export interface PatientRequestBody {
  firstName: string;
  lastName: string;
  gender?: string | null;
  dob?: string | null;
  address?: string | null;
  emergencyContact?: string | null;
  insuranceProvider?: string | null;
  insuranceNumber?: string | null;
}

/** Matches {@code PatientResponse}. */
export interface PatientResponseDto {
  id: number;
  firstName: string;
  lastName: string;
  gender?: string | null;
  dob?: string | null;
  address?: string | null;
  emergencyContact?: string | null;
  insuranceProvider?: string | null;
  insuranceNumber?: string | null;
}

@Injectable({ providedIn: 'root' })
export class PatientApiService {
  private readonly http = inject(HttpClient);
  private readonly base = `${environment.apiUrl}/api/patients`;

  list(page = 0, size = 50): Observable<PagedResponse<PatientResponseDto>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<PagedResponse<PatientResponseDto>>(this.base, { params });
  }

  getById(id: number): Observable<ApiEnvelope<PatientResponseDto>> {
    return this.http.get<ApiEnvelope<PatientResponseDto>>(`${this.base}/${id}`);
  }

  create(body: PatientRequestBody): Observable<ApiEnvelope<PatientResponseDto>> {
    return this.http.post<ApiEnvelope<PatientResponseDto>>(this.base, body);
  }

  update(id: number, body: PatientRequestBody): Observable<ApiEnvelope<PatientResponseDto>> {
    return this.http.put<ApiEnvelope<PatientResponseDto>>(`${this.base}/${id}`, body);
  }

  delete(id: number): Observable<ApiEnvelope<unknown>> {
    return this.http.delete<ApiEnvelope<unknown>>(`${this.base}/${id}`);
  }
}
