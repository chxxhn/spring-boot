package com.example.mariadb_demo;

import com.example.mariadb_demo.user.CustomUserDetails;
import com.example.mariadb_demo.user.SessionUser;
import com.example.mariadb_demo.user.User;
import com.example.mariadb_demo.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.SessionAttribute;

@Controller
public class MainController {

    @ModelAttribute("user")
    public SessionUser getSessionUser(@AuthenticationPrincipal CustomUserDetails user) {
        return (user != null) ? new SessionUser(user.getId(), user.getName(), user.getEmail()) : null;
    }

    @GetMapping("/")
    public String root(@SessionAttribute(name = "userId", required = false) Long userId, Model model) {
        model.addAttribute("view", "index");
        return "layout";
    }

}
