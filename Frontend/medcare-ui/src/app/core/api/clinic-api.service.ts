import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import type { ApiEnvelope, PagedResponse } from './pharmacy-api.service';

export interface ClinicDto {
  id: number;
  name: string;
  location?: string | null;
  contactNumber?: string | null;
}

/** Matches {@code ClinicRequest}. */
export interface ClinicRequestBody {
  name: string;
  location?: string | null;
  contactNumber?: string | null;
}

@Injectable({ providedIn: 'root' })
export class ClinicApiService {
  private readonly http = inject(HttpClient);
  private readonly base = `${environment.apiUrl}/api/clinics`;

  list(page = 0, size = 50): Observable<PagedResponse<ClinicDto>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<PagedResponse<ClinicDto>>(this.base, { params });
  }

  getById(id: number): Observable<ApiEnvelope<ClinicDto>> {
    return this.http.get<ApiEnvelope<ClinicDto>>(`${this.base}/${id}`);
  }

  create(body: ClinicRequestBody): Observable<ApiEnvelope<ClinicDto>> {
    return this.http.post<ApiEnvelope<ClinicDto>>(this.base, body);
  }

  update(id: number, body: ClinicRequestBody): Observable<ApiEnvelope<ClinicDto>> {
    return this.http.put<ApiEnvelope<ClinicDto>>(`${this.base}/${id}`, body);
  }

  delete(id: number): Observable<ApiEnvelope<unknown>> {
    return this.http.delete<ApiEnvelope<unknown>>(`${this.base}/${id}`);
  }
}
