package com.example.mariadb_demo.user;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/admin")
    public String listUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "user/list";
    }

    @GetMapping("/admin/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUserById(id);
        return "redirect:/admin";
    }

    @GetMapping("/admin/deactivate/{id}")
    public String deactivateUser(@PathVariable Long id) {
        userService.changeUserStatus(id, false);
        return "redirect:/admin";
    }

    @GetMapping("/admin/activate/{id}")
    public String activateUser(@PathVariable Long id) {
        userService.changeUserStatus(id, true);
        return "redirect:/admin";
    }

}
