package com.example.sqsDemo.controller;

import com.example.sqsDemo.entity.MessageSending;
import com.example.sqsDemo.service.SQSReader;
import com.example.sqsDemo.service.SQSReaderImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
public class SQSController {

    @Autowired
    SQSReader sqsReader;

    @GetMapping("/send")
    public boolean sendMessageToQueue(@RequestParam String topic, @RequestParam String message, @RequestParam String email) {
        return sqsReader.sendMessage(topic, message, email);
    }

    @GetMapping("/sendarn")
    public boolean sendMessageToQueue2(@RequestParam String topic, @RequestParam String message, @RequestParam String email) {
        return sqsReader.sendMessage2(topic, message, email);
    }

    @GetMapping("/pull")
    public List<MessageSending> pullMessageToQueue() {
        return sqsReader.pullMessages();
    }

    @GetMapping("/pullMessageWebflux")
    public Flux<MessageSending> pullMessageWebflux() {
        return sqsReader.pullMessagesWebFlux();
    }

}
