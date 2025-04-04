package com.example.mariadb_demo.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("")
    public String listUsers(Model model, @RequestParam(value = "page", defaultValue = "0") int page,
                            @RequestParam(value = "kw", defaultValue = "") String kw) {
        Sort sort = Sort.by(
                Sort.Order.asc("role"),
                Sort.Order.desc("createdAt")
        );
        Page<ApplicationUser> userPage = userService.getSearchResult(kw, PageRequest.of(page, 10, sort));

        Map<Long, Integer> userIdToRowNumber = new HashMap<>();
        int rowNum = 1;
        for (ApplicationUser user : userPage.getContent()) {
            if (user.getRole() == UserRole.USER) {
                userIdToRowNumber.put(user.getId(), rowNum++);
            }
        }

        model.addAttribute("userPage", userPage);
        model.addAttribute("userIdToRowNumber", userIdToRowNumber);
        model.addAttribute("kw", kw);

        return "user/list";
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUserById(id);
        return "redirect:/admin";
    }

    @GetMapping("/deactivate/{id}")
    public String deactivateUser(@PathVariable Long id) {
        userService.changeUserStatus(id, false);
        return "redirect:/admin";
    }

    @GetMapping("/activate/{id}")
    public String activateUser(@PathVariable Long id) {
        userService.changeUserStatus(id, true);
        return "redirect:/admin";
    }

}
