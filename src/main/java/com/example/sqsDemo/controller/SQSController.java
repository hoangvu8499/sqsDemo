package com.example.sqsDemo.controller;

import com.example.sqsDemo.entity.MessageSending;
import com.example.sqsDemo.service.SQSReader;
import com.example.sqsDemo.service.SQSReaderImpl;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SQSController {
    SQSReader sqsReader = new SQSReaderImpl();

    @GetMapping("/send")
    public void sendMessageToQueue(@RequestParam String topic, @RequestParam String message) {
        sqsReader.sendMessage(topic, message);
    }

    @GetMapping("/pull")
    public List<MessageSending> pullMessageToQueue() {
        return sqsReader.pullMessages(null);
    }

}
