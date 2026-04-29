package com.medcare.service.config.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

/** JSON shapes aligned with the Angular configuration hub. */
public final class RbacDtos {

    private RbacDtos() {
    }

    public record RbacModuleDto(String id, String name, String description, boolean active) {
    }

    public record RbacSubmoduleDto(String id, String moduleId, String name, boolean active) {
    }

    public record RbacPermissionDto(
            String id,
            String submoduleId,
            String key,
            String label,
            String scope,
            String kind,
            boolean active) {
    }

    public record RbacRoleDto(
            String id,
            String name,
            String description,
            List<String> moduleIds,
            List<String> permissionIds,
            boolean active) {
    }

    public record RbacTreeSubmoduleNode(RbacSubmoduleDto submodule, List<RbacPermissionDto> permissions) {
    }

    public record RbacTreeModuleNode(RbacModuleDto module, List<RbacTreeSubmoduleNode> submodules) {
    }

    public record RbacTreeResponse(List<RbacTreeModuleNode> modules) {
    }

    public record CreateModuleRequest(@NotBlank String name, String description) {
    }

    public record CreateSubmoduleRequest(@NotBlank String moduleId, @NotBlank String name) {
    }

    public record CreatePermissionRequest(
            @NotBlank String submoduleId,
            @NotBlank String key,
            @NotBlank String label,
            @NotBlank String scope,
            @NotBlank String kind) {
    }

    public record CreateRoleRequest(@NotBlank String name, String description) {
    }
}
