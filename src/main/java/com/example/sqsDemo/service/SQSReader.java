package com.example.sqsDemo.service;

import com.example.sqsDemo.entity.MessageSending;
import reactor.core.publisher.Flux;

import java.util.List;

public interface SQSReader {
    boolean sendMessage(String topicName, String message, String email);

    boolean sendMessage2(String topicName, String message, String email);

    List<MessageSending> pullMessages();

    void deleteMessage(String messageId, String receiptHandle);

    Flux<MessageSending> pullMessagesWebFlux();
}
