package ru.kata.spring.boot_security.demo.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.dto.UpdateUserRequest;
import ru.kata.spring.boot_security.demo.model.entity.Role;
import ru.kata.spring.boot_security.demo.model.entity.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.*;

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
    public String getAdminPanel(Model model,
                                @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails) {
        model.addAttribute("users", userService.getAllUsers());

        Optional<User> currentUser = userService.getByUsername(userDetails.getUsername());
        if (currentUser.isPresent()) {
            User actualCurrentUser = currentUser.get();
            model.addAttribute("currentUser", actualCurrentUser);
        } else {
            throw new RuntimeException("User не найден в базе данных.");

        }

        model.addAttribute("newUser", new User());

        List<Role> allRoles = roleService.getAllRoles();
        model.addAttribute("allRoles", allRoles);

        return "admin";
    }

    @GetMapping("/{id}/edit")
    @ResponseBody
    public Map<String, Object> getUserForEditing(@PathVariable("id") long id) {
        User user = userService.getUserOrCreateIfNotExists(id);
        List<Role> allRoles = roleService.getAllRoles();

        Map<String, Object> result = new HashMap<>();
        result.put("user", user);
        result.put("roles", allRoles);

        return result;
    }

    @GetMapping("/{id}/delete")
    @ResponseBody
    public ResponseEntity<?> deleteUser(@PathVariable("id") long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/update")
    @ResponseBody
    public ResponseEntity<?> updateUser(@PathVariable("id") Long id, @RequestBody UpdateUserRequest request) {

        userService.updateUser(id, request.getUser(), request.getRoleIds());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/registration")
    public String registerUser(@Valid @ModelAttribute("user") User user,
                               BindingResult bindingResult,
                               Model model, @RequestParam(value = "selectedRole", required = false) Long[] rolesIds) {

        if (userService.findByUsername(user.getUsername())) {
            model.addAttribute("errorMessage", "Пользователь с таким именем пользователя " +
                    "уже зарегистрирован.");
            return "redirect:/admin";
        }

        userService.setRolesForUser(user, Arrays.asList(rolesIds));

        userService.createUser(user);
        return "redirect:/admin";
    }

}
