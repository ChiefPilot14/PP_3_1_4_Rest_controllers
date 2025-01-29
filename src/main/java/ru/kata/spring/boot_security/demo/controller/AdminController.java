package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.exception.EntityNotFoundException;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.HashSet;
import java.util.List;
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
        user.setId(userId);

        Set<Role> roles = new HashSet<>();
        if (rolesIds != null) {
            for (Long roleId : rolesIds) {
                Role role = roleService.findById(roleId)
                        .orElseThrow(() -> new EntityNotFoundException("Роль с id=" + roleId + " не найдена"));
                roles.add(role);
            }
        }

        user.setRoles(roles);

        userService.updateUser(user);
        return "redirect:/admin";
    }

}
