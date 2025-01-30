package ru.kata.spring.boot_security.demo.init;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.kata.spring.boot_security.demo.dao.RoleDao;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Component
public class Init {

//    private final UserService userService;
//    private final RoleService roleService;
//    private final RoleDao roleDao;
//
//    @Autowired
//    public Init(UserService userService, RoleService roleService, RoleDao roleDao) {
//        this.userService = userService;
//        this.roleService = roleService;
//        this.roleDao = roleDao;
//    }
private UserService userService;
    private RoleService roleService;
    private RoleDao roleDao;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setRoleService(RoleService roleService) {
        this.roleService = roleService;
    }

    @Autowired
    public void setRoleDao(RoleDao roleDao) {
        this.roleDao = roleDao;
    }


    @PostConstruct
    public void initData() {
        if (userService == null || roleService == null || roleDao == null) {
            throw new IllegalStateException("Не удалось внедрить зависимости!");
        }

        System.out.println("Инициализация тестовых пользователей...");

        Role roleUser = roleService.findById(1L).get();

        User user1 = new User();
        user1.setUsername("testuser1");
        user1.setPassword("password1");
        user1.setName("Test");
        user1.setLastName("User1");
        user1.setEmail("testuser1@example.com");
        user1.setAge((byte) 30);

        user1.setRoles(Collections.singleton(roleUser), false);

//        userService.createUser(user1);
//
//        Role roleUser1 = roleService.findById(1L).get();
//        Role roleAdmin = roleService.findById(2L).get();
//
//        User user2 = new User();
//        user2.setUsername("admin");
//        user2.setPassword("password2");
//        user2.setName("TestAdmin");
//        user2.setLastName("AdminTest");
//        user2.setEmail("testadmin@example.com");
//        user2.setAge((byte) 42);
//
//        Set<Role> rolesForUser2 = new HashSet<>();
//        rolesForUser2.add(roleUser1);
//        rolesForUser2.add(roleAdmin);
//        user2.setRoles(rolesForUser2, false);
//
//        userService.createUser(user2);

        System.out.println("Тестовые пользователи успешно созданы.");
    }
}