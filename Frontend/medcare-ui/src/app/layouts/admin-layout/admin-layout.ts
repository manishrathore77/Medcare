import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { layoutTitleSignal } from '../../core/layout-title';
import { TopBar } from '../../shared/top-bar/top-bar';

@Component({
  selector: 'app-admin-layout',
  imports: [RouterOutlet, RouterLink, RouterLinkActive, TopBar],
  templateUrl: './admin-layout.html',
  styleUrl: './admin-layout.scss',
})
export class AdminLayout {
  protected readonly topTitle = layoutTitleSignal('Administration');
}
