import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { layoutTitleSignal } from '../../core/layout-title';
import { TopBar } from '../../shared/top-bar/top-bar';

@Component({
  selector: 'app-doctor-layout',
  imports: [RouterOutlet, RouterLink, RouterLinkActive, TopBar],
  templateUrl: './doctor-layout.html',
  styleUrl: './doctor-layout.scss',
})
export class DoctorLayout {
  protected readonly topTitle = layoutTitleSignal('Doctor portal');
}
