import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ConfigHubDataService } from '../../../core/rbac/config-hub-data.service';

@Component({
  selector: 'app-config-roles',
  imports: [FormsModule],
  templateUrl: './config-roles.html',
  styleUrl: './config-roles.scss',
})
export class ConfigRoles {
  protected readonly rbac = inject(ConfigHubDataService);
  readonly selectedRoleId = signal<string>(INITIAL_ROLE);
  readonly newRoleName = signal('');
  readonly newRoleDesc = signal('');

  addRole(): void {
    this.rbac.addRole(this.newRoleName(), this.newRoleDesc());
    this.newRoleName.set('');
    this.newRoleDesc.set('');
  }

  selectRole(id: string): void {
    this.selectedRoleId.set(id);
  }

  role(): ReturnType<ConfigHubDataService['rolesList']>[0] | undefined {
    return this.rbac.rolesList().find((r) => r.id === this.selectedRoleId());
  }

  toggleModule(modId: string, on: boolean): void {
    const r = this.role();
    if (!r) return;
    const set = new Set(r.moduleIds);
    if (on) set.add(modId);
    else set.delete(modId);
    this.rbac.setRoleModules(r.id, [...set]);
  }

  moduleOn(modId: string): boolean {
    return this.role()?.moduleIds.includes(modId) ?? false;
  }

  permOn(permId: string): boolean {
    return this.role()?.permissionIds.includes(permId) ?? false;
  }

  togglePerm(permId: string, on: boolean): void {
    const r = this.role();
    if (!r) return;
    this.rbac.toggleRolePermission(r.id, permId, on);
  }

  removeRole(): void {
    const r = this.role();
    if (!r || r.id === 'role-admin') {
      alert('Keep at least the seeded Admin role for demo.');
      return;
    }
    if (confirm('Delete this role?')) {
      this.rbac.deleteRole(r.id);
      this.selectedRoleId.set('role-admin');
    }
  }
}

const INITIAL_ROLE = 'role-admin';
