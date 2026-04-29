import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import type { ApiEnvelope, PagedResponse } from './pharmacy-api.service';

/** Matches {@code DoctorRequest}. */
export interface DoctorRequestBody {
  firstName: string;
  lastName: string;
  specialty?: string | null;
  licenseNumber?: string | null;
  contactNumber?: string | null;
  email?: string | null;
}

/** Matches {@code DoctorResponse}. */
export interface DoctorResponseDto {
  id: number;
  firstName: string;
  lastName: string;
  specialty?: string | null;
  licenseNumber?: string | null;
  contactNumber?: string | null;
  email?: string | null;
  active?: boolean | null;
}

@Injectable({ providedIn: 'root' })
export class DoctorApiService {
  private readonly http = inject(HttpClient);
  private readonly base = `${environment.apiUrl}/api/doctors`;

  list(page = 0, size = 50): Observable<PagedResponse<DoctorResponseDto>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<PagedResponse<DoctorResponseDto>>(this.base, { params });
  }

  getById(id: number): Observable<ApiEnvelope<DoctorResponseDto>> {
    return this.http.get<ApiEnvelope<DoctorResponseDto>>(`${this.base}/${id}`);
  }

  create(body: DoctorRequestBody): Observable<ApiEnvelope<DoctorResponseDto>> {
    return this.http.post<ApiEnvelope<DoctorResponseDto>>(this.base, body);
  }

  update(id: number, body: DoctorRequestBody): Observable<ApiEnvelope<DoctorResponseDto>> {
    return this.http.put<ApiEnvelope<DoctorResponseDto>>(`${this.base}/${id}`, body);
  }

  delete(id: number): Observable<ApiEnvelope<unknown>> {
    return this.http.delete<ApiEnvelope<unknown>>(`${this.base}/${id}`);
  }
}
