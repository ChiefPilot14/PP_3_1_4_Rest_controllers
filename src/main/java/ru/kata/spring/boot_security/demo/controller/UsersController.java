package ru.kata.spring.boot_security.demo.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.List;
import java.util.Optional;


@Controller
@RequestMapping("/users")
public class UsersController {

    private final UserService userService;

    @Autowired
    public UsersController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String showAllUsers(@AuthenticationPrincipal User currentUser, Model model) {
        if (!currentUser.hasAuthority("ROLE_ADMIN")) {
            return "redirect:/403";
        }

        model.addAttribute("users", userService.getAllUsers());
        return "users";
    }

    @GetMapping("/{id}/delete")
    public String deleteUser(@PathVariable("id") long id) {
        userService.removeUserById(id);
        return "redirect:/users";
    }

    @GetMapping("/{id}/edit")
    public String editUser(@PathVariable("id") long id, @AuthenticationPrincipal User currentUser, Model model) {
        Optional<User> optionalUser = userService.getUserById(id);
        if (optionalUser.isEmpty()) {
            if (!currentUser.hasAuthority("ROLE_ADMIN")) {
                return "redirect:/403";
            }

            User user = new User();
            user.setId(id);
            model.addAttribute("user", user);
            return "edit";
        }

        User userToEdit = optionalUser.get();

        if (!currentUser.hasAuthority("ROLE_ADMIN")) {
            return "redirect:/403";
        }

        model.addAttribute("user", userToEdit);
        return "edit";
    }


    @PostMapping("/{id}/update")
    public String updateUser(@ModelAttribute("user") User user, @AuthenticationPrincipal User currentUser) {
        if (!currentUser.equals(user) && !currentUser.hasAuthority("ROLE_ADMIN")) {
            return "redirect:/403";
        }

        userService.updateUser(user);
        return "redirect:/users";
    }

    @GetMapping("/new")
    public String newUserForm(Model model) {
        model.addAttribute("user", new User());
        return "add-user";
    }

    @PostMapping("/create")
    public String createUser(@ModelAttribute("user") User user) {
        userService.createUser(user);
        return "redirect:/users";
    }
}

