import { Component, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { UserApiService, type UserResponseDto } from '../../../core/api/user-api.service';
import { SessionAuthService } from '../../../core/auth/session-auth.service';

@Component({
  selector: 'app-admin-users',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './admin-users.html',
  styleUrl: './admin-users.scss',
})
export class AdminUsers {
  private readonly api = inject(UserApiService);
  private readonly fb = inject(FormBuilder);
  protected readonly auth = inject(SessionAuthService);

  readonly rows = signal<UserResponseDto[]>([]);
  readonly error = signal<string | null>(null);
  readonly success = signal<string | null>(null);
  readonly loading = signal(true);
  readonly submitting = signal(false);
  readonly deletingId = signal<number | null>(null);
  readonly query = signal('');
  readonly role = signal('ALL');
  readonly filteredRows = computed(() => {
    const q = this.query().trim().toLowerCase();
    const role = this.role();
    return this.rows().filter((u) => {
      const matchesQuery =
        q.length === 0 ||
        u.username.toLowerCase().includes(q) ||
        (u.email ?? '').toLowerCase().includes(q) ||
        String(u.id).includes(q);
      const rowRole = u.role ?? 'Unknown';
      const matchesRole = role === 'ALL' || rowRole === role;
      return matchesQuery && matchesRole;
    });
  });
  readonly roles = computed(() => {
    const set = new Set<string>();
    for (const row of this.rows()) set.add(row.role ?? 'Unknown');
    return [...set];
  });

  readonly createForm = this.fb.nonNullable.group({
    username: ['', Validators.required],
    password: ['', [Validators.required, Validators.minLength(6)]],
    email: ['', [Validators.required, Validators.email]],
  });

  constructor() {
    this.refresh();
  }

  refresh(): void {
    this.loading.set(true);
    this.api.list(0, 200).subscribe({
      next: (p) => {
        this.rows.set(p.content);
        this.error.set(null);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Could not load users. You need MEDCARE_USERS_READ (admin or IT support JWT).');
        this.loading.set(false);
      },
    });
  }

  createUser(): void {
    this.success.set(null);
    if (this.createForm.invalid) {
      this.createForm.markAllAsTouched();
      return;
    }
    const v = this.createForm.getRawValue();
    this.submitting.set(true);
    this.api.create(v).subscribe({
      next: (res) => {
        this.submitting.set(false);
        if (res.success) {
          this.success.set('User created (default role PATIENT on server).');
          this.createForm.reset();
          this.refresh();
        } else {
          this.error.set(res.message ?? 'Create failed');
        }
      },
      error: (e) => {
        this.submitting.set(false);
        this.error.set(e?.error?.message ?? 'Create failed');
      },
    });
  }

  remove(row: UserResponseDto): void {
    if (this.auth.role() === 'IT_SUPPORT' && (row.role === 'ADMIN' || row.role === 'IT_SUPPORT')) {
      this.error.set('IT Support cannot remove administrator or other IT accounts from this screen.');
      return;
    }
    if (row.username === this.auth.displayName()) {
      this.error.set('You cannot delete your own account from this screen.');
      return;
    }
    if (!confirm(`Delete user "${row.username}"? This cannot be undone.`)) return;
    this.deletingId.set(row.id);
    this.api.delete(row.id).subscribe({
      next: (res) => {
        this.deletingId.set(null);
        if (res.success) {
          this.refresh();
        } else {
          this.error.set(res.message ?? 'Delete failed');
        }
      },
      error: (e) => {
        this.deletingId.set(null);
        this.error.set(e?.error?.message ?? 'Delete failed');
      },
    });
  }

  setQuery(v: string): void {
    this.query.set(v);
  }

  setRole(v: string): void {
    this.role.set(v);
  }
}
