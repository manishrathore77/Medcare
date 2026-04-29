package com.medcare.service.repository;

import com.medcare.service.entity.RbacSubmoduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RbacSubmoduleRepository extends JpaRepository<RbacSubmoduleEntity, String> {

    @Query("SELECT DISTINCT s FROM RbacSubmoduleEntity s JOIN FETCH s.module")
    List<RbacSubmoduleEntity> findAllFetchingModule();

    List<RbacSubmoduleEntity> findByModule_Id(String moduleId);

    void deleteByModule_Id(String moduleId);
}
