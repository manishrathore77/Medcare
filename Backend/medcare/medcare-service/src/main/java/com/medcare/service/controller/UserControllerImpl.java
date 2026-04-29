package com.medcare.service.controller;


/**
 * REST implementation of {@link com.medcare.api.controller.UserController}.
 */

import com.medcare.api.controller.UserController;
import com.medcare.api.model.UserRequest;
import com.medcare.api.model.UserResponse;
import com.medcare.service.entity.User;
import com.medcare.service.generic.dto.ApiResponse;
import com.medcare.service.generic.dto.PagedResponse;
import com.medcare.service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class UserControllerImpl implements UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Override
    @PreAuthorize("hasAuthority('MEDCARE_USERS_READ')")
    public ResponseEntity<PagedResponse<UserResponse>> list(int page, int size) {
        List<User> all = userService.getAllUsers();
        int from = Math.min(page * size, all.size());
        int to = Math.min(from + size, all.size());
        List<UserResponse> content = all.subList(from, to).stream().map(this::toResponse).collect(Collectors.toList());
        PagedResponse<UserResponse> resp = new PagedResponse<>(content, all.size(), page, size,
                (all.size() + size - 1) / size, to == all.size());
        return ResponseEntity.ok(resp);
    }

    @Override
    @PreAuthorize("hasAuthority('MEDCARE_USERS_READ')")
    public ResponseEntity<ApiResponse<UserResponse>> getById(Long id) {
        return userService.getById(id)
                .map(u -> ResponseEntity.ok(new ApiResponse<>(true, "OK", toResponse(u), HttpStatus.OK.value())))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "Not found", null, HttpStatus.NOT_FOUND.value())));
    }

    @Override
    @PreAuthorize("hasAuthority('MEDCARE_USERS_WRITE')")
    public ResponseEntity<ApiResponse<UserResponse>> create(UserRequest req) {
        User u = new User();
        u.setUsername(req.getUsername());
        u.setPassword(passwordEncoder.encode(req.getPassword()));
        u.setEmail(req.getEmail());
        u.setRole(User.Role.PATIENT);
        User saved = userService.createUser(u);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Created", toResponse(saved), HttpStatus.CREATED.value()));
    }

    @Override
    @PreAuthorize("hasAuthority('MEDCARE_USERS_WRITE')")
    public ResponseEntity<ApiResponse<UserResponse>> update(Long id, UserRequest req) {
        User u = new User();
        u.setUsername(req.getUsername());
        u.setPassword(passwordEncoder.encode(req.getPassword()));
        u.setEmail(req.getEmail());
        User updated = userService.updateUser(id, u);
        return ResponseEntity.ok(new ApiResponse<>(true, "Updated", toResponse(updated), HttpStatus.OK.value()));
    }

    @Override
    @PreAuthorize("hasAuthority('MEDCARE_USERS_WRITE')")
    public ResponseEntity<ApiResponse<Void>> delete(Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Deleted", null, HttpStatus.OK.value()));
    }

    private UserResponse toResponse(User u) {
        UserResponse r = new UserResponse();
        r.setId(u.getId());
        r.setUsername(u.getUsername());
        r.setEmail(u.getEmail());
        r.setRole(u.getRole() != null ? u.getRole().name() : null);
        r.setActive(u.getIsActive());
        return r;
    }
}
