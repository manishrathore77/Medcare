import { HttpClient } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';
import { forkJoin, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { HttpErrorResponse } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { SessionAuthService } from '../auth/session-auth.service';
import { friendlyApiError } from '../http/api-error.util';
import {
  INITIAL_MODULES,
  INITIAL_PERMISSIONS,
  INITIAL_ROLES,
  INITIAL_SUBMODULES,
} from './mock-rbac.data';
import type { RbacModule, RbacPermission, RbacRole, RbacSubmodule } from './rbac.models';

interface ApiEnvelope<T> {
  success: boolean;
  message?: string;
  data: T | null;
  status: number;
}

interface TreeSubmoduleNode {
  submodule: RbacSubmodule;
  permissions: RbacPermission[];
}

interface TreeModuleNode {
  module: RbacModule;
  submodules: TreeSubmoduleNode[];
}

interface TreePayload {
  modules: TreeModuleNode[];
}

/**
 * Configuration hub backed by {@code /api/admin/config} when a JWT is present;
 * falls back to in-memory seed data for offline demo.
 */
@Injectable({ providedIn: 'root' })
export class ConfigHubDataService {
  private readonly http = inject(HttpClient);
  private readonly auth = inject(SessionAuthService);

  private readonly modules = signal<RbacModule[]>([]);
  private readonly submodules = signal<RbacSubmodule[]>([]);
  private readonly permissions = signal<RbacPermission[]>([]);
  private readonly roles = signal<RbacRole[]>([]);
  readonly apiError = signal<string | null>(null);

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

  constructor() {
    this.reload();
  }

  /** Call after login/logout or when opening admin configuration. */
  reload(): void {
    this.apiError.set(null);
    if (!this.auth.getToken()) {
      this.seedLocal();
      return;
    }
    forkJoin({
      tree: this.http.get<ApiEnvelope<TreePayload>>(`${environment.apiUrl}/api/admin/config/tree`),
      roles: this.http.get<ApiEnvelope<RbacRole[]>>(`${environment.apiUrl}/api/admin/config/roles`),
    })
      .pipe(
        catchError((err) => {
          const msg =
            err instanceof HttpErrorResponse
              ? friendlyApiError(err, 'Could not load configuration.')
              : 'Could not load configuration from API.';
          this.apiError.set(msg);
          this.seedLocal();
          return of(null);
        }),
      )
      .subscribe((res) => {
        if (!res?.tree?.data?.modules || !res.roles?.data) {
          return;
        }
        const flatM: RbacModule[] = [];
        const flatS: RbacSubmodule[] = [];
        const flatP: RbacPermission[] = [];
        for (const n of res.tree.data!.modules) {
          flatM.push(n.module);
          for (const sn of n.submodules) {
            flatS.push(sn.submodule);
            for (const p of sn.permissions) {
              flatP.push(p);
            }
          }
        }
        this.modules.set(flatM);
        this.submodules.set(flatS);
        this.permissions.set(flatP);
        this.roles.set(res.roles.data!);
        this.apiError.set(null);
      });
  }

  private seedLocal(): void {
    this.modules.set(structuredClone(INITIAL_MODULES));
    this.submodules.set(structuredClone(INITIAL_SUBMODULES));
    this.permissions.set(structuredClone(INITIAL_PERMISSIONS));
    this.roles.set(structuredClone(INITIAL_ROLES));
  }

  private base(): string {
    return `${environment.apiUrl}/api/admin/config`;
  }

  addModule(name: string, description: string): void {
    if (!this.auth.getToken()) {
      this.addModuleLocal(name, description);
      return;
    }
    this.http
      .post<ApiEnvelope<RbacModule>>(`${this.base()}/modules`, { name, description })
      .subscribe((r) => {
        if (r.success && r.data) {
          this.reload();
        }
      });
  }

  private addModuleLocal(name: string, description: string): void {
    const n = name.trim();
    if (!n || this.modules().some((m) => m.name.toLowerCase() === n.toLowerCase())) {
      return;
    }
    this.modules.update((list) => [
      ...list,
      { id: `mod-${Date.now().toString(36)}`, name: n, description: description.trim(), active: true },
    ]);
  }

  updateModule(id: string, patch: Partial<Pick<RbacModule, 'name' | 'description' | 'active'>>): void {
    if (!this.auth.getToken()) {
      this.modules.update((list) =>
        list.map((m) => (m.id === id ? { ...m, ...patch, name: patch.name?.trim() ?? m.name } : m)),
      );
      return;
    }
    this.http.put<ApiEnvelope<RbacModule>>(`${this.base()}/modules/${id}`, patch).subscribe(() => this.reload());
  }

  deleteModule(id: string): void {
    if (!this.auth.getToken()) {
      this.deleteModuleLocal(id);
      return;
    }
    this.http.delete<ApiEnvelope<unknown>>(`${this.base()}/modules/${id}`).subscribe(() => this.reload());
  }

  private deleteModuleLocal(id: string): void {
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

  addSubmodule(moduleId: string, name: string): void {
    if (!this.auth.getToken()) {
      this.addSubmoduleLocal(moduleId, name);
      return;
    }
    this.http
      .post<ApiEnvelope<RbacSubmodule>>(`${this.base()}/submodules`, { moduleId, name })
      .subscribe(() => this.reload());
  }

  private addSubmoduleLocal(moduleId: string, name: string): void {
    const n = name.trim();
    if (!n) {
      return;
    }
    this.submodules.update((list) => [
      ...list,
      { id: `sub-${Date.now().toString(36)}`, moduleId, name: n, active: true },
    ]);
  }

  updateSubmodule(id: string, patch: Partial<Pick<RbacSubmodule, 'name' | 'moduleId' | 'active'>>): void {
    if (!this.auth.getToken()) {
      this.submodules.update((list) =>
        list.map((s) => (s.id === id ? { ...s, ...patch, name: patch.name?.trim() ?? s.name } : s)),
      );
      return;
    }
    this.http.put<ApiEnvelope<RbacSubmodule>>(`${this.base()}/submodules/${id}`, patch).subscribe(() => this.reload());
  }

  deleteSubmodule(id: string): void {
    if (!this.auth.getToken()) {
      this.deleteSubmoduleLocal(id);
      return;
    }
    this.http.delete(`${this.base()}/submodules/${id}`).subscribe(() => this.reload());
  }

  private deleteSubmoduleLocal(id: string): void {
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

  addPermission(
    submoduleId: string,
    label: string,
    key: string,
    scope: string,
    kind: RbacPermission['kind'],
  ): void {
    if (!this.auth.getToken()) {
      this.addPermissionLocal(submoduleId, label, key, scope, kind);
      return;
    }
    this.http
      .post<ApiEnvelope<RbacPermission>>(`${this.base()}/permissions`, {
        submoduleId,
        key,
        label,
        scope,
        kind,
      })
      .subscribe(() => this.reload());
  }

  private addPermissionLocal(
    submoduleId: string,
    label: string,
    key: string,
    scope: string,
    kind: RbacPermission['kind'],
  ): void {
    const l = label.trim();
    const k = key.trim();
    const sc = scope.trim();
    if (!l || !k || !sc) {
      return;
    }
    this.permissions.update((list) => [
      ...list,
      {
        id: `perm-${Date.now().toString(36)}`,
        submoduleId,
        key: k,
        label: l,
        scope: sc,
        kind,
        active: true,
      },
    ]);
  }

  updatePermission(id: string, patch: Partial<Pick<RbacPermission, 'label' | 'scope' | 'kind' | 'active'>>): void {
    if (!this.auth.getToken()) {
      this.permissions.update((list) => list.map((p) => (p.id === id ? { ...p, ...patch } : p)));
      return;
    }
    this.http.put<ApiEnvelope<RbacPermission>>(`${this.base()}/permissions/${id}`, patch).subscribe(() => this.reload());
  }

  deletePermission(id: string): void {
    if (!this.auth.getToken()) {
      this.deletePermissionLocal(id);
      return;
    }
    this.http.delete(`${this.base()}/permissions/${id}`).subscribe(() => this.reload());
  }

  private deletePermissionLocal(id: string): void {
    this.permissions.update((list) => list.filter((p) => p.id !== id));
    this.roles.update((list) =>
      list.map((r) => ({ ...r, permissionIds: r.permissionIds.filter((pid) => pid !== id) })),
    );
  }

  addRole(name: string, description: string): void {
    if (!this.auth.getToken()) {
      this.addRoleLocal(name, description);
      return;
    }
    this.http.post<ApiEnvelope<RbacRole>>(`${this.base()}/roles`, { name, description }).subscribe(() => this.reload());
  }

  private addRoleLocal(name: string, description: string): void {
    const n = name.trim();
    if (!n) {
      return;
    }
    this.roles.update((list) => [
      ...list,
      {
        id: `role-${Date.now().toString(36)}`,
        name: n,
        description: description.trim(),
        moduleIds: [],
        permissionIds: [],
        active: true,
      },
    ]);
  }

  updateRole(id: string, patch: Partial<Pick<RbacRole, 'name' | 'description' | 'active'>>): void {
    if (!this.auth.getToken()) {
      this.roles.update((list) => list.map((r) => (r.id === id ? { ...r, ...patch } : r)));
      return;
    }
    this.http.put<ApiEnvelope<RbacRole>>(`${this.base()}/roles/${id}`, patch).subscribe(() => this.reload());
  }

  setRoleModules(roleId: string, moduleIds: string[]): void {
    if (!this.auth.getToken()) {
      this.roles.update((list) =>
        list.map((r) => (r.id === roleId ? { ...r, moduleIds: [...moduleIds] } : r)),
      );
      return;
    }
    this.http
      .put<ApiEnvelope<RbacRole>>(`${this.base()}/roles/${roleId}/modules`, { moduleIds })
      .subscribe(() => this.reload());
  }

  toggleRolePermission(roleId: string, permissionId: string, on: boolean): void {
    const r = this.roles().find((x) => x.id === roleId);
    if (!r) {
      return;
    }
    const set = new Set(r.permissionIds);
    if (on) {
      set.add(permissionId);
    } else {
      set.delete(permissionId);
    }
    this.setRolePermissions(roleId, [...set]);
  }

  setRolePermissions(roleId: string, permissionIds: string[]): void {
    if (!this.auth.getToken()) {
      this.roles.update((list) =>
        list.map((r) => (r.id === roleId ? { ...r, permissionIds: [...permissionIds] } : r)),
      );
      return;
    }
    this.http
      .put<ApiEnvelope<RbacRole>>(`${this.base()}/roles/${roleId}/permissions`, { permissionIds })
      .subscribe(() => this.reload());
  }

  deleteRole(id: string): void {
    if (!this.auth.getToken()) {
      this.roles.update((list) => list.filter((r) => r.id !== id));
      return;
    }
    this.http.delete(`${this.base()}/roles/${id}`).subscribe(() => this.reload());
  }

  submodulesForModule(moduleId: string): RbacSubmodule[] {
    return this.submodules().filter((s) => s.moduleId === moduleId);
  }

  permissionsForSubmodule(submoduleId: string): RbacPermission[] {
    return this.permissions().filter((p) => p.submoduleId === submoduleId);
  }
}
