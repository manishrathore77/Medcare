package com.medcare.service.controller;

import com.medcare.service.config.dto.RbacDtos;
import com.medcare.service.config.dto.RbacDtos.*;
import com.medcare.service.generic.dto.ApiResponse;
import com.medcare.service.service.RbacConfigurationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST API for the administration "Configuration hub" (modules, submodules, permissions, roles).
 * Persists to relational tables; JWT must include {@code MEDCARE_USERS_READ} / {@code MEDCARE_USERS_WRITE}.
 */
@RestController
@RequestMapping("/api/admin/config")
@RequiredArgsConstructor
public class ConfigHubController {

    private final RbacConfigurationService rbac;

    @GetMapping("/tree")
    @PreAuthorize("hasAuthority('MEDCARE_USERS_READ')")
    public ResponseEntity<ApiResponse<RbacTreeResponse>> tree() {
        return ResponseEntity.ok(new ApiResponse<>(true, "OK", rbac.getTree(), HttpStatus.OK.value()));
    }

    @GetMapping("/roles")
    @PreAuthorize("hasAuthority('MEDCARE_USERS_READ')")
    public ResponseEntity<ApiResponse<List<RbacRoleDto>>> roles() {
        return ResponseEntity.ok(new ApiResponse<>(true, "OK", rbac.listRoles(), HttpStatus.OK.value()));
    }

    @PostMapping("/modules")
    @PreAuthorize("hasAuthority('MEDCARE_USERS_WRITE')")
    public ResponseEntity<ApiResponse<RbacModuleDto>> createModule(@Valid @RequestBody CreateModuleRequest body) {
        RbacModuleDto created = rbac.createModule(body);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Created", created, HttpStatus.CREATED.value()));
    }

    @PutMapping("/modules/{id}")
    @PreAuthorize("hasAuthority('MEDCARE_USERS_WRITE')")
    public ResponseEntity<ApiResponse<RbacModuleDto>> updateModule(
            @PathVariable String id,
            @RequestBody Map<String, Object> body) {
        String name = body.get("name") == null ? null : String.valueOf(body.get("name"));
        String description = body.get("description") == null ? null : String.valueOf(body.get("description"));
        Boolean active = body.get("active") instanceof Boolean b ? b : null;
        return ResponseEntity.ok(new ApiResponse<>(true, "OK", rbac.updateModule(id, name, description, active),
                HttpStatus.OK.value()));
    }

