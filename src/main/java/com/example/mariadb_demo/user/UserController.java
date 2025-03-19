package com.example.mariadb_demo.user;

import com.example.mariadb_demo.mail.MailRequestDTO;
import com.example.mariadb_demo.mail.MailService;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/signup")
    public String showSignupPage(Model model) {
        model.addAttribute("userDTO", new UserDTO());
        return "user/signup";
    }

    @PostMapping("/signup")
    public String signup(@Valid UserDTO userDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "user/signup";
        }

        if (!userDTO.getPassword1().equals(userDTO.getPassword2())) {
            bindingResult.rejectValue("password2", "passwordInCorrect",
                    "비밀번호가 일치하지 않습니다.");
            return "user/signup";
        }

        try {
            userService.createUser(userDTO);
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("이메일 인증이 완료되지 않았습니다.")) {
                bindingResult.rejectValue("email", "emailNotVerified", e.getMessage());
            } else if (e.getMessage().contains("전화번호 인증이 완료되지 않았습니다.")) {
                bindingResult.rejectValue("phone", "phoneNotVerified", e.getMessage());
            } else if (e.getMessage().contains("이메일")) {
                bindingResult.rejectValue("email", "duplicateEmail", e.getMessage());
            } else if (e.getMessage().contains("전화번호")) {
                bindingResult.rejectValue("phone", "duplicatePhone", e.getMessage());
            } else {
                bindingResult.reject("signupFailed", e.getMessage());
            }
            return "user/signup";
        } catch (Exception e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", "회원가입 중 오류가 발생했습니다.");
            return "user/signup";
        }
        return "user/login";
    }

    @GetMapping("/login")
    public String login() {
        return "user/login";
    }

    @GetMapping("/login-error")
    public String loginError(Model model) {
        model.addAttribute("loginError", true);
        return "user/login";
    }

    @GetMapping("/admin")
    public String listUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "user/list";
    }

}
