package com.medcare.service.repository;


/**
 * Spring Data repository for {@link com.medcare.service.entity.SoapNote} persistence.
 */

import com.medcare.service.entity.SoapNote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SoapNoteRepository extends JpaRepository<SoapNote, Long> {
}
