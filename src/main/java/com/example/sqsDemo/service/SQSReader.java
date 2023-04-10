package com.example.sqsDemo.service;

import com.example.sqsDemo.entity.MessageSending;
import reactor.core.publisher.Flux;

import java.util.List;

public interface SQSReader {
    void sendMessage(String topicName, String message, String email);

    List<MessageSending> pullMessages();

    void deleteMessage(String messageId, String receiptHandle);

    Flux<MessageSending> pullMessagesWebFlux();
}
