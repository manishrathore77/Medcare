import { DecimalPipe } from '@angular/common';
import { Component, inject, OnInit, signal } from '@angular/core';
import { forkJoin } from 'rxjs';
import { BillingApiService, type InvoiceDto, type PaymentDto } from '../../../core/api/billing-api.service';

@Component({
  selector: 'app-receptionist-billing',
  standalone: true,
  imports: [DecimalPipe],
  templateUrl: './receptionist-billing.html',
  styleUrl: './receptionist-billing.scss',
})
export class ReceptionistBilling implements OnInit {
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
        this.error.set('Could not load billing. Check API and permissions.');
        this.loading.set(false);
      },
    });
  }
}
