import { Component, inject, OnInit, signal } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { PharmacyApiService, type MedicineDto } from '../../../core/api/pharmacy-api.service';

@Component({
  selector: 'app-patient-medicines',
  standalone: true,
  imports: [DecimalPipe],
  templateUrl: './patient-medicines.html',
  styleUrl: './patient-medicines.scss',
})
export class PatientMedicines implements OnInit {
  private readonly pharmacy = inject(PharmacyApiService);
  readonly rows = signal<MedicineDto[]>([]);
  readonly error = signal<string | null>(null);

  ngOnInit(): void {
    this.pharmacy.listMedicines(0, 200).subscribe({
      next: (p) => {
        this.rows.set(p.content);
        this.error.set(null);
      },
      error: () =>
        this.error.set(
          'Could not load catalog. Sign in with API; patient role includes pharmacy read.',
        ),
    });
  }
}
