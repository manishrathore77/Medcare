import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ConfigHubDataService } from '../../../core/rbac/config-hub-data.service';

@Component({
  selector: 'app-config-submodules',
  imports: [FormsModule],
  templateUrl: './config-submodules.html',
  styleUrl: './config-submodules.scss',
})
export class ConfigSubmodules {
  protected readonly rbac = inject(ConfigHubDataService);
  readonly moduleId = signal('');
  readonly subName = signal('');

  add(): void {
    const mid = this.moduleId();
    if (!mid) return;
    this.rbac.addSubmodule(mid, this.subName());
    this.subName.set('');
  }

  move(subId: string, newModuleId: string): void {
    this.rbac.updateSubmodule(subId, { moduleId: newModuleId });
  }

  remove(id: string): void {
    if (confirm('Delete submodule and its permissions from mock store?')) {
      this.rbac.deleteSubmodule(id);
    }
  }

  moduleName(id: string): string {
    return this.rbac.modulesList().find((m) => m.id === id)?.name ?? id;
  }
}
