import { Injectable, computed, signal } from '@angular/core';

export type AppRole = 'ADMIN' | 'IT_SUPPORT' | 'PATIENT' | 'DOCTOR' | 'RECEPTIONIST';

const ROLE_KEY = 'medcare_role';
const TOKEN_KEY = 'medcare_token';
const NAME_KEY = 'medcare_display_name';
const PATIENT_ID_KEY = 'medcare_patient_id';
const DOCTOR_ID_KEY = 'medcare_doctor_id';

@Injectable({ providedIn: 'root' })
export class SessionAuthService {
  private readonly roleSig = signal<AppRole | null>(this.readRole());
  private readonly nameSig = signal<string>(this.readName());
  private readonly patientIdSig = signal<number | null>(this.readPatientId());
  private readonly doctorIdSig = signal<number | null>(this.readDoctorId());

  readonly role = this.roleSig.asReadonly();
  readonly displayName = this.nameSig.asReadonly();
  readonly patientId = this.patientIdSig.asReadonly();
  readonly doctorId = this.doctorIdSig.asReadonly();

  readonly isAdmin = computed(() => this.roleSig() === 'ADMIN');
  readonly isItSupport = computed(() => this.roleSig() === 'IT_SUPPORT');
  readonly isPatient = computed(() => this.roleSig() === 'PATIENT');
  readonly isDoctor = computed(() => this.roleSig() === 'DOCTOR');
  readonly isReceptionist = computed(() => this.roleSig() === 'RECEPTIONIST');
  readonly isLoggedIn = computed(() => this.roleSig() !== null);

  getToken(): string | null {
    return sessionStorage.getItem(TOKEN_KEY);
  }

  /** After successful API login as patient (or register). */
  setPatientSession(token: string, displayName = 'Patient', patientId?: number | null): void {
    sessionStorage.setItem(TOKEN_KEY, token);
    sessionStorage.setItem(ROLE_KEY, 'PATIENT');
    sessionStorage.setItem(NAME_KEY, displayName);
    this.applyPatientId(patientId);
    this.applyDoctorId(null);
    this.roleSig.set('PATIENT');
    this.nameSig.set(displayName);
  }

  /** Demo login without API (doctor, receptionist, admin). */
  setDemoRole(role: Exclude<AppRole, 'PATIENT'>, displayName: string): void {
    sessionStorage.removeItem(TOKEN_KEY);
    sessionStorage.removeItem(PATIENT_ID_KEY);
    sessionStorage.removeItem(DOCTOR_ID_KEY);
    sessionStorage.setItem(ROLE_KEY, role);
    sessionStorage.setItem(NAME_KEY, displayName);
    this.patientIdSig.set(null);
    this.doctorIdSig.set(null);
    this.roleSig.set(role);
    this.nameSig.set(displayName);
  }

  /** After successful API login for any role (JWT required for secured endpoints). */
  setAuthenticatedSession(
    token: string,
    role: AppRole,
    displayName: string,
    patientId?: number | null,
    doctorId?: number | null,
  ): void {
    sessionStorage.setItem(TOKEN_KEY, token);
    sessionStorage.setItem(ROLE_KEY, role);
    sessionStorage.setItem(NAME_KEY, displayName);
    this.applyPatientId(role === 'PATIENT' ? patientId : null);
    this.applyDoctorId(role === 'DOCTOR' ? doctorId : null);
    this.roleSig.set(role);
    this.nameSig.set(displayName);
  }

  logout(): void {
    sessionStorage.removeItem(TOKEN_KEY);
    sessionStorage.removeItem(ROLE_KEY);
    sessionStorage.removeItem(NAME_KEY);
    sessionStorage.removeItem(PATIENT_ID_KEY);
    sessionStorage.removeItem(DOCTOR_ID_KEY);
    this.roleSig.set(null);
    this.nameSig.set('');
    this.patientIdSig.set(null);
    this.doctorIdSig.set(null);
  }

  private applyPatientId(patientId?: number | null): void {
    if (patientId != null && patientId > 0) {
      sessionStorage.setItem(PATIENT_ID_KEY, String(patientId));
      this.patientIdSig.set(patientId);
    } else {
      sessionStorage.removeItem(PATIENT_ID_KEY);
      this.patientIdSig.set(null);
    }
  }

  private applyDoctorId(doctorId?: number | null): void {
    if (doctorId != null && doctorId > 0) {
      sessionStorage.setItem(DOCTOR_ID_KEY, String(doctorId));
      this.doctorIdSig.set(doctorId);
    } else {
      sessionStorage.removeItem(DOCTOR_ID_KEY);
      this.doctorIdSig.set(null);
    }
  }

  private readRole(): AppRole | null {
    const r = sessionStorage.getItem(ROLE_KEY);
    if (
      r === 'ADMIN' ||
      r === 'IT_SUPPORT' ||
      r === 'PATIENT' ||
      r === 'DOCTOR' ||
      r === 'RECEPTIONIST'
    )
      return r;
    return null;
  }

  private readName(): string {
    return sessionStorage.getItem(NAME_KEY) ?? '';
  }

  private readPatientId(): number | null {
    const raw = sessionStorage.getItem(PATIENT_ID_KEY);
    if (!raw) return null;
    const n = Number(raw);
    return Number.isFinite(n) ? n : null;
  }

  private readDoctorId(): number | null {
    const raw = sessionStorage.getItem(DOCTOR_ID_KEY);
    if (!raw) return null;
    const n = Number(raw);
    return Number.isFinite(n) ? n : null;
  }
}
