import { Routes } from '@angular/router';
import {
  adminGuard,
  doctorGuard,
  itSupportGuard,
  patientGuard,
  receptionistGuard,
} from './core/guards/role.guard';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'login' },
  {
    path: 'login',
    loadComponent: () => import('./pages/login/login').then((m) => m.Login),
  },
  {
    path: 'register',
    loadComponent: () => import('./pages/register/register').then((m) => m.Register),
  },
  {
    path: 'admin',
    loadComponent: () => import('./layouts/admin-layout/admin-layout').then((m) => m.AdminLayout),
    canActivate: [adminGuard],
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'dashboard' },
      {
        path: 'dashboard',
        loadComponent: () =>
          import('./pages/admin/admin-dashboard/admin-dashboard').then((m) => m.AdminDashboard),
        data: { title: 'Admin dashboard' },
      },
      {
        path: 'patients/create',
        loadComponent: () =>
          import('./pages/admin/admin-patient-form/admin-patient-form').then((m) => m.AdminPatientForm),
        data: { title: 'Register patient' },
      },
      {
        path: 'patients/:id',
        loadComponent: () =>
          import('./pages/admin/admin-patient-form/admin-patient-form').then((m) => m.AdminPatientForm),
        data: { title: 'Patient' },
      },
      {
        path: 'patients',
        loadComponent: () =>
          import('./pages/admin/admin-patients/admin-patients').then((m) => m.AdminPatients),
        data: { title: 'Patients' },
      },
      {
        path: 'doctors/create',
        loadComponent: () =>
          import('./pages/admin/admin-doctor-form/admin-doctor-form').then((m) => m.AdminDoctorForm),
        data: { title: 'Add doctor' },
      },
      {
        path: 'doctors/:id',
        loadComponent: () =>
          import('./pages/admin/admin-doctor-form/admin-doctor-form').then((m) => m.AdminDoctorForm),
        data: { title: 'Doctor' },
      },
      {
        path: 'doctors',
        loadComponent: () =>
          import('./pages/admin/admin-doctors/admin-doctors').then((m) => m.AdminDoctors),
        data: { title: 'Doctors' },
      },
      {
        path: 'users',
        loadComponent: () => import('./pages/admin/admin-users/admin-users').then((m) => m.AdminUsers),
        data: { title: 'Users' },
      },
      {
        path: 'profile',
        loadComponent: () => import('./pages/admin/admin-profile/admin-profile').then((m) => m.AdminProfile),
        data: { title: 'Profile' },
      },
      {
        path: 'clinics/create',
        loadComponent: () =>
          import('./pages/admin/admin-clinic-form/admin-clinic-form').then((m) => m.AdminClinicForm),
        data: { title: 'Add clinic' },
      },
      {
        path: 'clinics/:id',
        loadComponent: () =>
          import('./pages/admin/admin-clinic-form/admin-clinic-form').then((m) => m.AdminClinicForm),
        data: { title: 'Clinic' },
      },
      {
        path: 'clinics',
        loadComponent: () => import('./pages/admin/admin-clinics/admin-clinics').then((m) => m.AdminClinics),
        data: { title: 'Clinics' },
      },
      {
        path: 'pharmacy/medicines/create',
        loadComponent: () =>
          import('./pages/admin/admin-pharmacy-medicine-form/admin-pharmacy-medicine-form').then(
            (m) => m.AdminPharmacyMedicineForm,
          ),
        data: { title: 'Add medicine' },
      },
      {
        path: 'pharmacy/medicines/:id',
        loadComponent: () =>
          import('./pages/admin/admin-pharmacy-medicine-form/admin-pharmacy-medicine-form').then(
            (m) => m.AdminPharmacyMedicineForm,
          ),
        data: { title: 'Medicine' },
      },
      {
        path: 'pharmacy/medicines',
        loadComponent: () =>
          import('./pages/admin/admin-pharmacy-medicines/admin-pharmacy-medicines').then(
            (m) => m.AdminPharmacyMedicines,
          ),
        data: { title: 'Medicines' },
      },
      {
        path: 'pharmacy/inventory',
        loadComponent: () =>
          import('./pages/admin/admin-pharmacy-inventory/admin-pharmacy-inventory').then(
            (m) => m.AdminPharmacyInventory,
          ),
        data: { title: 'Inventory' },
      },
      {
        path: 'pharmacy',
        loadComponent: () =>
          import('./pages/admin/admin-pharmacy-hub/admin-pharmacy-hub').then((m) => m.AdminPharmacyHub),
        data: { title: 'Pharmacy' },
      },
      {
        path: 'config',
        loadComponent: () =>
          import('./pages/admin/configuration-hub/configuration-hub').then((m) => m.ConfigurationHub),
        data: { title: 'Configuration' },
        children: [
          { path: '', pathMatch: 'full', redirectTo: 'modules' },
          {
            path: 'modules',
            loadComponent: () =>
              import('./pages/admin/config-modules/config-modules').then((m) => m.ConfigModules),
            data: { title: 'Modules' },
          },
          {
            path: 'submodules',
            loadComponent: () =>
              import('./pages/admin/config-submodules/config-submodules').then((m) => m.ConfigSubmodules),
            data: { title: 'Submodules' },
          },
          {
            path: 'permissions',
            loadComponent: () =>
              import('./pages/admin/config-permissions/config-permissions').then((m) => m.ConfigPermissions),
            data: { title: 'Permissions' },
          },
          {
            path: 'roles',
            loadComponent: () =>
              import('./pages/admin/config-roles/config-roles').then((m) => m.ConfigRoles),
            data: { title: 'Roles' },
          },
        ],
      },
    ],
  },
  {
    path: 'it-support',
    loadComponent: () =>
      import('./layouts/it-support-layout/it-support-layout').then((m) => m.ItSupportLayout),
    canActivate: [itSupportGuard],
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'dashboard' },
      {
        path: 'dashboard',
        loadComponent: () =>
          import('./pages/it-support/it-support-dashboard/it-support-dashboard').then(
            (m) => m.ItSupportDashboard,
          ),
        data: { title: 'IT dashboard' },
      },
      {
        path: 'users',
        loadComponent: () => import('./pages/admin/admin-users/admin-users').then((m) => m.AdminUsers),
        data: { title: 'Users' },
      },
      {
        path: 'audit-logs',
        loadComponent: () =>
          import('./pages/it-support/it-support-audit-logs/index').then((m) => m.ItSupportAuditLogs),
        data: { title: 'Audit logs' },
      },
      {
        path: 'billing',
        loadComponent: () =>
          import('./pages/it-support/it-support-billing/it-support-billing').then(
            (m) => m.ItSupportBilling,
          ),
        data: { title: 'Billing (read-only)' },
      },
      {
        path: 'profile',
        loadComponent: () =>
          import('./pages/it-support/it-support-profile/it-support-profile').then(
            (m) => m.ItSupportProfile,
          ),
        data: { title: 'Profile' },
      },
    ],
  },
  {
    path: 'patient',
    loadComponent: () =>
      import('./layouts/patient-layout/patient-layout').then((m) => m.PatientLayout),
    canActivate: [patientGuard],
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'dashboard' },
      {
        path: 'dashboard',
        loadComponent: () =>
          import('./pages/patient/patient-dashboard/patient-dashboard').then((m) => m.PatientDashboard),
        data: { title: 'Dashboard' },
      },
      {
        path: 'appointments/book',
        loadComponent: () =>
          import('./pages/patient/patient-book/patient-book').then((m) => m.PatientBook),
        data: { title: 'Book appointment' },
      },
      {
        path: 'appointments/history',
        loadComponent: () =>
          import('./pages/patient/patient-history/patient-history').then((m) => m.PatientHistory),
        data: { title: 'Appointment history' },
      },
      {
        path: 'medicines',
        loadComponent: () =>
          import('./pages/patient/patient-medicines/patient-medicines').then((m) => m.PatientMedicines),
        data: { title: 'Medicines' },
      },
      {
        path: 'ehr',
        loadComponent: () =>
          import('./pages/patient/patient-ehr-page/patient-ehr-page').then((m) => m.PatientEhrPage),
        data: { title: 'Electronic health record' },
      },
      {
        path: 'reports',
        loadComponent: () =>
          import('./pages/patient/patient-reports-page/patient-reports-page').then(
            (m) => m.PatientReportsPage,
          ),
        data: { title: 'Reports' },
      },
      {
        path: 'telemedicine',
        loadComponent: () =>
          import('./pages/patient/patient-telemedicine/patient-telemedicine').then(
            (m) => m.PatientTelemedicine,
          ),
        data: { title: 'Telemedicine' },
      },
      {
        path: 'profile',
        loadComponent: () =>
          import('./pages/patient/patient-profile/patient-profile').then((m) => m.PatientProfile),
        data: { title: 'Profile' },
      },
    ],
  },
  {
    path: 'doctor',
    loadComponent: () =>
      import('./layouts/doctor-layout/doctor-layout').then((m) => m.DoctorLayout),
    canActivate: [doctorGuard],
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'dashboard' },
      {
        path: 'dashboard',
        loadComponent: () =>
          import('./pages/doctor/doctor-dashboard/doctor-dashboard').then((m) => m.DoctorDashboard),
        data: { title: 'Dashboard' },
      },
      {
        path: 'patients/:id',
        loadComponent: () =>
          import('./pages/doctor/doctor-patient-detail/doctor-patient-detail').then(
            (m) => m.DoctorPatientDetail,
          ),
        data: { title: 'Patient' },
      },
      {
        path: 'patients',
        loadComponent: () =>
          import('./pages/doctor/doctor-patients/doctor-patients').then((m) => m.DoctorPatients),
        data: { title: 'Patients' },
      },
      {
        path: 'appointments',
        loadComponent: () =>
          import('./pages/doctor/doctor-appointments/doctor-appointments').then(
            (m) => m.DoctorAppointments,
          ),
        data: { title: 'Appointments' },
      },
      {
        path: 'availability',
        loadComponent: () =>
          import('./pages/doctor/doctor-availability/doctor-availability').then(
            (m) => m.DoctorAvailability,
          ),
        data: { title: 'Availability' },
      },
      {
        path: 'ehr',
        loadComponent: () => import('./pages/doctor/doctor-ehr/doctor-ehr').then((m) => m.DoctorEhr),
        data: { title: 'EHR' },
      },
      {
        path: 'reports',
        loadComponent: () =>
          import('./pages/doctor/doctor-reports/doctor-reports').then((m) => m.DoctorReports),
        data: { title: 'Reports' },
      },
      {
        path: 'telemedicine',
        loadComponent: () =>
          import('./pages/doctor/doctor-telemedicine/doctor-telemedicine').then(
            (m) => m.DoctorTelemedicine,
          ),
        data: { title: 'Telemedicine' },
      },
      {
        path: 'profile',
        loadComponent: () =>
          import('./pages/doctor/doctor-profile/doctor-profile').then((m) => m.DoctorProfile),
        data: { title: 'Profile' },
      },
    ],
  },
  {
    path: 'receptionist',
    loadComponent: () =>
      import('./layouts/receptionist-layout/receptionist-layout').then((m) => m.ReceptionistLayout),
    canActivate: [receptionistGuard],
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'dashboard' },
      {
        path: 'dashboard',
        loadComponent: () =>
          import('./pages/receptionist/receptionist-dashboard/receptionist-dashboard').then(
            (m) => m.ReceptionistDashboard,
          ),
        data: { title: 'Dashboard' },
      },
      {
        path: 'register-patient',
        loadComponent: () =>
          import('./pages/receptionist/receptionist-register-patient/receptionist-register-patient').then(
            (m) => m.ReceptionistRegisterPatient,
          ),
        data: { title: 'Register patient' },
      },
      {
        path: 'patients',
        loadComponent: () =>
          import('./pages/receptionist/receptionist-patients/receptionist-patients').then(
            (m) => m.ReceptionistPatients,
          ),
        data: { title: 'Patients' },
      },
      {
        path: 'book',
        loadComponent: () =>
          import('./pages/receptionist/receptionist-book/receptionist-book').then(
            (m) => m.ReceptionistBook,
          ),
        data: { title: 'Book appointment' },
      },
      {
        path: 'appointments',
        loadComponent: () =>
          import('./pages/receptionist/receptionist-appointments/receptionist-appointments').then(
            (m) => m.ReceptionistAppointments,
          ),
        data: { title: 'Appointments' },
      },
      {
        path: 'availability',
        loadComponent: () =>
          import('./pages/receptionist/receptionist-availability/receptionist-availability').then(
            (m) => m.ReceptionistAvailability,
          ),
        data: { title: 'Doctor availability' },
      },
      {
        path: 'billing',
        loadComponent: () =>
          import('./pages/receptionist/receptionist-billing/receptionist-billing').then(
            (m) => m.ReceptionistBilling,
          ),
        data: { title: 'Billing' },
      },
      {
        path: 'reports',
        loadComponent: () =>
          import('./pages/receptionist/receptionist-reports/receptionist-reports').then(
            (m) => m.ReceptionistReports,
          ),
        data: { title: 'Reports' },
      },
      {
        path: 'profile',
        loadComponent: () =>
          import('./pages/receptionist/receptionist-profile/receptionist-profile').then(
            (m) => m.ReceptionistProfile,
          ),
        data: { title: 'Profile' },
      },
    ],
  },
  { path: '**', redirectTo: 'login' },
];
