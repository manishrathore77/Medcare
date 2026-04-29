import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ConfigHubDataService } from '../../../core/rbac/config-hub-data.service';

@Component({
  selector: 'app-config-modules',
  imports: [FormsModule],
  templateUrl: './config-modules.html',
  styleUrl: './config-modules.scss',
})
export class ConfigModules {
  protected readonly rbac = inject(ConfigHubDataService);
  readonly newName = signal('');
  readonly newDesc = signal('');

  add(): void {
    this.rbac.addModule(this.newName(), this.newDesc());
    this.newName.set('');
    this.newDesc.set('');
  }

  toggle(id: string, active: boolean): void {
    this.rbac.updateModule(id, { active: !active });
  }

  remove(id: string): void {
    if (confirm('Delete this module from the configuration store? Related submodules may be removed.')) {
      this.rbac.deleteModule(id);
    }
  }
}
