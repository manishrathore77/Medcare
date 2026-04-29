package com.medcare.service.repository;

import com.medcare.service.entity.RbacModuleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RbacModuleRepository extends JpaRepository<RbacModuleEntity, String> {
}
