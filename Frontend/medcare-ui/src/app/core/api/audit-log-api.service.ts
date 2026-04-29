import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import type { ApiEnvelope, PagedResponse } from './pharmacy-api.service';

export interface AuditLogDto {
  id: number;
  userId: number | null;
  action: string | null;
  entityName: string | null;
  entityId: number | null;
  oldValue: string | null;
  newValue: string | null;
}

@Injectable({ providedIn: 'root' })
export class AuditLogApiService {
  private readonly http = inject(HttpClient);
  private readonly base = `${environment.apiUrl}/api/audit-logs`;

  list(page = 0, size = 100): Observable<PagedResponse<AuditLogDto>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<PagedResponse<AuditLogDto>>(this.base, { params });
  }

  delete(id: number): Observable<ApiEnvelope<unknown>> {
    return this.http.delete<ApiEnvelope<unknown>>(`${this.base}/${id}`);
  }
}
