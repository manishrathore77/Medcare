import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import type { ApiEnvelope } from './pharmacy-api.service';

export interface AccountProfileDto {
  id: number;
  username: string;
  email?: string | null;
  role?: string | null;
  phone?: string | null;
  active?: boolean | null;
}

@Injectable({ providedIn: 'root' })
export class AccountApiService {
  private readonly http = inject(HttpClient);
  private readonly base = `${environment.apiUrl}/api/account`;

  me(): Observable<ApiEnvelope<AccountProfileDto>> {
    return this.http.get<ApiEnvelope<AccountProfileDto>>(`${this.base}/me`);
  }
}
