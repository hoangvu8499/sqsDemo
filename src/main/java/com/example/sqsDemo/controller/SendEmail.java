package com.example.sqsDemo.controller;

import com.example.sqsDemo.entity.MessageSending;
import com.example.sqsDemo.service.SQSReader;
import com.example.sqsDemo.service.SQSReaderImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Component
public class SendEmail {

    @Autowired
    private JavaMailSender javaMailSender;

    SQSReader sqsReader = new SQSReaderImpl();

    @Scheduled(cron = "0 0/1 * * * ?")
    public void send() {
        List<MessageSending> messages = sqsReader.pullMessages();
        if (!CollectionUtils.isEmpty(messages)) {
            for (MessageSending messageSending : messages) {
                SimpleMailMessage mimeMessage = new SimpleMailMessage();
                mimeMessage.setText("Topic: "+messageSending.getTopic()+" | Message: "+messageSending.getMessage());
                mimeMessage.setTo(messageSending.getEmail());
                mimeMessage.setSubject("TEST SEND CONTENT SQS");
                mimeMessage.setFrom("nacu22984@gmail.com");
                javaMailSender.send(mimeMessage);
                sqsReader.deleteMessage(messageSending.getMessageId());
            }
        }
    }
}
