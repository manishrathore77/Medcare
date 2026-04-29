import { Component, inject, OnInit, signal } from '@angular/core';
import { AccountApiService, type AccountProfileDto } from '../../../core/api/account-api.service';
import { SessionAuthService } from '../../../core/auth/session-auth.service';

@Component({
  selector: 'app-receptionist-profile',
  standalone: true,
  templateUrl: './receptionist-profile.html',
  styleUrl: './receptionist-profile.scss',
})
export class ReceptionistProfile implements OnInit {
  protected readonly auth = inject(SessionAuthService);
  private readonly accountApi = inject(AccountApiService);

  readonly profile = signal<AccountProfileDto | null>(null);
  readonly error = signal<string | null>(null);
  readonly loading = signal(true);

  ngOnInit(): void {
    this.accountApi.me().subscribe({
      next: (res) => {
        if (res.success && res.data) {
          this.profile.set(res.data);
          this.error.set(null);
        } else {
          this.error.set(res.message ?? 'Could not load profile');
        }
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Could not load profile from API.');
        this.loading.set(false);
      },
    });
  }
}
