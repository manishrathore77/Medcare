import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { layoutTitleSignal } from '../../core/layout-title';
import { TopBar } from '../../shared/top-bar/top-bar';

@Component({
  selector: 'app-receptionist-layout',
  imports: [RouterOutlet, RouterLink, RouterLinkActive, TopBar],
  templateUrl: './receptionist-layout.html',
  styleUrl: './receptionist-layout.scss',
})
export class ReceptionistLayout {
  protected readonly topTitle = layoutTitleSignal('Reception');
}
