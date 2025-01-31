package ru.kata.spring.boot_security.demo.init;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.dao.RoleDao;
import ru.kata.spring.boot_security.demo.dao.UserDao;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;

import java.util.Collections;
import java.util.HashSet;

@Component
public class Init {

    private final UserDao userDao;
    private final RoleDao roleDao;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public Init(UserDao userDao, RoleDao roleDao, BCryptPasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.roleDao = roleDao;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    @Transactional
    public void init() {
        try {
            // Создание ролей
            Role roleUser = roleDao.findByName("ROLE_USER").orElseGet(() -> {
                Role newRole = new Role();
                newRole.setName("ROLE_USER");
                return roleDao.save(newRole);
            });

            Role roleAdmin = roleDao.findByName("ROLE_ADMIN").orElseGet(() -> {
                Role newRole = new Role();
                newRole.setName("ROLE_ADMIN");
                return roleDao.save(newRole);
            });

            // Создание пользователей
            if (userDao.findByUsername("user").isEmpty()) {
                User user = new User();
                user.setUsername("user");
                user.setPassword(passwordEncoder.encode("password"));
                user.setRoles(new HashSet<>(Collections.singletonList(roleUser)), false);
                user.setName("Test User");
                user.setLastName("Userovich");
                user.setAge((byte) 25);
                user.setEmail("user@example.com");
                userDao.save(user);
            }

            if (userDao.findByUsername("admin").isEmpty()) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin"));
                admin.setRoles(new HashSet<>(Collections.singletonList(roleAdmin)), false);
                admin.setName("Test Admin");
                admin.setLastName("Adminovich");
                admin.setAge((byte) 30);
                admin.setEmail("admin@example.com");
                userDao.save(admin);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize test data", e);
        }
    }
}