package com.medcare.service.config;

import com.medcare.service.entity.*;
import com.medcare.service.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Seeds the configuration hub tables once when empty (matches the Angular mock defaults).
 */
@Slf4j
@Component
@Order(20)
@RequiredArgsConstructor
public class RbacConfigSeed implements CommandLineRunner {

    private final RbacModuleRepository moduleRepository;
    private final RbacSubmoduleRepository submoduleRepository;
    private final RbacPermissionRepository permissionRepository;
    private final RbacRoleRepository roleRepository;

    @Override
    @Transactional
    public void run(String... args) {
        if (moduleRepository.count() == 0) {
            seedModulesPermissionsAndRoles();
        }
        ensureReceptionistAndItSupportHubRoles();
    }

    private void seedModulesPermissionsAndRoles() {
        log.info("Seeding RBAC configuration hub defaults");

        RbacModuleEntity mPatient = mod("mod-patient", "Patient", "Patient demographics & visits");
        RbacModuleEntity mDoctor = mod("mod-doctor", "Doctor", "Doctor directory & schedules");
        RbacModuleEntity mAppt = mod("mod-appt", "Appointment", "Booking & scheduling");
        RbacModuleEntity mPh = mod("mod-pharmacy", "Pharmacy", "Medicines & inventory");
        RbacModuleEntity mCfg = mod("mod-config", "Configuration", "RBAC & system settings");
        moduleRepository.saveAll(List.of(mPatient, mDoctor, mAppt, mPh, mCfg));

        RbacSubmoduleEntity subP1 = sub("sub-p1", mPatient, "Create Patient");
        RbacSubmoduleEntity subP2 = sub("sub-p2", mPatient, "View Patient");
        RbacSubmoduleEntity subP3 = sub("sub-p3", mPatient, "Update Patient");
        RbacSubmoduleEntity subD1 = sub("sub-d1", mDoctor, "Add Doctor");
        RbacSubmoduleEntity subD2 = sub("sub-d2", mDoctor, "View Doctors");
        RbacSubmoduleEntity subA1 = sub("sub-a1", mAppt, "Book Appointment");
        RbacSubmoduleEntity subA2 = sub("sub-a2", mAppt, "Appointment History");
        RbacSubmoduleEntity subPh1 = sub("sub-ph1", mPh, "Inventory Management");
        RbacSubmoduleEntity subPh2 = sub("sub-ph2", mPh, "Low Stock Alerts");
        RbacSubmoduleEntity subC1 = sub("sub-c1", mCfg, "Module Management");
        RbacSubmoduleEntity subC2 = sub("sub-c2", mCfg, "Permission Control");
        submoduleRepository.saveAll(List.of(subP1, subP2, subP3, subD1, subD2, subA1, subA2, subPh1, subPh2, subC1, subC2));

        permissionRepository.saveAll(List.of(
                perm("perm-p1-r", subP2, "read", "Read", "PATIENT_MANAGEMENT_PATIENTS_READ", "READ"),
                perm("perm-p1-w", subP1, "create", "Create", "PATIENT_MANAGEMENT_PATIENTS_WRITE", "CREATE"),
                perm("perm-p1-u", subP3, "update", "Update", "PATIENT_MANAGEMENT_PATIENTS_WRITE", "UPDATE"),
                perm("perm-d-r", subD2, "read", "Read", "MEDCARE_DOCTORS_READ", "READ"),
                perm("perm-d-w", subD1, "create", "Create", "MEDCARE_DOCTORS_WRITE", "CREATE"),
                perm("perm-ap-r", subA2, "read", "Read", "MEDCARE_APPOINTMENTS_READ", "READ"),
                perm("perm-ap-w", subA1, "create", "Book", "MEDCARE_APPOINTMENTS_WRITE", "CREATE"),
                perm("perm-ph-r", subPh1, "read", "Read", "MEDCARE_PHARMACY_READ", "READ"),
                perm("perm-ph-exp", subPh2, "export", "Export alerts", "MEDCARE_PHARMACY_READ", "CUSTOM"),
                perm("perm-cfg", subC1, "admin", "Configure", "MEDCARE_USERS_WRITE", "CUSTOM")
        ));

        List<String> allMods = List.of("mod-patient", "mod-doctor", "mod-appt", "mod-pharmacy", "mod-config");
        List<String> allPerms = List.of(
                "perm-p1-r", "perm-p1-w", "perm-p1-u", "perm-d-r", "perm-d-w",
                "perm-ap-r", "perm-ap-w", "perm-ph-r", "perm-ph-exp", "perm-cfg");

        RbacRoleEntity admin = role("role-admin", "Admin", "Full configuration & all modules", allMods, allPerms);
        RbacRoleEntity doctor = role(
                "role-doctor",
                "Doctor",
                "Clinical workflows",
                List.of("mod-patient", "mod-appt", "mod-doctor"),
                List.of("perm-p1-r", "perm-ap-r", "perm-ap-w", "perm-d-r"));
        RbacRoleEntity patient = role(
                "role-patient",
                "Patient",
                "Self-service portal",
                List.of("mod-appt", "mod-patient"),
                List.of("perm-ap-r", "perm-ap-w", "perm-p1-r"));
        List<String> recvMods = List.of("mod-patient", "mod-doctor", "mod-appt", "mod-pharmacy");
        List<String> recvPerms = List.of(
                "perm-p1-r", "perm-p1-w", "perm-p1-u", "perm-d-r", "perm-d-w",
                "perm-ap-r", "perm-ap-w", "perm-ph-r", "perm-ph-exp");
        RbacRoleEntity receptionist = role(
                "role-receptionist",
                "Receptionist",
                "Front desk, patients, appointments, billing-related modules",
                recvMods,
                recvPerms);
        List<String> itMods = List.of("mod-config", "mod-patient", "mod-doctor", "mod-appt", "mod-pharmacy");
        List<String> itPerms = List.of("perm-cfg", "perm-p1-r", "perm-d-r", "perm-ap-r", "perm-ph-r");
        RbacRoleEntity itSupport = role(
                "role-it-support",
                "IT Support",
                "User management, audit, read-only operational views",
                itMods,
                itPerms);
        roleRepository.saveAll(List.of(admin, doctor, patient, receptionist, itSupport));
        log.info("RBAC seed complete: {} modules, {} roles", moduleRepository.count(), roleRepository.count());
    }

