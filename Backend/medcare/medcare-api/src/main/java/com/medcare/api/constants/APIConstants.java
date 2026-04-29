package com.medcare.api.constants;

/**
 * Shared constants for OpenAPI documentation and fine-grained JWT permission scopes.
 * <p>
 * {@link #DEFAULT_SCHEME} names the bearer security scheme in OpenAPI. Scope constants
 * match Spring Security authorities issued at login and referenced in
 * {@code @PreAuthorize} / {@code @SecurityRequirement}.
 * </p>
 */
public final class APIConstants {

    /** OpenAPI security scheme name (HTTP bearer JWT). */
    public static final String DEFAULT_SCHEME = "bearerAuth";

    /** OpenAPI / HTTP 201 response code string. */
    public static final String CREATED_CODE = "201";
    /** Standard description for HTTP 201 responses. */
    public static final String CREATED_CODE_MSG = "Created";
    /** OpenAPI / HTTP 200 response code string. */
    public static final String OK_CODE = "200";
    /** Standard description for HTTP 200 responses. */
    public static final String OK_CODE_MSG = "OK";
    /** OpenAPI / HTTP 404 response code string. */
    public static final String NOT_FOUND_CODE = "404";
    /** Standard description for HTTP 404 responses. */
    public static final String NOT_FOUND_CODE_MSG = "Not Found";

    /** Scope: read patient demographics. */
    public static final String PATIENT_MANAGEMENT_PATIENTS_READ = "PATIENT_MANAGEMENT_PATIENTS_READ";
    /** Scope: create/update/delete patients. */
    public static final String PATIENT_MANAGEMENT_PATIENTS_WRITE = "PATIENT_MANAGEMENT_PATIENTS_WRITE";

    /** Scope: read payment milestone / payment records. */
    public static final String PROCUREMENT_MANAGEMENT_PAYMENT_MILESTONES_READ = "PROCUREMENT_MANAGEMENT_PAYMENT_MILESTONES_READ";
    /** Scope: create or modify payment milestones. */
    public static final String PROCUREMENT_MANAGEMENT_PAYMENT_MILESTONES_WRITE = "PROCUREMENT_MANAGEMENT_PAYMENT_MILESTONES_WRITE";

    /** Scope: read billing invoices. */
    public static final String BILLING_MANAGEMENT_INVOICES_READ = "BILLING_MANAGEMENT_INVOICES_READ";
    /** Scope: create or modify invoices. */
    public static final String BILLING_MANAGEMENT_INVOICES_WRITE = "BILLING_MANAGEMENT_INVOICES_WRITE";

    /** Scope: read appointments. */
    public static final String MEDCARE_APPOINTMENTS_READ = "MEDCARE_APPOINTMENTS_READ";
    /** Scope: book or change appointments. */
    public static final String MEDCARE_APPOINTMENTS_WRITE = "MEDCARE_APPOINTMENTS_WRITE";

    /** Scope: read clinic directory. */
    public static final String MEDCARE_CLINICS_READ = "MEDCARE_CLINICS_READ";
    /** Scope: maintain clinics. */
    public static final String MEDCARE_CLINICS_WRITE = "MEDCARE_CLINICS_WRITE";

    /** Scope: read doctors. */
    public static final String MEDCARE_DOCTORS_READ = "MEDCARE_DOCTORS_READ";
    /** Scope: maintain doctor profiles. */
    public static final String MEDCARE_DOCTORS_WRITE = "MEDCARE_DOCTORS_WRITE";

    /** Scope: read user accounts. */
    public static final String MEDCARE_USERS_READ = "MEDCARE_USERS_READ";
    /** Scope: create or update users. */
    public static final String MEDCARE_USERS_WRITE = "MEDCARE_USERS_WRITE";

    /** Scope: read pharmacy catalog and stock movements. */
    public static final String MEDCARE_PHARMACY_READ = "MEDCARE_PHARMACY_READ";
    /** Scope: update pharmacy data. */
    public static final String MEDCARE_PHARMACY_WRITE = "MEDCARE_PHARMACY_WRITE";

    /** Scope: read notifications. */
    public static final String MEDCARE_NOTIFICATIONS_READ = "MEDCARE_NOTIFICATIONS_READ";
    /** Scope: send or update notifications. */
    public static final String MEDCARE_NOTIFICATIONS_WRITE = "MEDCARE_NOTIFICATIONS_WRITE";

    /** Scope: read HR aggregates (staff, attendance, payroll). */
    public static final String MEDCARE_HR_READ = "MEDCARE_HR_READ";
    /** Scope: modify HR records. */
    public static final String MEDCARE_HR_WRITE = "MEDCARE_HR_WRITE";

    /** Scope: read laboratory data. */
    public static final String MEDCARE_LAB_READ = "MEDCARE_LAB_READ";
    /** Scope: enter lab results and reports. */
    public static final String MEDCARE_LAB_WRITE = "MEDCARE_LAB_WRITE";

    /** Scope: read EMR content. */
    public static final String MEDCARE_EMR_READ = "MEDCARE_EMR_READ";
    /** Scope: document clinical encounters. */
    public static final String MEDCARE_EMR_WRITE = "MEDCARE_EMR_WRITE";

    /** Scope: view audit events. */
    public static final String MEDCARE_AUDIT_LOGS_READ = "MEDCARE_AUDIT_LOGS_READ";
    /** Scope: delete audit entries. */
    public static final String MEDCARE_AUDIT_LOGS_DELETE = "MEDCARE_AUDIT_LOGS_DELETE";

    private APIConstants() {
    }
}
