package com.example.mariadb_demo.user;

import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

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
            if (e.getMessage().contains("이메일")) {
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
    public String login(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("loginError", "아이디 또는 비밀번호가 올바르지 않습니다.");
        }
        return "user/login";
    }

    @GetMapping("/admin")
    public String listUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "user/list";
    }

}
