package com.medcare.service.controller;

import com.medcare.service.auth.AuthRequest;
import com.medcare.service.auth.AuthResponse;
import com.medcare.service.auth.RegisterRequest;
import com.medcare.service.entity.Doctor;
import com.medcare.service.entity.Patient;
import com.medcare.service.entity.User;
import com.medcare.service.generic.dto.ApiResponse;
import com.medcare.service.repository.UserRepository;
import com.medcare.service.service.DoctorService;
import com.medcare.service.service.PatientService;
import com.medcare.service.security.JwtService;
import com.medcare.service.security.RolePermissionMapper;
import com.medcare.service.service.RegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Issues JWT access tokens after validating credentials against the user store.
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final RegistrationService registrationService;
    private final PatientService patientService;
    private final DoctorService doctorService;

    /**
     * Authenticates a user and returns a signed JWT plus granted permission scopes.
     *
     * @param request credentials (username and password); password is never logged
     * @return {@link AuthResponse} wrapped in {@link ApiResponse}, or 401 on failure
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody AuthRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
            String token = jwtService.generateToken(authentication.getName(), authentication.getAuthorities());
            User user = userRepository.findByUsername(authentication.getName()).orElseThrow();
            List<String> scopes = RolePermissionMapper.getScopes(user.getRole()).stream().sorted().toList();
            Long patientId = null;
            Long doctorId = null;
            if (user.getRole() == User.Role.PATIENT) {
                patientId = patientService.getByUserId(user.getId()).map(Patient::getId).orElse(null);
            } else if (user.getRole() == User.Role.DOCTOR) {
                doctorId = doctorService.getByUserId(user.getId()).map(Doctor::getId).orElse(null);
            }
            AuthResponse body = new AuthResponse(token, "Bearer", user.getRole().name(), scopes, patientId, doctorId);
            log.info("User authenticated: username={}, role={}", user.getUsername(), user.getRole());
            return ResponseEntity.ok(new ApiResponse<>(true, "OK", body, HttpStatus.OK.value()));
        } catch (BadCredentialsException ex) {
            log.warn("Failed login attempt for username={}", request.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(false, "Invalid username or password", null, HttpStatus.UNAUTHORIZED.value()));
        }
    }

    /**
     * Public sign-up: creates {@code PATIENT} + profile and returns a JWT (same shape as login).
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse body = registrationService.registerPatient(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Registered successfully", body, HttpStatus.CREATED.value()));
    }
}
