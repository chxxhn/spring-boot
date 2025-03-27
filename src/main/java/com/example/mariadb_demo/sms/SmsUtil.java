package com.example.mariadb_demo.sms;

import jakarta.annotation.PostConstruct;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SmsUtil {

    @Value("${spring.coolsms.fromnumber}")
    private String fromNumber;

    @Value("${spring.coolsms.apikey}")
    private String apiKey;

    @Value("${spring.coolsms.apisecret}")
    private String apiSecretKey;

    @Value("${spring.coolsms.provider}")
    private String provider;

    private DefaultMessageService messageService;


    @PostConstruct
    private void init() {
        this.messageService = NurigoApp.INSTANCE.initialize(apiKey, apiSecretKey, provider);
    }

    public SingleMessageSentResponse sendSMS(String to, String verificationCode) {
        Message message = new Message();

        //발신번호 및 수신번호는 반드시 01012345678 형태로 입력되어야 함!!
        message.setFrom(fromNumber);
        message.setTo(to);
        message.setText("[Ｗｅｂ 발신] 인증번호를 입력해 주세요\n" + verificationCode);

        try {
            SingleMessageSentResponse response = this.messageService.sendOne(new SingleMessageSendingRequest(message));
            System.out.println(response);
            return response;
        } catch (Exception e) {
            System.out.println("Exception while sending SMS: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}
