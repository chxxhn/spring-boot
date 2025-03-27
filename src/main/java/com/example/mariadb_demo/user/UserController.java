package com.example.mariadb_demo.user;

import com.example.mariadb_demo.exception.CustomExceptions;
import com.example.mariadb_demo.mail.MailService;
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

    private final UserService userService;
    private final MailService mailService;

    public UserController(UserService userService, MailService mailService) {
        this.userService = userService;
        this.mailService = mailService;
    }

    @GetMapping("/signup")
    public String showSignupPage(Model model) {
        model.addAttribute("userDTO", new UserDTO());
        return "user/signup";
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@Valid UserDTO userDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder sb = new StringBuilder();
            bindingResult.getFieldErrors().forEach(error -> {
                sb.append(error.getField())
                        .append(": ")
                        .append(error.getDefaultMessage())
                        .append("\\n");
            });
            return ResponseEntity.badRequest().body(sb.toString());
        }

        if (!userDTO.getPassword1().equals(userDTO.getPassword2())) {
            return ResponseEntity.badRequest().body("비밀번호가 일치하지 않습니다.");
        }

        if (!userDTO.getEmail().equals(userDTO.getHiddenEmail())
                || !mailService.isEmailVerified(userDTO.getEmail())) {
            throw new CustomExceptions.EmailNotVerifiedException("이메일 인증이 완료되지 않았습니다.");
        }

        try {
            userService.createUser(userDTO);
        } catch (CustomExceptions.EmailNotVerifiedException |
                 CustomExceptions.PhoneNotVerifiedException |
                 CustomExceptions.UserAlreadyExistsException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("회원가입 중 오류가 발생했습니다.");
        }

        return ResponseEntity.ok("회원가입 성공");
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
