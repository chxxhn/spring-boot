package com.example.mariadb_demo.controller;

import com.example.mariadb_demo.mail.MailCheckDTO;
import com.example.mariadb_demo.mail.MailController;
import com.example.mariadb_demo.mail.MailRequestDTO;
import com.example.mariadb_demo.mail.MailService;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MailController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(MailControllerTest.TestConfig.class)
public class MailControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MailService mailService;

    @Autowired
    private ObjectMapper objectMapper;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public MailService mailService() {
            return Mockito.mock(MailService.class);
        }
    }

    @BeforeEach
    public void setup() {

    }

    @Test
    public void 메일보내기() throws Exception {
        MailRequestDTO mailRequestDTO = new MailRequestDTO();
        mailRequestDTO.setEmail("test@example.com");

        when(mailService.joinEmail(anyString())).thenReturn("123456");

        mockMvc.perform(post("/signup/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mailRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("123456"));
    }

    @Test
    public void 메일인증_성공() throws Exception {
        MailCheckDTO mailCheckDTO = new MailCheckDTO();
        mailCheckDTO.setEmail("test@example.com");
        mailCheckDTO.setAuthNum("123456");

        when(mailService.checkAuthNum(anyString(), anyString())).thenReturn(true);

        mockMvc.perform(post("/signup/emailAuth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mailCheckDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string("이메일 인증 성공!"));
    }
}
