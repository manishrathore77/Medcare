import type { RbacModule, RbacPermission, RbacRole, RbacSubmodule } from './rbac.models';

export const INITIAL_MODULES: RbacModule[] = [
  { id: 'mod-patient', name: 'Patient', description: 'Patient demographics & visits', active: true },
  { id: 'mod-doctor', name: 'Doctor', description: 'Doctor directory & schedules', active: true },
  { id: 'mod-appt', name: 'Appointment', description: 'Booking & scheduling', active: true },
  { id: 'mod-pharmacy', name: 'Pharmacy', description: 'Medicines & inventory', active: true },
  { id: 'mod-config', name: 'Configuration', description: 'RBAC & system settings', active: true },
];

export const INITIAL_SUBMODULES: RbacSubmodule[] = [
  { id: 'sub-p1', moduleId: 'mod-patient', name: 'Create Patient', active: true },
  { id: 'sub-p2', moduleId: 'mod-patient', name: 'View Patient', active: true },
  { id: 'sub-p3', moduleId: 'mod-patient', name: 'Update Patient', active: true },
  { id: 'sub-d1', moduleId: 'mod-doctor', name: 'Add Doctor', active: true },
  { id: 'sub-d2', moduleId: 'mod-doctor', name: 'View Doctors', active: true },
  { id: 'sub-a1', moduleId: 'mod-appt', name: 'Book Appointment', active: true },
  { id: 'sub-a2', moduleId: 'mod-appt', name: 'Appointment History', active: true },
  { id: 'sub-ph1', moduleId: 'mod-pharmacy', name: 'Inventory Management', active: true },
  { id: 'sub-ph2', moduleId: 'mod-pharmacy', name: 'Low Stock Alerts', active: true },
  { id: 'sub-c1', moduleId: 'mod-config', name: 'Module Management', active: true },
  { id: 'sub-c2', moduleId: 'mod-config', name: 'Permission Control', active: true },
];

export const INITIAL_PERMISSIONS: RbacPermission[] = [
  { id: 'perm-p1-r', submoduleId: 'sub-p2', key: 'read', label: 'Read', scope: 'PATIENT_MANAGEMENT_PATIENTS_READ', kind: 'READ', active: true },
  { id: 'perm-p1-w', submoduleId: 'sub-p1', key: 'create', label: 'Create', scope: 'PATIENT_MANAGEMENT_PATIENTS_WRITE', kind: 'CREATE', active: true },
  { id: 'perm-p1-u', submoduleId: 'sub-p3', key: 'update', label: 'Update', scope: 'PATIENT_MANAGEMENT_PATIENTS_WRITE', kind: 'UPDATE', active: true },
  { id: 'perm-d-r', submoduleId: 'sub-d2', key: 'read', label: 'Read', scope: 'MEDCARE_DOCTORS_READ', kind: 'READ', active: true },
  { id: 'perm-d-w', submoduleId: 'sub-d1', key: 'create', label: 'Create', scope: 'MEDCARE_DOCTORS_WRITE', kind: 'CREATE', active: true },
  { id: 'perm-ap-r', submoduleId: 'sub-a2', key: 'read', label: 'Read', scope: 'MEDCARE_APPOINTMENTS_READ', kind: 'READ', active: true },
  { id: 'perm-ap-w', submoduleId: 'sub-a1', key: 'create', label: 'Book', scope: 'MEDCARE_APPOINTMENTS_WRITE', kind: 'CREATE', active: true },
  { id: 'perm-ph-r', submoduleId: 'sub-ph1', key: 'read', label: 'Read', scope: 'MEDCARE_PHARMACY_READ', kind: 'READ', active: true },
  { id: 'perm-ph-exp', submoduleId: 'sub-ph2', key: 'export', label: 'Export alerts', scope: 'MEDCARE_PHARMACY_READ', kind: 'CUSTOM', active: true },
  { id: 'perm-cfg', submoduleId: 'sub-c1', key: 'admin', label: 'Configure', scope: 'MEDCARE_USERS_WRITE', kind: 'CUSTOM', active: true },
];

export const INITIAL_ROLES: RbacRole[] = [
  {
    id: 'role-admin',
    name: 'Admin',
    description: 'Full configuration & all modules',
    moduleIds: INITIAL_MODULES.map((m) => m.id),
    permissionIds: INITIAL_PERMISSIONS.map((p) => p.id),
    active: true,
  },
  {
    id: 'role-doctor',
    name: 'Doctor',
    description: 'Clinical workflows',
    moduleIds: ['mod-patient', 'mod-appt', 'mod-doctor'],
    permissionIds: ['perm-p1-r', 'perm-ap-r', 'perm-ap-w', 'perm-d-r'],
    active: true,
  },
  {
    id: 'role-patient',
    name: 'Patient',
    description: 'Self-service portal',
    moduleIds: ['mod-appt', 'mod-patient'],
    permissionIds: ['perm-ap-r', 'perm-ap-w', 'perm-p1-r'],
    active: true,
  },
];
