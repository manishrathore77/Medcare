import { Injectable, signal, computed } from '@angular/core';
import {
  INITIAL_MODULES,
  INITIAL_PERMISSIONS,
  INITIAL_ROLES,
  INITIAL_SUBMODULES,
} from './mock-rbac.data';
import type { RbacModule, RbacPermission, RbacRole, RbacSubmodule } from './rbac.models';

function uid(prefix: string): string {
  return `${prefix}-${Date.now().toString(36)}-${Math.random().toString(36).slice(2, 7)}`;
}

@Injectable({ providedIn: 'root' })
export class MockRbacService {
  private readonly modules = signal<RbacModule[]>(structuredClone(INITIAL_MODULES));
  private readonly submodules = signal<RbacSubmodule[]>(structuredClone(INITIAL_SUBMODULES));
  private readonly permissions = signal<RbacPermission[]>(structuredClone(INITIAL_PERMISSIONS));
  private readonly roles = signal<RbacRole[]>(structuredClone(INITIAL_ROLES));

  readonly modulesList = this.modules.asReadonly();
  readonly submodulesList = this.submodules.asReadonly();
  readonly permissionsList = this.permissions.asReadonly();
  readonly rolesList = this.roles.asReadonly();

  readonly tree = computed(() => {
    const mods = this.modules();
    const subs = this.submodules();
    const perms = this.permissions();
    return mods.map((m) => ({
      module: m,
      submodules: subs
        .filter((s) => s.moduleId === m.id)
        .map((s) => ({
          submodule: s,
          permissions: perms.filter((p) => p.submoduleId === s.id),
        })),
    }));
  });

  // --- Modules ---
  addModule(name: string, description: string): void {
    const n = name.trim();
    if (!n || this.modules().some((m) => m.name.toLowerCase() === n.toLowerCase())) return;
    this.modules.update((list) => [
      ...list,
      { id: uid('mod'), name: n, description: description.trim(), active: true },
    ]);
  }

  updateModule(id: string, patch: Partial<Pick<RbacModule, 'name' | 'description' | 'active'>>): void {
    this.modules.update((list) =>
      list.map((m) => (m.id === id ? { ...m, ...patch, name: patch.name?.trim() ?? m.name } : m)),
    );
  }

  deleteModule(id: string): void {
    const subIds = this.submodules()
      .filter((s) => s.moduleId === id)
      .map((s) => s.id);
    const permDrop = new Set(
      this.permissions().filter((p) => subIds.includes(p.submoduleId)).map((p) => p.id),
    );
    this.modules.update((list) => list.filter((m) => m.id !== id));
    this.submodules.update((list) => list.filter((s) => s.moduleId !== id));
    this.permissions.update((list) => list.filter((p) => !subIds.includes(p.submoduleId)));
    this.roles.update((list) =>
      list.map((r) => ({
        ...r,
        moduleIds: r.moduleIds.filter((mid) => mid !== id),
        permissionIds: r.permissionIds.filter((pid) => !permDrop.has(pid)),
      })),
    );
  }

  // --- Submodules ---
  addSubmodule(moduleId: string, name: string): void {
    const n = name.trim();
    if (!n) return;
    this.submodules.update((list) => [...list, { id: uid('sub'), moduleId, name: n, active: true }]);
  }

  updateSubmodule(id: string, patch: Partial<Pick<RbacSubmodule, 'name' | 'moduleId' | 'active'>>): void {
    this.submodules.update((list) =>
      list.map((s) => (s.id === id ? { ...s, ...patch, name: patch.name?.trim() ?? s.name } : s)),
    );
  }

  deleteSubmodule(id: string): void {
    const permDrop = new Set(
      this.permissions().filter((p) => p.submoduleId === id).map((p) => p.id),
    );
    this.submodules.update((list) => list.filter((s) => s.id !== id));
    this.permissions.update((list) => list.filter((p) => p.submoduleId !== id));
    this.roles.update((list) =>
      list.map((r) => ({
        ...r,
        permissionIds: r.permissionIds.filter((pid) => !permDrop.has(pid)),
      })),
    );
  }

  // --- Permissions ---
  addPermission(
    submoduleId: string,
    label: string,
    key: string,
    scope: string,
    kind: RbacPermission['kind'],
  ): void {
    const l = label.trim();
    const k = key.trim();
    const sc = scope.trim();
    if (!l || !k || !sc) return;
    this.permissions.update((list) => [
      ...list,
      { id: uid('perm'), submoduleId, key: k, label: l, scope: sc, kind, active: true },
    ]);
  }

  updatePermission(id: string, patch: Partial<Pick<RbacPermission, 'label' | 'scope' | 'kind' | 'active'>>): void {
    this.permissions.update((list) => list.map((p) => (p.id === id ? { ...p, ...patch } : p)));
  }

  deletePermission(id: string): void {
    this.permissions.update((list) => list.filter((p) => p.id !== id));
    this.roles.update((list) =>
      list.map((r) => ({ ...r, permissionIds: r.permissionIds.filter((pid) => pid !== id) })),
    );
  }

  // --- Roles ---
  addRole(name: string, description: string): void {
    const n = name.trim();
    if (!n) return;
    this.roles.update((list) => [
      ...list,
      { id: uid('role'), name: n, description: description.trim(), moduleIds: [], permissionIds: [], active: true },
    ]);
  }

  updateRole(id: string, patch: Partial<Pick<RbacRole, 'name' | 'description' | 'active'>>): void {
    this.roles.update((list) => list.map((r) => (r.id === id ? { ...r, ...patch } : r)));
  }

  setRoleModules(roleId: string, moduleIds: string[]): void {
    this.roles.update((list) => list.map((r) => (r.id === roleId ? { ...r, moduleIds: [...moduleIds] } : r)));
  }

  toggleRolePermission(roleId: string, permissionId: string, on: boolean): void {
    this.roles.update((list) =>
      list.map((r) => {
        if (r.id !== roleId) return r;
        const set = new Set(r.permissionIds);
        if (on) set.add(permissionId);
        else set.delete(permissionId);
        return { ...r, permissionIds: [...set] };
      }),
    );
  }

  deleteRole(id: string): void {
    this.roles.update((list) => list.filter((r) => r.id !== id));
  }

  submodulesForModule(moduleId: string): RbacSubmodule[] {
    return this.submodules().filter((s) => s.moduleId === moduleId);
  }

  permissionsForSubmodule(submoduleId: string): RbacPermission[] {
    return this.permissions().filter((p) => p.submoduleId === submoduleId);
  }
}
