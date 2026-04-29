package com.medcare.service.service.impl;

import com.medcare.service.entity.SoapNote;
import com.medcare.service.repository.SoapNoteRepository;
import com.medcare.service.service.SoapNoteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Default {@link SoapNoteService} implementation using JPA repositories.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class SoapNoteServiceImpl implements SoapNoteService {

    private final SoapNoteRepository soapNoteRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<SoapNote> getAll() {
        log.debug("SoapNote listAll start");
        List<SoapNote> all = soapNoteRepository.findAll();
        log.debug("SoapNote listAll count={}", all.size());
        return all;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<SoapNote> getById(Long id) {
        log.debug("SoapNote lookup id={}", id);
        return soapNoteRepository.findById(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SoapNote createSoapNote(SoapNote soapNote) {
        SoapNote saved = soapNoteRepository.save(soapNote);
        log.info("SoapNote created id={} appointmentId={}",
                saved.getId(),
                saved.getAppointment() != null ? saved.getAppointment().getId() : null);
        return saved;
    }
}
