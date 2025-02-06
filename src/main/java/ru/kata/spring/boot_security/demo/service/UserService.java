package ru.kata.spring.boot_security.demo.service;

import ru.kata.spring.boot_security.demo.model.entity.User;

import java.util.List;

public interface UserService {
    void createUser(User user);

    void updateUser(Long userId, User updatedUser, Long[] rolesIds);

    void deleteUser(long id);

    User getUserOrCreateIfNotExists(long id);

    List<User> getAllUsers();

    User getByUsername(String username);

    boolean findByUsername(String username);

}
