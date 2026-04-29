package com.medcare.service.service;

import com.medcare.service.config.dto.RbacDtos;
import com.medcare.service.config.dto.RbacDtos.*;
import com.medcare.service.entity.*;
import com.medcare.service.generic.exception.ResourceNotFoundException;
import com.medcare.service.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RbacConfigurationService {

    private final RbacModuleRepository moduleRepository;
    private final RbacSubmoduleRepository submoduleRepository;
    private final RbacPermissionRepository permissionRepository;
    private final RbacRoleRepository roleRepository;

    private static String uid(String prefix) {
        return prefix + "-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
    }

    @Transactional(readOnly = true)
    public List<RbacRoleDto> listRoles() {
        return roleRepository.findAll().stream().map(this::toRoleDto).toList();
    }

    @Transactional(readOnly = true)
    public RbacTreeResponse getTree() {
        List<RbacModuleEntity> mods = moduleRepository.findAll();
        List<RbacSubmoduleEntity> subs = submoduleRepository.findAllFetchingModule();
        List<RbacPermissionEntity> perms = permissionRepository.findAllFetchingSubmodule();

        Map<String, List<RbacSubmoduleEntity>> subByModule = subs.stream()
                .collect(Collectors.groupingBy(s -> s.getModule() != null ? s.getModule().getId() : ""));

        Map<String, List<RbacPermissionEntity>> permBySub = perms.stream()
                .collect(Collectors.groupingBy(p -> p.getSubmodule() != null ? p.getSubmodule().getId() : ""));

        List<RbacTreeModuleNode> nodes = new ArrayList<>();
        for (RbacModuleEntity m : mods) {
            List<RbacTreeSubmoduleNode> subNodes = new ArrayList<>();
            for (RbacSubmoduleEntity s : subByModule.getOrDefault(m.getId(), List.of())) {
                List<RbacPermissionDto> pDtos = permBySub.getOrDefault(s.getId(), List.of()).stream()
                        .map(this::toPermissionDto)
                        .toList();
                subNodes.add(new RbacTreeSubmoduleNode(toSubmoduleDto(s), pDtos));
            }
            nodes.add(new RbacTreeModuleNode(toModuleDto(m), subNodes));
        }
        return new RbacTreeResponse(nodes);
    }

    @Transactional
    public RbacModuleDto createModule(CreateModuleRequest req) {
        String n = req.name() == null ? "" : req.name().trim();
        if (n.isEmpty()) {
            throw new IllegalArgumentException("Module name is required");
        }
        RbacModuleEntity e = new RbacModuleEntity();
        e.setId(uid("mod"));
        e.setName(n);
        e.setDescription(req.description() == null ? "" : req.description().trim());
        e.setActive(true);
        moduleRepository.save(e);
        return toModuleDto(e);
    }

    @Transactional
    public RbacModuleDto updateModule(String id, String name, String description, Boolean active) {
        RbacModuleEntity e = moduleRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Module " + id));
        if (name != null) {
            e.setName(name.trim());
        }
        if (description != null) {
            e.setDescription(description.trim());
        }
        if (active != null) {
            e.setActive(active);
        }
        moduleRepository.save(e);
        return toModuleDto(e);
    }

    @Transactional
    public void deleteModule(String id) {
        RbacModuleEntity m = moduleRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Module " + id));
        List<RbacSubmoduleEntity> subs = submoduleRepository.findByModule_Id(m.getId());
        for (RbacSubmoduleEntity s : subs) {
            deleteSubmoduleAndPermissions(s.getId());
        }
        moduleRepository.delete(m);
        stripModuleFromRoles(id);
    }

    @Transactional
    public RbacSubmoduleDto createSubmodule(CreateSubmoduleRequest req) {
        RbacModuleEntity mod = moduleRepository.findById(req.moduleId())
                .orElseThrow(() -> new ResourceNotFoundException("Module " + req.moduleId()));
        String n = req.name() == null ? "" : req.name().trim();
        if (n.isEmpty()) {
            throw new IllegalArgumentException("Submodule name is required");
        }
        RbacSubmoduleEntity e = new RbacSubmoduleEntity();
        e.setId(uid("sub"));
        e.setModule(mod);
        e.setName(n);
        e.setActive(true);
        submoduleRepository.save(e);
        return toSubmoduleDto(e);
    }

    @Transactional
    public RbacSubmoduleDto updateSubmodule(String id, String name, Boolean active) {
        RbacSubmoduleEntity e = submoduleRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Submodule " + id));
        if (name != null) {
            e.setName(name.trim());
        }
        if (active != null) {
            e.setActive(active);
        }
        submoduleRepository.save(e);
        return toSubmoduleDto(e);
    }

    @Transactional
    public void deleteSubmodule(String id) {
        deleteSubmoduleAndPermissions(id);
    }

    private void deleteSubmoduleAndPermissions(String id) {
        List<RbacPermissionEntity> perms = permissionRepository.findBySubmodule_Id(id);
        Set<String> permIds = perms.stream().map(RbacPermissionEntity::getId).collect(Collectors.toSet());
        permissionRepository.deleteAll(perms);
        submoduleRepository.deleteById(id);
        stripPermissionsFromRoles(permIds);
    }

    @Transactional
    public RbacPermissionDto createPermission(CreatePermissionRequest req) {
        RbacSubmoduleEntity sub = submoduleRepository.findById(req.submoduleId())
                .orElseThrow(() -> new ResourceNotFoundException("Submodule " + req.submoduleId()));
        RbacPermissionEntity e = new RbacPermissionEntity();
        e.setId(uid("perm"));
        e.setSubmodule(sub);
        e.setPermKey(req.key().trim());
        e.setLabel(req.label().trim());
        e.setScope(req.scope().trim());
        e.setKind(req.kind().trim().toUpperCase(Locale.ROOT));
        e.setActive(true);
        permissionRepository.save(e);
        return toPermissionDto(e);
    }

    @Transactional
    public RbacPermissionDto updatePermission(String id, String label, String scope, String kind, Boolean active) {
        RbacPermissionEntity e = permissionRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Permission " + id));
        if (label != null) {
            e.setLabel(label.trim());
        }
        if (scope != null) {
            e.setScope(scope.trim());
        }
        if (kind != null) {
            e.setKind(kind.trim().toUpperCase(Locale.ROOT));
        }
        if (active != null) {
            e.setActive(active);
        }
        permissionRepository.save(e);
        return toPermissionDto(e);
    }

    @Transactional
    public void deletePermission(String id) {
        if (!permissionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Permission " + id);
        }
        permissionRepository.deleteById(id);
        stripPermissionsFromRoles(Set.of(id));
    }

    @Transactional
    public RbacRoleDto createRole(CreateRoleRequest req) {
        String n = req.name() == null ? "" : req.name().trim();
        if (n.isEmpty()) {
            throw new IllegalArgumentException("Role name is required");
        }
        RbacRoleEntity e = new RbacRoleEntity();
        e.setId(uid("role"));
        e.setName(n);
        e.setDescription(req.description() == null ? "" : req.description().trim());
        e.setActive(true);
        e.setModuleIds(new ArrayList<>());
        e.setPermissionIds(new ArrayList<>());
        roleRepository.save(e);
        return toRoleDto(e);
    }

    @Transactional
    public RbacRoleDto updateRole(String id, String name, String description, Boolean active) {
        RbacRoleEntity e = roleRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Role " + id));
        if (name != null) {
            e.setName(name.trim());
        }
        if (description != null) {
            e.setDescription(description.trim());
        }
        if (active != null) {
            e.setActive(active);
        }
        roleRepository.save(e);
        return toRoleDto(e);
    }

    @Transactional
    public RbacRoleDto setRoleModules(String roleId, List<String> moduleIds) {
        RbacRoleEntity e = roleRepository.findById(roleId).orElseThrow(() -> new ResourceNotFoundException("Role " + roleId));
        e.setModuleIds(new ArrayList<>(moduleIds == null ? List.of() : moduleIds));
        roleRepository.save(e);
        return toRoleDto(e);
    }

    @Transactional
    public RbacRoleDto setRolePermissions(String roleId, List<String> permissionIds) {
        RbacRoleEntity e = roleRepository.findById(roleId).orElseThrow(() -> new ResourceNotFoundException("Role " + roleId));
        e.setPermissionIds(new ArrayList<>(permissionIds == null ? List.of() : permissionIds));
        roleRepository.save(e);
        return toRoleDto(e);
    }

    @Transactional
    public void deleteRole(String id) {
        if (!roleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Role " + id);
        }
        roleRepository.deleteById(id);
    }

    private void stripModuleFromRoles(String moduleId) {
        for (RbacRoleEntity r : roleRepository.findAll()) {
            boolean changed = r.getModuleIds().remove(moduleId);
            if (changed) {
                roleRepository.save(r);
            }
        }
    }

    private void stripPermissionsFromRoles(Set<String> permissionIds) {
        if (permissionIds.isEmpty()) {
            return;
        }
        for (RbacRoleEntity r : roleRepository.findAll()) {
            boolean changed = r.getPermissionIds().removeIf(permissionIds::contains);
            if (changed) {
                roleRepository.save(r);
            }
        }
    }

    private RbacModuleDto toModuleDto(RbacModuleEntity e) {
        return new RbacModuleDto(e.getId(), e.getName(), e.getDescription(), e.isActive());
    }

    private RbacSubmoduleDto toSubmoduleDto(RbacSubmoduleEntity e) {
        String moduleId = e.getModule() != null ? e.getModule().getId() : "";
        return new RbacSubmoduleDto(e.getId(), moduleId, e.getName(), e.isActive());
    }

    private RbacPermissionDto toPermissionDto(RbacPermissionEntity e) {
        String subId = e.getSubmodule() != null ? e.getSubmodule().getId() : "";
        return new RbacPermissionDto(
                e.getId(),
                subId,
                e.getPermKey(),
                e.getLabel(),
                e.getScope(),
                e.getKind(),
                e.isActive());
    }

    private RbacRoleDto toRoleDto(RbacRoleEntity e) {
        return new RbacRoleDto(
                e.getId(),
                e.getName(),
                e.getDescription(),
                new ArrayList<>(e.getModuleIds()),
                new ArrayList<>(e.getPermissionIds()),
                e.isActive());
    }
}
