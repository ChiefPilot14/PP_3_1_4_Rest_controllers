package ru.kata.spring.boot_security.demo.service;

import org.springframework.security.core.userdetails.UserDetails;
import ru.kata.spring.boot_security.demo.dao.UserDao;
import ru.kata.spring.boot_security.demo.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    final UserDao userDao = null;

    public void createUser(User user);

    public void updateUser(User user);

    void deleteUser(long id);

    public User getUserOrCreateIfNotExists(long id);

    List<User> getAllUsers();

    public UserDetails loadUserByUsername(String username);

    public Optional<User> findByUsername(String username);
}
