import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import type { ApiEnvelope, PagedResponse } from './pharmacy-api.service';

export interface LabReportDto {
  id: number;
  labTestId: number | null;
  patientId: number | null;
  reportFileUrl: string | null;
  fileName: string | null;
  contentType: string | null;
  uploadedAt: string | null;
}

@Injectable({ providedIn: 'root' })
export class LabApiService {
  private readonly http = inject(HttpClient);
  private readonly base = `${environment.apiUrl}/api/lab`;

  listReports(page = 0, size = 100, patientId?: number): Observable<PagedResponse<LabReportDto>> {
    let params = new HttpParams().set('page', page).set('size', size);
    if (patientId != null) params = params.set('patientId', patientId);
    return this.http.get<PagedResponse<LabReportDto>>(`${this.base}/reports`, { params });
  }

  uploadReport(patientId: number, file: File, labTestId?: number): Observable<ApiEnvelope<LabReportDto>> {
    const form = new FormData();
    form.set('patientId', String(patientId));
    if (labTestId != null) form.set('labTestId', String(labTestId));
    form.set('file', file);
    return this.http.post<ApiEnvelope<LabReportDto>>(`${this.base}/reports/upload`, form);
  }

  downloadReport(reportId: number): Observable<Blob> {
    return this.http.get(`${this.base}/reports/${reportId}/download`, { responseType: 'blob' });
  }
}
