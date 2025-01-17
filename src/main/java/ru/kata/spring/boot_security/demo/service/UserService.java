package ru.kata.spring.boot_security.demo.service;

import ru.kata.spring.boot_security.demo.dao.UserDao;
import ru.kata.spring.boot_security.demo.model.User;

import java.util.List;
import java.util.Optional;


public interface UserService {
    final UserDao userDao = null;

    public void createUser(User user);

    public void updateUser(User user);

    void removeUserById(long id);

    Optional<User> getUserById(long id);

    List<User> getAllUsers();

}