    @DeleteMapping("/modules/{id}")
    @PreAuthorize("hasAuthority('MEDCARE_USERS_WRITE')")
    public ResponseEntity<ApiResponse<Object>> deleteModule(@PathVariable String id) {
        rbac.deleteModule(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Deleted", null, HttpStatus.OK.value()));
    }

    @PostMapping("/submodules")
    @PreAuthorize("hasAuthority('MEDCARE_USERS_WRITE')")
    public ResponseEntity<ApiResponse<RbacSubmoduleDto>> createSubmodule(@Valid @RequestBody CreateSubmoduleRequest body) {
        RbacSubmoduleDto created = rbac.createSubmodule(body);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Created", created, HttpStatus.CREATED.value()));
    }

    @PutMapping("/submodules/{id}")
    @PreAuthorize("hasAuthority('MEDCARE_USERS_WRITE')")
    public ResponseEntity<ApiResponse<RbacSubmoduleDto>> updateSubmodule(
            @PathVariable String id,
            @RequestBody Map<String, Object> body) {
        String name = body.get("name") == null ? null : String.valueOf(body.get("name"));
        Boolean active = body.get("active") instanceof Boolean b ? b : null;
        return ResponseEntity.ok(new ApiResponse<>(true, "OK", rbac.updateSubmodule(id, name, active),
                HttpStatus.OK.value()));
    }

    @DeleteMapping("/submodules/{id}")
    @PreAuthorize("hasAuthority('MEDCARE_USERS_WRITE')")
    public ResponseEntity<ApiResponse<Object>> deleteSubmodule(@PathVariable String id) {
        rbac.deleteSubmodule(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Deleted", null, HttpStatus.OK.value()));
    }

    @PostMapping("/permissions")
    @PreAuthorize("hasAuthority('MEDCARE_USERS_WRITE')")
    public ResponseEntity<ApiResponse<RbacPermissionDto>> createPermission(@Valid @RequestBody CreatePermissionRequest body) {
        RbacPermissionDto created = rbac.createPermission(body);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Created", created, HttpStatus.CREATED.value()));
    }

    @PutMapping("/permissions/{id}")
    @PreAuthorize("hasAuthority('MEDCARE_USERS_WRITE')")
    public ResponseEntity<ApiResponse<RbacPermissionDto>> updatePermission(
            @PathVariable String id,
            @RequestBody Map<String, Object> body) {
        String label = body.get("label") == null ? null : String.valueOf(body.get("label"));
        String scope = body.get("scope") == null ? null : String.valueOf(body.get("scope"));
        String kind = body.get("kind") == null ? null : String.valueOf(body.get("kind"));
        Boolean active = body.get("active") instanceof Boolean b ? b : null;
        return ResponseEntity.ok(new ApiResponse<>(true, "OK", rbac.updatePermission(id, label, scope, kind, active),
                HttpStatus.OK.value()));
    }

    @DeleteMapping("/permissions/{id}")
    @PreAuthorize("hasAuthority('MEDCARE_USERS_WRITE')")
    public ResponseEntity<ApiResponse<Object>> deletePermission(@PathVariable String id) {
        rbac.deletePermission(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Deleted", null, HttpStatus.OK.value()));
    }

    @PostMapping("/roles")
    @PreAuthorize("hasAuthority('MEDCARE_USERS_WRITE')")
    public ResponseEntity<ApiResponse<RbacRoleDto>> createRole(@Valid @RequestBody CreateRoleRequest body) {
        RbacRoleDto created = rbac.createRole(body);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Created", created, HttpStatus.CREATED.value()));
    }

    @PutMapping("/roles/{id}")
    @PreAuthorize("hasAuthority('MEDCARE_USERS_WRITE')")
    public ResponseEntity<ApiResponse<RbacRoleDto>> updateRole(
            @PathVariable String id,
            @RequestBody Map<String, Object> body) {
        String name = body.get("name") == null ? null : String.valueOf(body.get("name"));
        String description = body.get("description") == null ? null : String.valueOf(body.get("description"));
        Boolean active = body.get("active") instanceof Boolean b ? b : null;
        return ResponseEntity.ok(new ApiResponse<>(true, "OK", rbac.updateRole(id, name, description, active),
                HttpStatus.OK.value()));
    }

    @PutMapping("/roles/{id}/modules")
    @PreAuthorize("hasAuthority('MEDCARE_USERS_WRITE')")
    public ResponseEntity<ApiResponse<RbacRoleDto>> setRoleModules(
            @PathVariable String id,
            @RequestBody Map<String, List<String>> body) {
        List<String> mids = body.get("moduleIds");
        return ResponseEntity.ok(new ApiResponse<>(true, "OK", rbac.setRoleModules(id, mids), HttpStatus.OK.value()));
    }

    @PutMapping("/roles/{id}/permissions")
    @PreAuthorize("hasAuthority('MEDCARE_USERS_WRITE')")
    public ResponseEntity<ApiResponse<RbacRoleDto>> setRolePermissions(
            @PathVariable String id,
            @RequestBody Map<String, List<String>> body) {
        List<String> pids = body.get("permissionIds");
        return ResponseEntity.ok(new ApiResponse<>(true, "OK", rbac.setRolePermissions(id, pids), HttpStatus.OK.value()));
    }

    @DeleteMapping("/roles/{id}")
    @PreAuthorize("hasAuthority('MEDCARE_USERS_WRITE')")
    public ResponseEntity<ApiResponse<Object>> deleteRole(@PathVariable String id) {
        rbac.deleteRole(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Deleted", null, HttpStatus.OK.value()));
    }
}
