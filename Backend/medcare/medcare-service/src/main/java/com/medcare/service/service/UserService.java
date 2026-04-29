package com.medcare.service.service;

import com.medcare.service.entity.User;

import java.util.List;
import java.util.Optional;

/**
 * Application service contract for application user accounts.
 */
public interface UserService {

    /**
     * Creates a new user record.
     *
     * @param user populated entity; password should already be encoded when applicable
     * @return saved user with id
     */
    User createUser(User user);

    /**
     * @param id user id
     * @return optional user when found
     */
    Optional<User> getById(Long id);

    /**
     * @param username unique username
     * @return optional user when found
     */
    Optional<User> getByUsername(String username);

    /**
     * @param email email address
     * @return optional user when found
     */
    Optional<User> getByEmail(String email);

    /**
     * @return all users
     */
    List<User> getAllUsers();

    /**
     * Updates an existing user.
     *
     * @param id   user id
     * @param user field values to persist
     * @return updated entity
     */
    User updateUser(Long id, User user);

    /**
     * Deletes a user by id.
     *
     * @param id user id
     */
    void deleteUser(Long id);
}
