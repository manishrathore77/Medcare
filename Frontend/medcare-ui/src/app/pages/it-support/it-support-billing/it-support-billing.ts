import { DecimalPipe } from '@angular/common';
import { Component, inject, OnInit, signal } from '@angular/core';
import { forkJoin } from 'rxjs';
import { BillingApiService, type InvoiceDto, type PaymentDto } from '../../../core/api/billing-api.service';

@Component({
  selector: 'app-it-support-billing',
  standalone: true,
  imports: [DecimalPipe],
  templateUrl: './it-support-billing.html',
  styleUrl: './it-support-billing.scss',
})
export class ItSupportBilling implements OnInit {
  private readonly billing = inject(BillingApiService);

  readonly invoices = signal<InvoiceDto[]>([]);
  readonly payments = signal<PaymentDto[]>([]);
  readonly error = signal<string | null>(null);
  readonly loading = signal(true);

  ngOnInit(): void {
    this.loading.set(true);
    forkJoin({
      inv: this.billing.listInvoices(0, 100),
      pay: this.billing.listPayments(0, 100),
    }).subscribe({
      next: ({ inv, pay }) => {
        this.invoices.set(inv.content);
        this.payments.set(pay.content);
        this.error.set(null);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Read-only billing requires invoice and milestone scopes on your JWT.');
        this.loading.set(false);
      },
    });
  }
}
