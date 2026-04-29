/** RBAC entities — mock store, maps to future backend. */
export interface RbacModule {
  id: string;
  name: string;
  description: string;
  active: boolean;
}

export interface RbacSubmodule {
  id: string;
  moduleId: string;
  name: string;
  active: boolean;
}

export type PermissionKind = 'CREATE' | 'READ' | 'UPDATE' | 'DELETE' | 'CUSTOM';

export interface RbacPermission {
  id: string;
  submoduleId: string;
  key: string;
  label: string;
  scope: string;
  kind: PermissionKind;
  active: boolean;
}

export interface RbacRole {
  id: string;
  name: string;
  description: string;
  moduleIds: string[];
  permissionIds: string[];
  active: boolean;
}
