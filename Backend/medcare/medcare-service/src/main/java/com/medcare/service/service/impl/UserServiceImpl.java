package com.medcare.service.service.impl;

import com.medcare.service.entity.User;
import com.medcare.service.generic.exception.ResourceNotFoundException;
import com.medcare.service.repository.UserRepository;
import com.medcare.service.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Default {@link UserService} implementation using JPA repositories.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public User createUser(User user) {
        User saved = userRepository.save(user);
        log.info("User created userId={} username={}", saved.getId(), saved.getUsername());
        return saved;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<User> getById(Long id) {
        log.debug("User lookup by id={}", id);
        return userRepository.findById(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<User> getByUsername(String username) {
        log.debug("User lookup by username={}", username);
        return userRepository.findByUsername(username);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<User> getByEmail(String email) {
        log.debug("User lookup by email={}", email);
        return userRepository.findByEmail(email);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        log.debug("User listAll start");
        List<User> all = userRepository.findAll();
        log.debug("User listAll count={}", all.size());
        return all;
    }

    /**
     * {@inheritDoc}
     */
    /**
     * Updates credentials and profile fields from {@code patch} onto the persisted row.
     * Role, active flag, and phone are preserved unless a future API exposes them explicitly.
     */
    @Override
    public User updateUser(Long id, User patch) {
        log.info("User update id={} username={}", id, patch.getUsername());
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
        existing.setUsername(patch.getUsername());
        existing.setPassword(patch.getPassword());
        existing.setEmail(patch.getEmail());
        return userRepository.save(existing);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteUser(Long id) {
        log.info("User delete id={}", id);
        userRepository.deleteById(id);
    }
}
