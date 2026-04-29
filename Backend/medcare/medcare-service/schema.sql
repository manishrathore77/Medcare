-- =============================================================================
-- Medcare database schema — matches medcare-service JPA entities (Spring Boot 3).
--
-- Load locally (example):
--   mysql -u root -p
--   CREATE DATABASE IF NOT EXISTS medcare_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
--   USE medcare_db;
--   SOURCE /path/to/medcare-service/schema.sql;
--
-- Or from shell:
--   mysql -u root -p medcare_db < schema.sql
--
-- Overview:
--   users (+ audit) → patients | doctors | staff (optional user link)
--   clinics, appointments (patient + doctor + clinic)
--   EMR: soap_notes, diagnoses, treatments, prescriptions (→ appointment)
--   lab_tests (→ appointment), lab_reports (→ lab_test)
--   invoices (1:1 appointment), payments (→ invoice)
--   medicines, inventory_logs (→ medicine)
--   attendance (→ staff), payroll (→ staff)
--   notifications, audit_logs (→ user)
--   rbac_* : configuration hub (modules → submodules → permissions; roles + join tables)
--
-- After import: start the API; DataInitializer seeds admin/doctor/receptionist/itsupport if missing.
-- =============================================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

CREATE DATABASE IF NOT EXISTS medcare_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE medcare_db;

-- ----------------------------------------------------------------------------- cleanup (idempotent re-run)
DROP TABLE IF EXISTS rbac_role_permission_ids;
DROP TABLE IF EXISTS rbac_role_module_ids;
DROP TABLE IF EXISTS rbac_permissions;
DROP TABLE IF EXISTS rbac_submodules;
DROP TABLE IF EXISTS rbac_roles;
DROP TABLE IF EXISTS rbac_modules;

DROP TABLE IF EXISTS payments;
DROP TABLE IF EXISTS invoices;
DROP TABLE IF EXISTS inventory_logs;
DROP TABLE IF EXISTS lab_reports;
DROP TABLE IF EXISTS lab_tests;
DROP TABLE IF EXISTS prescriptions;
DROP TABLE IF EXISTS treatments;
DROP TABLE IF EXISTS diagnoses;
DROP TABLE IF EXISTS soap_notes;
DROP TABLE IF EXISTS appointments;
DROP TABLE IF EXISTS payroll;
DROP TABLE IF EXISTS attendance;
DROP TABLE IF EXISTS notifications;
DROP TABLE IF EXISTS audit_logs;

DROP TABLE IF EXISTS medicines;
DROP TABLE IF EXISTS patients;
DROP TABLE IF EXISTS doctors;
DROP TABLE IF EXISTS staff;
DROP TABLE IF EXISTS clinics;
DROP TABLE IF EXISTS users;

SET FOREIGN_KEY_CHECKS = 1;

