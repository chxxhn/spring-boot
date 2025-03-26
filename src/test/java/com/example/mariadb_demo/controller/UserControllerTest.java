package com.example.mariadb_demo.controller;

import com.example.mariadb_demo.user.UserController;
import com.example.mariadb_demo.user.UserDTO;
import com.example.mariadb_demo.user.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(UserControllerTest.TestConfig.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public UserService userService() {
            return Mockito.mock(UserService.class);
        }
    }

    @BeforeEach
    void setup() {
    }

    @Test
    public void 회원가입_성공() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("testuser");
        userDTO.setEmail("test@example.com");
        userDTO.setPassword1("password");
        userDTO.setPassword2("password");
        userDTO.setPhone("010-1234-5678");

        when(userService.createUser(any(UserDTO.class))).thenReturn(null);

        mockMvc.perform(post("/signup")
                        .contentType("application/x-www-form-urlencoded")
                        .param("username", userDTO.getUsername())
                        .param("email", userDTO.getEmail())
                        .param("password1", userDTO.getPassword1())
                        .param("password2", userDTO.getPassword2())
                        .param("phone", userDTO.getPhone()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/login"));
    }

    @Test
    public void 비밀번호_불일치() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("testuser");
        userDTO.setEmail("test@example.com");
        userDTO.setPassword1("password");
        userDTO.setPassword2("differentPassword");
        userDTO.setPhone("010-1234-5678");

        mockMvc.perform(post("/signup")
                        .contentType("application/x-www-form-urlencoded")
                        .param("username", userDTO.getUsername())
                        .param("email", userDTO.getEmail())
                        .param("password1", userDTO.getPassword1())
                        .param("password2", userDTO.getPassword2())
                        .param("phone", userDTO.getPhone()))
                .andExpect(status().isOk())
                .andExpect(view().name("user/signup"));
    }
}
