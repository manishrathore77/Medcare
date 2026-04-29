import { Component, inject, input } from '@angular/core';
import { Router } from '@angular/router';
import { SessionAuthService } from '../../core/auth/session-auth.service';

@Component({
  selector: 'app-top-bar',
  standalone: true,
  templateUrl: './top-bar.html',
  styleUrl: './top-bar.scss',
})
export class TopBar {
  /** Breadcrumb left segment, e.g. `Patient` → shows as `Patient | Dashboard` */
  readonly moduleName = input<string>('');
  readonly title = input.required<string>();
  protected readonly auth = inject(SessionAuthService);
  private readonly router = inject(Router);

  logout(): void {
    this.auth.logout();
    void this.router.navigateByUrl('/login');
  }
}
