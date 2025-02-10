package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.exception.EntityNotFoundException;
import ru.kata.spring.boot_security.demo.model.dto.UpdateUserRequest;
import ru.kata.spring.boot_security.demo.model.entity.Role;
import ru.kata.spring.boot_security.demo.model.entity.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.*;

@RestController
@RequestMapping("/api")
public class AdminApiController {
    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    public AdminApiController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }
    //Получение всех пользователей
    @GetMapping("/users")
    public List<User> getAdminPanel() {
        return userService.getAllUsers();
    }
    //Получение одного пользователя
    @GetMapping("/users/{id}")
    @ResponseBody
    public User getUserForEditing(@PathVariable("id") long id) {
        return userService.getUserOrCreateIfNotExists(id);
    }
    //Добавление пользователя
    @PostMapping("/users")
    @ResponseBody
    public ResponseEntity<?> registerUser(@RequestBody UpdateUserRequest request) {
        User user = request.getUser();
        Long[] rolesIds = request.getRoleIds();
        Set<Role> roles = new HashSet<>();
        for (Long roleId : rolesIds) {
            Role role = roleService.findById(roleId).orElseThrow(() ->
                    new EntityNotFoundException("Роль с id=" + roleId + " не найдена"));
            roles.add(role);
        }

        user.setRoles(roles, false);
        userService.createUser(user);

        return ResponseEntity.ok().build();
    }
    //Изменение пользователя
    @PutMapping("/users")
    @ResponseBody
    public ResponseEntity<?> updateUser(@RequestBody UpdateUserRequest request) {

        userService.updateUser(request.getId(), request.getUser(), request.getRoleIds());
        return ResponseEntity.ok().build();
    }
    //Удаление пользователя
    @DeleteMapping("/users/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteUser(@PathVariable("id") long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }
    //Получение всех ролей
    @GetMapping("/roles")
    public List<Role> getAllRoles() {
        return roleService.getAllRoles();
    }

}
