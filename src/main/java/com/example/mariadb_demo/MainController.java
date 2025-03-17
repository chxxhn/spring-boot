package com.example.mariadb_demo;

import com.example.mariadb_demo.user.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.SessionAttribute;

@Controller
public class MainController {

    @ModelAttribute("user")
    public CustomUserDetails getUser(@AuthenticationPrincipal CustomUserDetails user) {
        return user;
    }

    @GetMapping("/")
    public String root(@SessionAttribute(name = "userId", required = false) Long userId, Model model) {
        model.addAttribute("view", "index");
        return "layout";
    }

}
