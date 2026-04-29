-- =============================================================================
-- Medcare: upgrade existing MySQL schema (e.g. medcare_db) to match current
-- Spring Data JPA entities in medcare-service.
--
-- Run AFTER backing up your database:
--   mysqldump -u root -p medcare_db > medcare_db_backup.sql
--
-- Apply:
--   mysql -u root -p medcare_db < medcare_db_upgrade_jpa.sql
--
-- Greenfield (no legacy data): use medcare-service/schema.sql instead of this script.
--
-- Then start the API with spring.jpa.hibernate.ddl-auto=update (default) so
-- Hibernate can add any remaining columns (audit fields, FK indexes, etc.).
--
-- Legacy tables NOT mapped by current code (safe to keep if you still need data):
--   schedules, medical_documents, doctor_slots, slot_bookings
-- =============================================================================

USE medcare_db;

-- ---------------------------------------------------------------------------
-- 1) Appointments: JPA enum includes PENDING; older dumps often omit it.
--    If this fails with "Data truncated", fix or delete rows with invalid status values.
-- ---------------------------------------------------------------------------
UPDATE appointments SET status = 'CONFIRMED' WHERE status IS NULL;
ALTER TABLE appointments
  MODIFY COLUMN status ENUM('PENDING','CONFIRMED','COMPLETED','CANCELLED','NO_SHOW') NULL DEFAULT 'PENDING';

-- ---------------------------------------------------------------------------
-- 2) Configuration hub (RBAC) — required by RbacConfigurationService /admin/config
-- ---------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS rbac_modules (
  id VARCHAR(64) NOT NULL PRIMARY KEY,
  name VARCHAR(255) NULL,
  description VARCHAR(1024) NULL,
  active BIT(1) NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS rbac_submodules (
  id VARCHAR(64) NOT NULL PRIMARY KEY,
  module_id VARCHAR(64) NOT NULL,
  name VARCHAR(255) NULL,
  active BIT(1) NOT NULL DEFAULT 1,
  CONSTRAINT fk_rbac_sub_module FOREIGN KEY (module_id) REFERENCES rbac_modules (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS rbac_permissions (
  id VARCHAR(64) NOT NULL PRIMARY KEY,
  submodule_id VARCHAR(64) NOT NULL,
  perm_key VARCHAR(255) NULL,
  label VARCHAR(255) NULL,
  scope VARCHAR(255) NULL,
  kind VARCHAR(255) NULL,
  active BIT(1) NOT NULL DEFAULT 1,
  CONSTRAINT fk_rbac_perm_sub FOREIGN KEY (submodule_id) REFERENCES rbac_submodules (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS rbac_roles (
  id VARCHAR(64) NOT NULL PRIMARY KEY,
  name VARCHAR(255) NULL,
  description VARCHAR(2048) NULL,
  active BIT(1) NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS rbac_role_module_ids (
  role_id VARCHAR(64) NOT NULL,
  module_id VARCHAR(64) NOT NULL,
  PRIMARY KEY (role_id, module_id),
  CONSTRAINT fk_rrm_role FOREIGN KEY (role_id) REFERENCES rbac_roles (id),
  CONSTRAINT fk_rrm_mod FOREIGN KEY (module_id) REFERENCES rbac_modules (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS rbac_role_permission_ids (
  role_id VARCHAR(64) NOT NULL,
  permission_id VARCHAR(64) NOT NULL,
  PRIMARY KEY (role_id, permission_id),
  CONSTRAINT fk_rrp_role FOREIGN KEY (role_id) REFERENCES rbac_roles (id),
  CONSTRAINT fk_rrp_perm FOREIGN KEY (permission_id) REFERENCES rbac_permissions (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------------
-- 3) Optional: if clinics has no updated_at (BaseEntity), uncomment and run once:
--    ALTER TABLE clinics ADD COLUMN updated_at DATETIME(6) NULL;
--    Ignore error 1060 (Duplicate column) if Hibernate already added it.
-- ---------------------------------------------------------------------------

-- ---------------------------------------------------------------------------
-- Done. Start the application once; let Hibernate ddl-auto=update align the rest
-- (audit columns, FK names, column types). If startup reports a column conflict,
-- fix the reported table manually or temporarily set ddl-auto=none after this script.
-- =============================================================================
