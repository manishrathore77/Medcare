package com.medcare.service.controller;

import com.medcare.api.controller.AccountController;
import com.medcare.api.model.AccountProfileDto;
import com.medcare.service.entity.User;
import com.medcare.service.generic.dto.ApiResponse;
import com.medcare.service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AccountControllerImpl implements AccountController {

    private final UserRepository userRepository;

    @Override
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<AccountProfileDto>> me() {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        User u = userRepository.findByUsername(name).orElseThrow();
        AccountProfileDto dto = new AccountProfileDto(
                u.getId(),
                u.getUsername(),
                u.getEmail(),
                u.getRole() != null ? u.getRole().name() : null,
                u.getPhone(),
                u.getIsActive());
        return ResponseEntity.ok(new ApiResponse<>(true, "OK", dto, HttpStatus.OK.value()));
    }
}
