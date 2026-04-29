package com.medcare.service.service;

import com.medcare.service.entity.SoapNote;

import java.util.List;
import java.util.Optional;

/**
 * Application service contract for SOAP-format clinical notes.
 */
public interface SoapNoteService {

    /**
     * @return all SOAP notes
     */
    List<SoapNote> getAll();

    /**
     * @param id note id
     * @return optional note when found
     */
    Optional<SoapNote> getById(Long id);

    /**
     * @param soapNote new SOAP note
     * @return persisted note
     */
    SoapNote createSoapNote(SoapNote soapNote);
}
