import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ConfigHubDataService } from '../../../core/rbac/config-hub-data.service';
import type { PermissionKind } from '../../../core/rbac/rbac.models';

@Component({
  selector: 'app-config-permissions',
  imports: [FormsModule],
  templateUrl: './config-permissions.html',
  styleUrl: './config-permissions.scss',
})
export class ConfigPermissions {
  protected readonly rbac = inject(ConfigHubDataService);
  readonly submoduleId = signal('');
  readonly label = signal('');
  readonly key = signal('');
  readonly scope = signal('');
  readonly kind = signal<PermissionKind>('READ');

  kinds: PermissionKind[] = ['CREATE', 'READ', 'UPDATE', 'DELETE', 'CUSTOM'];

  add(): void {
    const sid = this.submoduleId();
    if (!sid) return;
    this.rbac.addPermission(sid, this.label(), this.key(), this.scope(), this.kind());
    this.label.set('');
    this.key.set('');
    this.scope.set('');
  }

  remove(id: string): void {
    this.rbac.deletePermission(id);
  }

  subLabel(id: string): string {
    const s = this.rbac.submodulesList().find((x) => x.id === id);
    const m = s ? this.rbac.modulesList().find((x) => x.id === s.moduleId) : null;
    return s ? `${m?.name ?? '?'}/${s.name}` : id;
  }
}
