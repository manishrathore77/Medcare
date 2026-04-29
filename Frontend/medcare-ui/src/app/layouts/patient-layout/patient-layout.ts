import { Component, inject } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { SessionAuthService } from '../../core/auth/session-auth.service';
import { layoutTitleSignal } from '../../core/layout-title';
import { TopBar } from '../../shared/top-bar/top-bar';

@Component({
  selector: 'app-patient-layout',
  imports: [RouterOutlet, RouterLink, RouterLinkActive, TopBar],
  templateUrl: './patient-layout.html',
  styleUrl: './patient-layout.scss',
})
export class PatientLayout {
  protected readonly auth = inject(SessionAuthService);
  protected readonly topTitle = layoutTitleSignal('Patient portal');
}
