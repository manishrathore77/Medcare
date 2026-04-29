import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-configuration-hub',
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './configuration-hub.html',
  styleUrl: './configuration-hub.scss',
})
export class ConfigurationHub {}
