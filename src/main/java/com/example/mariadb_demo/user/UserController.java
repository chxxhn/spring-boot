package com.example.mariadb_demo.user;

import com.example.mariadb_demo.exception.CustomExceptions;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
            bindingResult.rejectValue("password2", "passwordInCorrect", "비밀번호가 일치하지 않습니다.");
            return "user/signup";
        }

        try {
            userService.createUser(userDTO);
        } catch (CustomExceptions.EmailNotVerifiedException e) {
            bindingResult.rejectValue("email", "emailNotVerified", e.getMessage());
            return "user/signup";
        } catch (CustomExceptions.PhoneNotVerifiedException e) {
            bindingResult.rejectValue("phone", "phoneNotVerified", e.getMessage());
            return "user/signup";
        } catch (CustomExceptions.UserAlreadyExistsException e) {
            bindingResult.rejectValue("email", "duplicateEmail", e.getMessage());
            return "user/signup";
        } catch (Exception e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", "회원가입 중 오류가 발생했습니다.");
            return "user/signup";
        }
        return "redirect:/login";
    }

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
