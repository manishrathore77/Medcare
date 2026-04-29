import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface MedicineDto {
  id: number | null;
  name: string;
  batchNo: string;
  /** ISO date string or null when unknown */
  expiryDate: string | null;
  stockQuantity: number;
  reorderLevel: number;
  unitPrice: number;
}

export interface PagedResponse<T> {
  content: T[];
  totalElements: number;
  page: number;
  size: number;
  totalPages: number;
  last: boolean;
}

export interface ApiEnvelope<T> {
  success: boolean;
  message?: string;
  data: T | null;
  status: number;
}

export type InventoryChangeType = 'IN' | 'OUT' | 'ADJUSTMENT';

export interface InventoryLogDto {
  id: number | null;
  medicineId: number | null;
  changeType: InventoryChangeType;
  quantity: number;
  reason: string | null;
}

@Injectable({ providedIn: 'root' })
export class PharmacyApiService {
  private readonly http = inject(HttpClient);
  private readonly base = `${environment.apiUrl}/api/pharmacy`;

  listMedicines(page = 0, size = 50): Observable<PagedResponse<MedicineDto>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<PagedResponse<MedicineDto>>(`${this.base}/medicines`, { params });
  }

  getMedicine(id: number): Observable<ApiEnvelope<MedicineDto>> {
    return this.http.get<ApiEnvelope<MedicineDto>>(`${this.base}/medicines/${id}`);
  }

  createMedicine(body: Omit<MedicineDto, 'id'>): Observable<ApiEnvelope<MedicineDto>> {
    return this.http.post<ApiEnvelope<MedicineDto>>(`${this.base}/medicines`, body);
  }

  updateMedicine(id: number, body: Omit<MedicineDto, 'id'>): Observable<ApiEnvelope<MedicineDto>> {
    return this.http.put<ApiEnvelope<MedicineDto>>(`${this.base}/medicines/${id}`, body);
  }

  deleteMedicine(id: number): Observable<ApiEnvelope<unknown>> {
    return this.http.delete<ApiEnvelope<unknown>>(`${this.base}/medicines/${id}`);
  }

  listInventory(page = 0, size = 50): Observable<PagedResponse<InventoryLogDto>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<PagedResponse<InventoryLogDto>>(`${this.base}/inventory`, { params });
  }

  createInventoryLog(body: Omit<InventoryLogDto, 'id'>): Observable<ApiEnvelope<InventoryLogDto>> {
    return this.http.post<ApiEnvelope<InventoryLogDto>>(`${this.base}/inventory`, body);
  }
}
