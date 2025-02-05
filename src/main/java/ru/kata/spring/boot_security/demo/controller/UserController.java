package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.entity.Role;
import ru.kata.spring.boot_security.demo.model.entity.User;
import ru.kata.spring.boot_security.demo.service.UserService;
import ru.kata.spring.boot_security.demo.service.RoleService;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    public UserController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

//    @GetMapping
//    public String showUserForm(Model model, Principal principal) {
//        String username = principal.getName();
//        Optional<User> user = userService.getByUsername(username);
//        if (user.isPresent()) {
//            model.addAttribute("user", user.get());
//            Set<Role> allRoles = userService.getRoles(user.get());
//            model.addAttribute("allRoles", allRoles);
//        }
//        return "user";
//    }

    @GetMapping
    public String getUserForm(Model model,
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

        return "user";
    }


}