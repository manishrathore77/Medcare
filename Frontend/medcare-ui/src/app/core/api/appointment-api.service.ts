import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import type { ApiEnvelope, PagedResponse } from './pharmacy-api.service';

export type AppointmentTypeApi = 'IN_CLINIC' | 'VIDEO';

export type AppointmentStatusApi = 'PENDING' | 'CONFIRMED' | 'CANCELLED' | string;

export interface AppointmentResponseDto {
  id: number;
  doctorId: number | null;
  patientId: number | null;
  clinicId: number | null;
  appointmentTime: string;
  status: AppointmentStatusApi | null;
  appointmentType: AppointmentTypeApi | null;
}

export interface AppointmentRequestBody {
  doctorId: number;
  patientId: number;
  clinicId: number;
  appointmentTime: string;
  appointmentType: AppointmentTypeApi;
}

@Injectable({ providedIn: 'root' })
export class AppointmentApiService {
  private readonly http = inject(HttpClient);
  private readonly base = `${environment.apiUrl}/api/appointments`;

  list(page = 0, size = 50): Observable<PagedResponse<AppointmentResponseDto>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<PagedResponse<AppointmentResponseDto>>(this.base, { params });
  }

  getById(id: number): Observable<ApiEnvelope<AppointmentResponseDto>> {
    return this.http.get<ApiEnvelope<AppointmentResponseDto>>(`${this.base}/${id}`);
  }

  create(body: AppointmentRequestBody): Observable<ApiEnvelope<AppointmentResponseDto>> {
    return this.http.post<ApiEnvelope<AppointmentResponseDto>>(this.base, body);
  }

  /** Reschedule: backend uses {@code appointmentTime} from body. */
  update(id: number, body: AppointmentRequestBody): Observable<ApiEnvelope<AppointmentResponseDto>> {
    return this.http.put<ApiEnvelope<AppointmentResponseDto>>(`${this.base}/${id}`, body);
  }

  delete(id: number): Observable<ApiEnvelope<unknown>> {
    return this.http.delete<ApiEnvelope<unknown>>(`${this.base}/${id}`);
  }
}
