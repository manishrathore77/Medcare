package com.medcare.service.security;

import com.medcare.api.constants.APIConstants;
import com.medcare.service.entity.User;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Maps each {@link User.Role} to the set of JWT scope strings granted at authentication time.
 */
public final class RolePermissionMapper {

    private static final Set<String> ALL_SCOPES = Set.of(
            APIConstants.PATIENT_MANAGEMENT_PATIENTS_READ,
            APIConstants.PATIENT_MANAGEMENT_PATIENTS_WRITE,
            APIConstants.PROCUREMENT_MANAGEMENT_PAYMENT_MILESTONES_READ,
            APIConstants.PROCUREMENT_MANAGEMENT_PAYMENT_MILESTONES_WRITE,
            APIConstants.BILLING_MANAGEMENT_INVOICES_READ,
            APIConstants.BILLING_MANAGEMENT_INVOICES_WRITE,
            APIConstants.MEDCARE_APPOINTMENTS_READ,
            APIConstants.MEDCARE_APPOINTMENTS_WRITE,
            APIConstants.MEDCARE_CLINICS_READ,
            APIConstants.MEDCARE_CLINICS_WRITE,
            APIConstants.MEDCARE_DOCTORS_READ,
            APIConstants.MEDCARE_DOCTORS_WRITE,
            APIConstants.MEDCARE_USERS_READ,
            APIConstants.MEDCARE_USERS_WRITE,
            APIConstants.MEDCARE_PHARMACY_READ,
            APIConstants.MEDCARE_PHARMACY_WRITE,
            APIConstants.MEDCARE_NOTIFICATIONS_READ,
            APIConstants.MEDCARE_NOTIFICATIONS_WRITE,
            APIConstants.MEDCARE_HR_READ,
            APIConstants.MEDCARE_HR_WRITE,
            APIConstants.MEDCARE_LAB_READ,
            APIConstants.MEDCARE_LAB_WRITE,
            APIConstants.MEDCARE_EMR_READ,
            APIConstants.MEDCARE_EMR_WRITE,
            APIConstants.MEDCARE_AUDIT_LOGS_READ,
            APIConstants.MEDCARE_AUDIT_LOGS_DELETE
    );

    private static final Map<User.Role, Set<String>> ROLE_TO_SCOPES = new EnumMap<>(User.Role.class);

    static {
        ROLE_TO_SCOPES.put(User.Role.ADMIN, new HashSet<>(ALL_SCOPES));

        ROLE_TO_SCOPES.put(User.Role.DOCTOR, Set.of(
                APIConstants.PATIENT_MANAGEMENT_PATIENTS_READ,
                APIConstants.MEDCARE_APPOINTMENTS_READ,
                APIConstants.MEDCARE_APPOINTMENTS_WRITE,
                APIConstants.MEDCARE_CLINICS_READ,
                APIConstants.MEDCARE_EMR_READ,
                APIConstants.MEDCARE_EMR_WRITE,
                APIConstants.MEDCARE_LAB_READ,
                APIConstants.MEDCARE_LAB_WRITE,
                APIConstants.MEDCARE_DOCTORS_READ
        ));

        ROLE_TO_SCOPES.put(User.Role.RECEPTIONIST, Set.of(
                APIConstants.PATIENT_MANAGEMENT_PATIENTS_READ,
                APIConstants.PATIENT_MANAGEMENT_PATIENTS_WRITE,
                APIConstants.MEDCARE_APPOINTMENTS_READ,
                APIConstants.MEDCARE_APPOINTMENTS_WRITE,
                APIConstants.MEDCARE_CLINICS_READ,
                APIConstants.MEDCARE_DOCTORS_READ,
                APIConstants.MEDCARE_PHARMACY_READ,
                APIConstants.MEDCARE_PHARMACY_WRITE,
                APIConstants.MEDCARE_NOTIFICATIONS_READ,
                APIConstants.MEDCARE_NOTIFICATIONS_WRITE,
                APIConstants.BILLING_MANAGEMENT_INVOICES_READ,
                APIConstants.BILLING_MANAGEMENT_INVOICES_WRITE,
                APIConstants.PROCUREMENT_MANAGEMENT_PAYMENT_MILESTONES_READ,
                APIConstants.PROCUREMENT_MANAGEMENT_PAYMENT_MILESTONES_WRITE,
                APIConstants.MEDCARE_LAB_READ,
                APIConstants.MEDCARE_LAB_WRITE
        ));

        ROLE_TO_SCOPES.put(User.Role.PATIENT, Set.of(
                APIConstants.PATIENT_MANAGEMENT_PATIENTS_READ,
                APIConstants.MEDCARE_APPOINTMENTS_READ,
                APIConstants.MEDCARE_APPOINTMENTS_WRITE,
                APIConstants.MEDCARE_CLINICS_READ,
                APIConstants.MEDCARE_DOCTORS_READ,
                APIConstants.MEDCARE_PHARMACY_READ,
                APIConstants.MEDCARE_LAB_READ
        ));

        ROLE_TO_SCOPES.put(User.Role.IT_SUPPORT, Set.of(
                APIConstants.MEDCARE_USERS_READ,
                APIConstants.MEDCARE_USERS_WRITE,
                APIConstants.MEDCARE_AUDIT_LOGS_READ,
                APIConstants.MEDCARE_AUDIT_LOGS_DELETE,
                APIConstants.BILLING_MANAGEMENT_INVOICES_READ,
                APIConstants.PROCUREMENT_MANAGEMENT_PAYMENT_MILESTONES_READ
        ));
    }

    private RolePermissionMapper() {
    }

    /**
     * @param role persisted application role; {@code null} yields an empty set
     * @return mutable copy of configured scopes for that role
     */
    public static Set<String> getScopes(User.Role role) {
        if (role == null) {
            return Set.of();
        }
        return new HashSet<>(ROLE_TO_SCOPES.getOrDefault(role, Set.of()));
    }
}
