package com.medcare.service.service.impl;

import com.medcare.service.auth.AuthResponse;
import com.medcare.service.auth.RegisterRequest;
import com.medcare.service.entity.Patient;
import com.medcare.service.entity.User;
import com.medcare.service.generic.exception.RegistrationConflictException;
import com.medcare.service.repository.UserRepository;
import com.medcare.service.security.JwtService;
import com.medcare.service.security.RolePermissionMapper;
import com.medcare.service.service.PatientService;
import com.medcare.service.service.RegistrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Registers {@code PATIENT} users with a linked patient record and issues a JWT.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RegistrationServiceImpl implements RegistrationService {

    private final UserRepository userRepository;
    private final PatientService patientService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    @Transactional
    public AuthResponse registerPatient(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername().trim()).isPresent()) {
            throw new RegistrationConflictException("Username already taken");
        }
        String email = request.getEmail().trim();
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RegistrationConflictException("Email already registered");
        }

        User user = new User();
        user.setUsername(request.getUsername().trim());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(email);
        user.setPhone(request.getPhone() != null ? request.getPhone().trim() : null);
        user.setRole(User.Role.PATIENT);
        user.setIsActive(true);
        User savedUser = userRepository.save(user);

        Patient patient = new Patient();
        patient.setUser(savedUser);
        patient.setFirstName(request.getFirstName().trim());
        patient.setLastName(request.getLastName().trim());
        patient.setGender(request.getGender());
        patient.setDob(request.getDob());
        patient.setAddress(request.getAddress());
        patient.setEmergencyContact(request.getEmergencyContact());
        Patient savedPatient = patientService.registerPatient(patient);

        UserDetails details = userDetailsService.loadUserByUsername(savedUser.getUsername());
        String token = jwtService.generateToken(savedUser.getUsername(), details.getAuthorities());
        List<String> scopes = RolePermissionMapper.getScopes(User.Role.PATIENT).stream().sorted().toList();
        log.info("Patient registered username={} patient record linked", savedUser.getUsername());
        return new AuthResponse(token, "Bearer", User.Role.PATIENT.name(), scopes, savedPatient.getId(), null);
    }
}
