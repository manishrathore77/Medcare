import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import type { ApiEnvelope, PagedResponse } from './pharmacy-api.service';

export type InvoiceStatusApi = 'PENDING' | 'PAID' | 'CANCELLED' | string;
export type InsuranceStatusApi = 'NOT_APPLIED' | 'SUBMITTED' | 'APPROVED' | 'REJECTED' | string;
export type PaymentModeApi = 'CASH' | 'CARD' | 'UPI' | 'INSURANCE' | string;
export type PaymentStatusApi = string;
export type PaymentGatewayApi = string;

export interface InvoiceDto {
  id: number;
  appointmentId: number | null;
  subtotal: number | null;
  gst: number | null;
  discount: number | null;
  totalAmount: number | null;
  status: InvoiceStatusApi | null;
  insuranceClaimNo: string | null;
  insuranceStatus: InsuranceStatusApi | null;
}

export interface PaymentDto {
  id: number;
  invoiceId: number | null;
  amount: number | null;
  paymentMode: PaymentModeApi | null;
  paymentStatus: PaymentStatusApi | null;
  transactionId: string | null;
  gateway: PaymentGatewayApi | null;
  paidAt: string | null;
}

@Injectable({ providedIn: 'root' })
export class BillingApiService {
  private readonly http = inject(HttpClient);
  private readonly base = `${environment.apiUrl}/api/billing`;

  listInvoices(page = 0, size = 50): Observable<PagedResponse<InvoiceDto>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<PagedResponse<InvoiceDto>>(`${this.base}/invoices`, { params });
  }

  listPayments(page = 0, size = 50): Observable<PagedResponse<PaymentDto>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<PagedResponse<PaymentDto>>(`${this.base}/payments`, { params });
  }
}
