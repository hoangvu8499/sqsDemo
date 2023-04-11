package com.example.sqsDemo.controller;

import com.example.sqsDemo.entity.MessageSending;
import com.example.sqsDemo.service.SQSReader;
import com.example.sqsDemo.service.SQSReaderImpl;
import com.example.sqsDemo.utils.SQSConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class SendEmail {
    Logger logger = LoggerFactory.getLogger(SendEmail.class);

    @Autowired
    private JavaMailSender javaMailSender;

    SQSReader sqsReader = new SQSReaderImpl();

//    @Scheduled(cron = "0 0/1 * * * ?")
//    public void send() {
//        List<MessageSending> messages = sqsReader.pullMessages();
//        if (!CollectionUtils.isEmpty(messages)) {
//            for (MessageSending messageSending : messages) {
//                SimpleMailMessage mimeMessage = new SimpleMailMessage();
//                mimeMessage.setText("Topic: "+messageSending.getTopic()+" | Message: "+messageSending.getMessage());
//                mimeMessage.setTo(messageSending.getEmail());
//                mimeMessage.setSubject("TEST SEND CONTENT SQS");
//                mimeMessage.setFrom("nacu22984@gmail.com");
//                javaMailSender.send(mimeMessage);
//                logger.info("Sent message: "+ messageSending.getMessageId());
//                sqsReader.deleteMessage(messageSending.getMessageId(), messageSending.getReceiptHandle());
//            }
//        }else {
//            logger.info("Don't have message to send email!");
//        }
//    }

    public void send() {

    }
}
