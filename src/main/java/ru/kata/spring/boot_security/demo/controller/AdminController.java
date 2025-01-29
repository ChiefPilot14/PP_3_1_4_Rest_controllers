package ru.kata.spring.boot_security.demo.controller;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    public AdminController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping
    public String userList(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "admin";
    }

    @GetMapping("/{id}/delete")
    public String deleteUser(@PathVariable("id") long id) {
        userService.deleteUser(id);
        return "redirect:/admin";
    }

    @GetMapping("/{id}/edit")
    public String editUser(@PathVariable("id") long id, Model model) {
        User user = userService.getUserOrCreateIfNotExists(id);
        model.addAttribute("user", user);
        List<Role> allRoles = roleService.getAllRoles();
        model.addAttribute("allRoles", allRoles);
        return "edit";

    }

    @PostMapping("/{id}/update")
    public String updateUser(
            @PathVariable("id") Long userId,
            @ModelAttribute("user") User user,
            @RequestParam(value = "roles[]", required = false) Long[] rolesIds
    ) {
        User currentUser = userService.getUserById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь с id=" + userId + " не найден"));

        if (!StringUtils.isEmpty(user.getPassword())) {
            currentUser.setPassword(user.getPassword());
        } else {
            user.setPassword(currentUser.getPassword());
        }

        currentUser.setName(user.getName());
        currentUser.setLastName(user.getLastName());
        currentUser.setAge(user.getAge());
        currentUser.setEmail(user.getEmail());

        Set<Role> roles = new HashSet<>();
        if (rolesIds != null) {
            for (Long roleId : rolesIds) {
                System.out.println("Проверка роли с id = " + roleId); // Добавляем логирование для отладки

                Optional<Role> optionalRole = roleService.findById(roleId);

                if (optionalRole.isPresent()) {
                    Role role = optionalRole.get();
                    roles.add(role);
                } else {
                    throw new EntityNotFoundException("Роль с id=" + roleId + " не найдена");
                }
            }
        }
        currentUser.setRoles(roles);

        userService.updateUser(currentUser);
        return "redirect:/admin";
    }
}
