import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import type { ApiEnvelope, PagedResponse } from './pharmacy-api.service';

/** Matches {@code UserRequest} — password required on create/update per API. */
export interface UserRequestBody {
  username: string;
  password: string;
  email: string;
}

export interface UserResponseDto {
  id: number;
  username: string;
  email?: string | null;
  role?: string | null;
  active?: boolean | null;
}

@Injectable({ providedIn: 'root' })
export class UserApiService {
  private readonly http = inject(HttpClient);
  private readonly base = `${environment.apiUrl}/api/users`;

  list(page = 0, size = 100): Observable<PagedResponse<UserResponseDto>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<PagedResponse<UserResponseDto>>(this.base, { params });
  }

  getById(id: number): Observable<ApiEnvelope<UserResponseDto>> {
    return this.http.get<ApiEnvelope<UserResponseDto>>(`${this.base}/${id}`);
  }

  create(body: UserRequestBody): Observable<ApiEnvelope<UserResponseDto>> {
    return this.http.post<ApiEnvelope<UserResponseDto>>(this.base, body);
  }

  update(id: number, body: UserRequestBody): Observable<ApiEnvelope<UserResponseDto>> {
    return this.http.put<ApiEnvelope<UserResponseDto>>(`${this.base}/${id}`, body);
  }

  delete(id: number): Observable<ApiEnvelope<unknown>> {
    return this.http.delete<ApiEnvelope<unknown>>(`${this.base}/${id}`);
  }
}
