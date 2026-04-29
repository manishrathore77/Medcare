import { Component, input } from '@angular/core';

export type KpiAccent = 'blue' | 'green' | 'violet' | 'amber' | 'rose';

@Component({
  selector: 'app-kpi-card',
  standalone: true,
  templateUrl: './kpi-card.html',
  styleUrl: './kpi-card.scss',
})
export class KpiCard {
  readonly label = input.required<string>();
  readonly value = input.required<string>();
  readonly hint = input<string>();
  readonly trend = input<string>();
  /** When true, trend is styled as positive (green); false as negative (red). */
  readonly trendPositive = input(true);
  /** Material Symbols icon name, e.g. `event_upcoming` */
  readonly icon = input.required<string>();
  readonly accent = input<KpiAccent>('blue');
}
