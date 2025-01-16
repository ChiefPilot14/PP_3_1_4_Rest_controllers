package ru.kata.spring.boot_security.demo.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.UserServiceImpl;

import java.util.List;
import java.util.Optional;

@Controller
public class UsersController {
    private final UserServiceImpl userService;

    @Autowired
    public UsersController(UserServiceImpl userService) {
        this.userService = userService;

    }

    private void populateUsersList(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
    }

    @GetMapping("/")
    public String showAllUsers(Model model) {
        populateUsersList(model);
        return "users";
    }

    @GetMapping("/delete/{id}")
    public String removeUserById(@PathVariable("id") int id, Model model) {
        userService.removeUserById(id);
        populateUsersList(model);
        return "users";
    }

    //    @GetMapping("/edit")
//    public String editUserById(HttpServletRequest request, Model model) {
//        int id = Integer.parseInt(request.getParameter("id"));
//        Optional<User> user = userService.getUserById(id);
//        model.addAttribute("user", user);
//        return "edit";
//    }
    @GetMapping("/edit/{id}")
    public String editUserById(@PathVariable("id") int id, Model model) {
        Optional<User> user = userService.getUserById(id);
        model.addAttribute("user", user);
        return "edit";
    }

    @PostMapping("/save/{id}/{name}/{lastName}/{age}")
    public String saveUser(
            @PathVariable("id") Long id,
            @PathVariable("name") String name,
            @PathVariable("lastName") String lastName,
            @PathVariable("age") Byte age,
            Model model) {

        userService.saveUserById(id, name, lastName, age);
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "users";

    }

    @GetMapping("/add-user")
    public String addUserForm() {
        return "add-user";
    }

    @PostMapping("/create")
    public String createNewUser(@ModelAttribute User user, Model model) {
        userService.saveUser(user.getName(), user.getLastName(), user.getAge());
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "redirect:/";
    }
}




