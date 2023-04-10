package com.example.sqsDemo.service;

import com.example.sqsDemo.entity.MessageSending;
import software.amazon.awssdk.services.sqs.model.Message;

import java.util.List;

public interface SQSReader {
    void sendMessage(String topicName, String message);
    List<MessageSending> pullMessages(String queueUrl);
}
