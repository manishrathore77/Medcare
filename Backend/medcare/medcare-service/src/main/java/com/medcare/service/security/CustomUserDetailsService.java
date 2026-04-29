package com.medcare.service.security;

import com.medcare.service.entity.User;
import com.medcare.service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

/**
 * Loads users for Spring Security and attaches both {@code ROLE_*} and scope-style authorities.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Set<GrantedAuthority> authorities = new HashSet<>();
        User.Role role = user.getRole() != null ? user.getRole() : User.Role.PATIENT;
        authorities.add(new SimpleGrantedAuthority("ROLE_" + role.name()));
        RolePermissionMapper.getScopes(role).forEach(s -> authorities.add(new SimpleGrantedAuthority(s)));
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .disabled(Boolean.FALSE.equals(user.getIsActive()))
                .authorities(authorities)
                .build();
    }
}