-- =============================================================================
-- 1. USERS
-- =============================================================================
CREATE TABLE users (
  id BIGINT NOT NULL AUTO_INCREMENT,
  username VARCHAR(255) NOT NULL,
  password VARCHAR(255) NULL,
  role VARCHAR(32) NULL,
  email VARCHAR(255) NULL,
  phone VARCHAR(255) NULL,
  is_active BIT(1) NULL,
  created_at DATETIME(6) NULL,
  updated_at DATETIME(6) NULL,
  created_by VARCHAR(255) NULL,
  updated_by VARCHAR(255) NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_users_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================================================
-- 2. CLINICS
-- =============================================================================
CREATE TABLE clinics (
  id BIGINT NOT NULL AUTO_INCREMENT,
  name VARCHAR(255) NULL,
  location VARCHAR(255) NULL,
  contact_number VARCHAR(255) NULL,
  created_at DATETIME(6) NULL,
  updated_at DATETIME(6) NULL,
  created_by VARCHAR(255) NULL,
  updated_by VARCHAR(255) NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================================================
-- 3. PATIENTS / DOCTORS / STAFF
-- =============================================================================
CREATE TABLE patients (
  id BIGINT NOT NULL AUTO_INCREMENT,
  user_id BIGINT NULL,
  first_name VARCHAR(255) NULL,
  last_name VARCHAR(255) NULL,
  gender VARCHAR(255) NULL,
  dob DATE NULL,
  address VARCHAR(255) NULL,
  emergency_contact VARCHAR(255) NULL,
  insurance_provider VARCHAR(255) NULL,
  insurance_number VARCHAR(255) NULL,
  created_at DATETIME(6) NULL,
  updated_at DATETIME(6) NULL,
  created_by VARCHAR(255) NULL,
  updated_by VARCHAR(255) NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_patients_user (user_id),
  CONSTRAINT fk_patients_user FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE doctors (
  id BIGINT NOT NULL AUTO_INCREMENT,
  user_id BIGINT NULL,
  first_name VARCHAR(255) NULL,
  last_name VARCHAR(255) NULL,
  specialty VARCHAR(255) NULL,
  license_number VARCHAR(255) NULL,
  contact_number VARCHAR(255) NULL,
  email VARCHAR(255) NULL,
  consultation_fee DOUBLE NULL,
  total_earnings DOUBLE NULL,
  is_active BIT(1) NULL,
  created_at DATETIME(6) NULL,
  updated_at DATETIME(6) NULL,
  created_by VARCHAR(255) NULL,
  updated_by VARCHAR(255) NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_doctors_license (license_number),
  UNIQUE KEY uk_doctors_user (user_id),
  CONSTRAINT fk_doctors_user FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE staff (
  id BIGINT NOT NULL AUTO_INCREMENT,
  user_id BIGINT NULL,
  name VARCHAR(255) NULL,
  department VARCHAR(255) NULL,
  role VARCHAR(255) NULL,
  salary DOUBLE NULL,
  is_active BIT(1) NULL,
  created_at DATETIME(6) NULL,
  updated_at DATETIME(6) NULL,
  created_by VARCHAR(255) NULL,
  updated_by VARCHAR(255) NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_staff_user (user_id),
  CONSTRAINT fk_staff_user FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================================================
-- 4. APPOINTMENTS
-- =============================================================================
CREATE TABLE appointments (
  id BIGINT NOT NULL AUTO_INCREMENT,
  patient_id BIGINT NULL,
  doctor_id BIGINT NULL,
  clinic_id BIGINT NULL,
  appointment_time DATETIME(6) NULL,
  appointment_type VARCHAR(32) NULL,
  status VARCHAR(32) NULL,
  created_at DATETIME(6) NULL,
  updated_at DATETIME(6) NULL,
  created_by VARCHAR(255) NULL,
  updated_by VARCHAR(255) NULL,
  PRIMARY KEY (id),
  KEY idx_appt_patient (patient_id),
  KEY idx_appt_doctor_time (doctor_id, appointment_time),
  KEY idx_appt_clinic (clinic_id),
  CONSTRAINT fk_appt_patient FOREIGN KEY (patient_id) REFERENCES patients (id),
  CONSTRAINT fk_appt_doctor FOREIGN KEY (doctor_id) REFERENCES doctors (id),
  CONSTRAINT fk_appt_clinic FOREIGN KEY (clinic_id) REFERENCES clinics (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================================================
-- 5. EMR (SOAP, diagnoses, treatments, prescriptions)
-- =============================================================================
CREATE TABLE soap_notes (
  id BIGINT NOT NULL AUTO_INCREMENT,
  appointment_id BIGINT NULL,
  subjective LONGTEXT NULL,
  objective LONGTEXT NULL,
  assessment LONGTEXT NULL,
  plan LONGTEXT NULL,
  created_at DATETIME(6) NULL,
  updated_at DATETIME(6) NULL,
  created_by VARCHAR(255) NULL,
  updated_by VARCHAR(255) NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_soap_appointment (appointment_id),
  CONSTRAINT fk_soap_appt FOREIGN KEY (appointment_id) REFERENCES appointments (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE diagnoses (
  id BIGINT NOT NULL AUTO_INCREMENT,
  appointment_id BIGINT NULL,
  diagnosis_name VARCHAR(255) NULL,
  severity VARCHAR(255) NULL,
  notes LONGTEXT NULL,
  created_at DATETIME(6) NULL,
  updated_at DATETIME(6) NULL,
  created_by VARCHAR(255) NULL,
  updated_by VARCHAR(255) NULL,
  PRIMARY KEY (id),
  KEY idx_dx_appt (appointment_id),
  CONSTRAINT fk_dx_appt FOREIGN KEY (appointment_id) REFERENCES appointments (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE treatments (
  id BIGINT NOT NULL AUTO_INCREMENT,
  appointment_id BIGINT NULL,
  treatment_name VARCHAR(255) NULL,
  start_date DATE NULL,
  end_date DATE NULL,
  instructions LONGTEXT NULL,
  created_at DATETIME(6) NULL,
  updated_at DATETIME(6) NULL,
  created_by VARCHAR(255) NULL,
  updated_by VARCHAR(255) NULL,
  PRIMARY KEY (id),
  KEY idx_tx_appt (appointment_id),
  CONSTRAINT fk_tx_appt FOREIGN KEY (appointment_id) REFERENCES appointments (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE prescriptions (
  id BIGINT NOT NULL AUTO_INCREMENT,
  appointment_id BIGINT NULL,
  medicine_name VARCHAR(255) NULL,
  dosage VARCHAR(255) NULL,
  frequency VARCHAR(255) NULL,
  duration VARCHAR(255) NULL,
  created_at DATETIME(6) NULL,
  updated_at DATETIME(6) NULL,
  created_by VARCHAR(255) NULL,
  updated_by VARCHAR(255) NULL,
  PRIMARY KEY (id),
  KEY idx_rx_appt (appointment_id),
  CONSTRAINT fk_rx_appt FOREIGN KEY (appointment_id) REFERENCES appointments (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================================================
-- 6. LAB
-- =============================================================================
CREATE TABLE lab_tests (
  id BIGINT NOT NULL AUTO_INCREMENT,
  appointment_id BIGINT NULL,
  test_name VARCHAR(255) NULL,
  normal_range VARCHAR(255) NULL,
  result_value VARCHAR(255) NULL,
  status VARCHAR(32) NULL,
  created_at DATETIME(6) NULL,
  updated_at DATETIME(6) NULL,
  created_by VARCHAR(255) NULL,
  updated_by VARCHAR(255) NULL,
  PRIMARY KEY (id),
  KEY idx_labtest_appt (appointment_id),
  CONSTRAINT fk_labtest_appt FOREIGN KEY (appointment_id) REFERENCES appointments (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE lab_reports (
  id BIGINT NOT NULL AUTO_INCREMENT,
  lab_test_id BIGINT NULL,
  report_file_url LONGTEXT NULL,
  created_at DATETIME(6) NULL,
  updated_at DATETIME(6) NULL,
  created_by VARCHAR(255) NULL,
  updated_by VARCHAR(255) NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_labreport_test (lab_test_id),
  CONSTRAINT fk_labreport_test FOREIGN KEY (lab_test_id) REFERENCES lab_tests (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================================================
-- 7. BILLING
-- =============================================================================
CREATE TABLE invoices (
  id BIGINT NOT NULL AUTO_INCREMENT,
  appointment_id BIGINT NULL,
  subtotal DOUBLE NULL,
  gst DOUBLE NULL,
  discount DOUBLE NULL,
  total_amount DOUBLE NULL,
  status VARCHAR(32) NULL,
  insurance_claim_no VARCHAR(255) NULL,
  insurance_status VARCHAR(32) NULL,
  created_at DATETIME(6) NULL,
  updated_at DATETIME(6) NULL,
  created_by VARCHAR(255) NULL,
  updated_by VARCHAR(255) NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_invoice_appt (appointment_id),
  CONSTRAINT fk_invoice_appt FOREIGN KEY (appointment_id) REFERENCES appointments (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE payments (
  id BIGINT NOT NULL AUTO_INCREMENT,
  invoice_id BIGINT NULL,
  amount DOUBLE NULL,
  payment_mode VARCHAR(32) NULL,
  payment_status VARCHAR(32) NULL,
  transaction_id VARCHAR(255) NULL,
  gateway VARCHAR(32) NULL,
  gateway_response LONGTEXT NULL,
  paid_at DATETIME(6) NULL,
  created_at DATETIME(6) NULL,
  updated_at DATETIME(6) NULL,
  created_by VARCHAR(255) NULL,
  updated_by VARCHAR(255) NULL,
  PRIMARY KEY (id),
  KEY idx_pay_invoice (invoice_id),
  CONSTRAINT fk_pay_invoice FOREIGN KEY (invoice_id) REFERENCES invoices (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================================================
-- 8. PHARMACY
-- =============================================================================
CREATE TABLE medicines (
  id BIGINT NOT NULL AUTO_INCREMENT,
  name VARCHAR(255) NULL,
  batch_no VARCHAR(255) NULL,
  expiry_date DATE NULL,
  stock_quantity INT NULL,
  reorder_level INT NULL,
  unit_price DOUBLE NULL,
  created_at DATETIME(6) NULL,
  updated_at DATETIME(6) NULL,
  created_by VARCHAR(255) NULL,
  updated_by VARCHAR(255) NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE inventory_logs (
  id BIGINT NOT NULL AUTO_INCREMENT,
  medicine_id BIGINT NULL,
  change_type VARCHAR(32) NULL,
  quantity INT NULL,
  reason VARCHAR(255) NULL,
  created_at DATETIME(6) NULL,
  updated_at DATETIME(6) NULL,
  created_by VARCHAR(255) NULL,
  updated_by VARCHAR(255) NULL,
  PRIMARY KEY (id),
  KEY idx_inv_med (medicine_id),
  CONSTRAINT fk_inv_med FOREIGN KEY (medicine_id) REFERENCES medicines (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================================================
-- 9. HR
-- =============================================================================
CREATE TABLE attendance (
  id BIGINT NOT NULL AUTO_INCREMENT,
  staff_id BIGINT NULL,
  date DATE NULL,
  check_in TIME NULL,
  check_out TIME NULL,
  created_at DATETIME(6) NULL,
  updated_at DATETIME(6) NULL,
  created_by VARCHAR(255) NULL,
  updated_by VARCHAR(255) NULL,
  PRIMARY KEY (id),
  KEY idx_att_staff (staff_id),
  CONSTRAINT fk_att_staff FOREIGN KEY (staff_id) REFERENCES staff (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE payroll (
  id BIGINT NOT NULL AUTO_INCREMENT,
  staff_id BIGINT NULL,
  month VARCHAR(255) NULL,
  base_salary DOUBLE NULL,
  deductions DOUBLE NULL,
  net_salary DOUBLE NULL,
  paid_on DATE NULL,
  created_at DATETIME(6) NULL,
  updated_at DATETIME(6) NULL,
  created_by VARCHAR(255) NULL,
  updated_by VARCHAR(255) NULL,
  PRIMARY KEY (id),
  KEY idx_payroll_staff (staff_id),
  CONSTRAINT fk_payroll_staff FOREIGN KEY (staff_id) REFERENCES staff (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================================================
-- 10. NOTIFICATIONS & AUDIT
-- =============================================================================
CREATE TABLE notifications (
  id BIGINT NOT NULL AUTO_INCREMENT,
  user_id BIGINT NULL,
  title VARCHAR(255) NULL,
  message TEXT NULL,
  channel VARCHAR(32) NULL,
  status VARCHAR(32) NULL,
  related_id BIGINT NULL,
  created_at DATETIME(6) NULL,
  updated_at DATETIME(6) NULL,
  created_by VARCHAR(255) NULL,
  updated_by VARCHAR(255) NULL,
  PRIMARY KEY (id),
  KEY idx_notif_user (user_id),
  CONSTRAINT fk_notif_user FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE audit_logs (
  id BIGINT NOT NULL AUTO_INCREMENT,
  user_id BIGINT NULL,
  action VARCHAR(255) NULL,
  entity_name VARCHAR(255) NULL,
  entity_id BIGINT NULL,
  old_value LONGTEXT NULL,
  new_value LONGTEXT NULL,
  created_at DATETIME(6) NULL,
  updated_at DATETIME(6) NULL,
  created_by VARCHAR(255) NULL,
  updated_by VARCHAR(255) NULL,
  PRIMARY KEY (id),
  KEY idx_audit_user (user_id),
  CONSTRAINT fk_audit_user FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================================================
-- 11. RBAC (configuration hub)
-- =============================================================================
CREATE TABLE rbac_modules (
  id VARCHAR(64) NOT NULL,
  name VARCHAR(255) NULL,
  description VARCHAR(1024) NULL,
  active BIT(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE rbac_submodules (
  id VARCHAR(64) NOT NULL,
  module_id VARCHAR(64) NOT NULL,
  name VARCHAR(255) NULL,
  active BIT(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (id),
  KEY idx_rbac_sub_mod (module_id),
  CONSTRAINT fk_rbac_sub_mod FOREIGN KEY (module_id) REFERENCES rbac_modules (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE rbac_permissions (
  id VARCHAR(64) NOT NULL,
  submodule_id VARCHAR(64) NOT NULL,
  perm_key VARCHAR(255) NULL,
  label VARCHAR(255) NULL,
  scope VARCHAR(255) NULL,
  kind VARCHAR(255) NULL,
  active BIT(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (id),
  KEY idx_rbac_perm_sub (submodule_id),
  CONSTRAINT fk_rbac_perm_sub FOREIGN KEY (submodule_id) REFERENCES rbac_submodules (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE rbac_roles (
  id VARCHAR(64) NOT NULL,
  name VARCHAR(255) NULL,
  description VARCHAR(2048) NULL,
  active BIT(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE rbac_role_module_ids (
  role_id VARCHAR(64) NOT NULL,
  module_id VARCHAR(64) NOT NULL,
  PRIMARY KEY (role_id, module_id),
  CONSTRAINT fk_rrm_role FOREIGN KEY (role_id) REFERENCES rbac_roles (id),
  CONSTRAINT fk_rrm_mod FOREIGN KEY (module_id) REFERENCES rbac_modules (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE rbac_role_permission_ids (
  role_id VARCHAR(64) NOT NULL,
  permission_id VARCHAR(64) NOT NULL,
  PRIMARY KEY (role_id, permission_id),
  CONSTRAINT fk_rrp_role FOREIGN KEY (role_id) REFERENCES rbac_roles (id),
  CONSTRAINT fk_rrp_perm FOREIGN KEY (permission_id) REFERENCES rbac_permissions (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

SET FOREIGN_KEY_CHECKS = 1;

-- =============================================================================
-- End of schema. Enum-like columns use VARCHAR (matches @Enumerated(STRING)).
-- =============================================================================
