package com.example.sqsDemo.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MessageSending {
    private String topic;
    private String message;
    private String email;
    private String messageId;
    private String receiptHandle;
}