    /**
     * Older databases may have been seeded before receptionist / IT hub roles existed.
     */
    private void ensureReceptionistAndItSupportHubRoles() {
        if (moduleRepository.count() == 0) {
            return;
        }
        List<String> recvMods = List.of("mod-patient", "mod-doctor", "mod-appt", "mod-pharmacy");
        List<String> recvPerms = List.of(
                "perm-p1-r", "perm-p1-w", "perm-p1-u", "perm-d-r", "perm-d-w",
                "perm-ap-r", "perm-ap-w", "perm-ph-r", "perm-ph-exp");
        if (!roleRepository.existsById("role-receptionist")) {
            roleRepository.save(role(
                    "role-receptionist",
                    "Receptionist",
                    "Front desk, patients, appointments, pharmacy",
                    recvMods,
                    recvPerms));
            log.info("Added missing RBAC hub role: Receptionist");
        }
        List<String> itMods = List.of("mod-config", "mod-patient", "mod-doctor", "mod-appt", "mod-pharmacy");
        List<String> itPerms = List.of("perm-cfg", "perm-p1-r", "perm-d-r", "perm-ap-r", "perm-ph-r");
        if (!roleRepository.existsById("role-it-support")) {
            roleRepository.save(role(
                    "role-it-support",
                    "IT Support",
                    "User management, audit, read-only operational views",
                    itMods,
                    itPerms));
            log.info("Added missing RBAC hub role: IT Support");
        }
    }

    private static RbacModuleEntity mod(String id, String name, String desc) {
        RbacModuleEntity e = new RbacModuleEntity();
        e.setId(id);
        e.setName(name);
        e.setDescription(desc);
        e.setActive(true);
        return e;
    }

    private static RbacSubmoduleEntity sub(String id, RbacModuleEntity m, String name) {
        RbacSubmoduleEntity e = new RbacSubmoduleEntity();
        e.setId(id);
        e.setModule(m);
        e.setName(name);
        e.setActive(true);
        return e;
    }

    private static RbacPermissionEntity perm(
            String id, RbacSubmoduleEntity sub, String key, String label, String scope, String kind) {
        RbacPermissionEntity e = new RbacPermissionEntity();
        e.setId(id);
        e.setSubmodule(sub);
        e.setPermKey(key);
        e.setLabel(label);
        e.setScope(scope);
        e.setKind(kind);
        e.setActive(true);
        return e;
    }

    private static RbacRoleEntity role(String id, String name, String desc, List<String> mods, List<String> perms) {
        RbacRoleEntity e = new RbacRoleEntity();
        e.setId(id);
        e.setName(name);
        e.setDescription(desc);
        e.setActive(true);
        e.setModuleIds(new ArrayList<>(mods));
        e.setPermissionIds(new ArrayList<>(perms));
        return e;
    }
}
