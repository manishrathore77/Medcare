package com.medcare.service.repository;

import com.medcare.service.entity.RbacRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RbacRoleRepository extends JpaRepository<RbacRoleEntity, String> {
}
