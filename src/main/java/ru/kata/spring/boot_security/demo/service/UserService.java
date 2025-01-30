package ru.kata.spring.boot_security.demo.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.kata.spring.boot_security.demo.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    void createUser(User user);

    void updateUser(Long userId, User updatedUser, Long[] rolesIds);

    void deleteUser(long id);

    User getUserOrCreateIfNotExists(long id);

    List<User> getAllUsers();

    UserDetails loadUserByUsername(String username);

    Optional<User> findByUsername(String username);

    Optional<User> getUserById(long id);
}
