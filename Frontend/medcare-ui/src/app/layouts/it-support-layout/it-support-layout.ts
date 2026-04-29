import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { layoutTitleSignal } from '../../core/layout-title';
import { TopBar } from '../../shared/top-bar/top-bar';

@Component({
  selector: 'app-it-support-layout',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive, TopBar],
  templateUrl: './it-support-layout.html',
  styleUrl: './it-support-layout.scss',
})
export class ItSupportLayout {
  protected readonly topTitle = layoutTitleSignal('IT Support');
}
