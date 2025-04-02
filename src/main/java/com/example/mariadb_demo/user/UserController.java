package com.example.mariadb_demo.user;

import com.example.mariadb_demo.exception.CustomExceptions;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class UserController {

    @GetMapping("/login")
    public String login(HttpServletRequest request, Model model) {
        Object errorMessage = request.getSession().getAttribute("loginErrorMessage");
        if (errorMessage != null) {
            model.addAttribute("loginErrorMessage", errorMessage);
            request.getSession().removeAttribute("loginErrorMessage");
        }
        return "user/login";
    }

}
