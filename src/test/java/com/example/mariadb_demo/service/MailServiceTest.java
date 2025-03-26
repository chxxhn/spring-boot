package com.example.mariadb_demo.service;

import com.example.mariadb_demo.mail.MailService;
import com.example.mariadb_demo.mail.RedisConfig;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MailServiceTest {

    private MailService mailService;
    private JavaMailSender javaMailSender;
    private RedisConfig redisConfig;
    private RedisTemplate<String, String> redisTemplate;
    private ValueOperations<String, String> valueOperations;

    @BeforeEach
    public void setUp() {
        javaMailSender = mock(JavaMailSender.class);
        redisConfig = mock(RedisConfig.class);
        redisTemplate = mock(RedisTemplate.class);
        valueOperations = mock(ValueOperations.class);

        when(redisConfig.redisTemplate()).thenReturn(redisTemplate);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        mailService = new MailService(javaMailSender, redisConfig);
        ReflectionTestUtils.setField(mailService, "serviceName", "test@example.com");
    }

    @Test
    public void 메일작성() throws Exception {
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

        String email = "customer@example.com";
        String code = mailService.joinEmail(email);

        assertEquals(6, code.length());

        verify(javaMailSender, times(1)).send(mimeMessage);

        verify(valueOperations, times(1)).set(eq(email), eq(code), eq(180L), eq(java.util.concurrent.TimeUnit.SECONDS));
    }

    @Test
    public void 메일인증_성공() {
        String email = "test@example.com";
        String authCode = "123456";

        when(valueOperations.get(email)).thenReturn("123456");

        Boolean result = mailService.checkAuthNum(email, authCode);

        assertTrue(result);
        verify(valueOperations).set("email:verified:" + email, "true", 10, TimeUnit.MINUTES);
    }

    @Test
    public void 메일인증_실패() {
        String email = "test@example.com";
        String authCode = "wrong-code";

        when(valueOperations.get(email)).thenReturn("123456");

        Boolean result = mailService.checkAuthNum(email, authCode);

        assertFalse(result);
        verify(valueOperations, never()).set(eq("email:verified:" + email), eq("true"), anyLong(), any());
    }

    @Test
    public void testIsEmailVerified_True() {
        String email = "test@example.com";

        when(valueOperations.get("email:verified:" + email)).thenReturn("true");

        boolean result = mailService.isEmailVerified(email);

        assertTrue(result);
    }

    @Test
    public void testIsEmailVerified_False() {
        String email = "test@example.com";

        when(valueOperations.get("email:verified:" + email)).thenReturn(null);

        boolean result = mailService.isEmailVerified(email);

        assertFalse(result);
    }
}

