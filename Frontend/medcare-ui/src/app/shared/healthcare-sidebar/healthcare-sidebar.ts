import { Component, inject, input } from '@angular/core';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { SessionAuthService } from '../../core/auth/session-auth.service';

/** Single item in the Healthcare ERP sidebar (Figma reference). */
export interface HealthcareNavItem {
  label: string;
  /** Material Symbols Outlined ligature name. */
  icon: string;
  path: string;
  /** Use `true` for dashboard home routes. */
  exact?: boolean;
}

@Component({
  selector: 'app-healthcare-sidebar',
  standalone: true,
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './healthcare-sidebar.html',
  styleUrl: './healthcare-sidebar.scss',
})
export class HealthcareSidebar {
  protected readonly auth = inject(SessionAuthService);
  private readonly router = inject(Router);

  /** Router link for the brand (usually role dashboard). */
  readonly brandRouterLink = input.required<string>();
  readonly navItems = input.required<HealthcareNavItem[]>();

  protected roleLabel(): string {
    const r = this.auth.role();
    if (!r) return '';
    const map: Record<string, string> = {
      PATIENT: 'Patient',
      DOCTOR: 'Doctor',
      RECEPTIONIST: 'Staff',
      ADMIN: 'Admin',
      IT_SUPPORT: 'IT Support',
    };
    return map[r] ?? r;
  }

  logout(): void {
    this.auth.logout();
    void this.router.navigateByUrl('/login');
  }
}
