package com.example.sqsDemo.dto;

public class MessageSendingDto {
    private String topic;
    private String message;

    public MessageSendingDto(String topic, String message) {
        this.topic = topic;
        this.message = message;
    }
}
