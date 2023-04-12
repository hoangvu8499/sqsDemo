package com.example.sqsDemo.controller;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.example.sqsDemo.entity.MessageSending;
import com.example.sqsDemo.service.SQSReader;
import com.example.sqsDemo.service.SQSReaderImpl;
import com.example.sqsDemo.utils.SQSConstant;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class SendEmail  {
    Logger logger = LoggerFactory.getLogger(SendEmail.class);

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    SQSReader sqsReader;

    @SqsListener(value = "${sqs.queue-name}")
    public void processMessage(String message) {
        // Xử lý message khi nó được pull về từ SQS
    }

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
//
//    @Override
//    public void run(ApplicationArguments args) throws Exception {
//          implements ApplicationRunner
//    }


    @Autowired
    SqsClient sqsClient;


    private final ExecutorService executorService;

    public SendEmail() {
        this.executorService = Executors.newSingleThreadExecutor();
    }

    @PostConstruct
    public void startPolling() {
        executorService.execute(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                List<MessageSending> messages = sqsReader.pullMessages();
                for (MessageSending message : messages) {
                    logger.info("=====MESSAGE PULLED: "+message.getMessage()+"=======");
                    sqsReader.deleteMessage(message.getMessageId(), message.getReceiptHandle());
                }
            }
        });
    }

    @PreDestroy
    public void stopPolling() {
        executorService.shutdownNow();
    }
}
