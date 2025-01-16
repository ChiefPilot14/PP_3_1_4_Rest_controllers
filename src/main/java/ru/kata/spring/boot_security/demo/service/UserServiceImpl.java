package ru.kata.spring.boot_security.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.kata.spring.boot_security.demo.dao.UserDao;
import ru.kata.spring.boot_security.demo.model.User;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserDao userDao;

    @Autowired
    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public void saveUser(String name, String lastName, byte age) {
        addUser(name, lastName, age);
    }

    @Override
    public void removeUserById(long id) {
        userDao.deleteById(id);
    }

    @Override
    public Optional<User> getUserById(long id) {
        return userDao.findById(id);
    }

    @Override
    public List<User> getAllUsers() {
        return userDao.findAll();
    }

    @Override
    public void addUser(String name, String lastName, byte age) {
        User user = new User(name, lastName, age);
        userDao.save(user);
    }

    @Override
    public void saveUserById(long id, String name, String lastName, byte age) {
        Optional<User> optionalUser = userDao.findById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setName(name);
            user.setLastName(lastName);
            user.setAge(age);
            userDao.save(user);
        }
    }

}
