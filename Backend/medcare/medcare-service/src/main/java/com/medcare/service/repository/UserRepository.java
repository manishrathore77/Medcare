package com.medcare.service.repository;


/**
 * Spring Data repository for {@link com.medcare.service.entity.User} persistence.
 */

import com.medcare.service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);
}
