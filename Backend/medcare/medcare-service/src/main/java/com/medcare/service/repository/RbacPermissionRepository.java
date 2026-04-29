package com.medcare.service.repository;

import com.medcare.service.entity.RbacPermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RbacPermissionRepository extends JpaRepository<RbacPermissionEntity, String> {

    @Query("SELECT DISTINCT p FROM RbacPermissionEntity p JOIN FETCH p.submodule")
    List<RbacPermissionEntity> findAllFetchingSubmodule();

    List<RbacPermissionEntity> findBySubmodule_Id(String submoduleId);

    void deleteBySubmodule_Id(String submoduleId);
}
