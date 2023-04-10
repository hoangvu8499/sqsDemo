package com.example.sqsDemo.service;

import com.example.sqsDemo.entity.MessageSending;
import com.example.sqsDemo.utils.JsonConverter;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class SQSReaderImpl implements SQSReader {

    Logger logger= LoggerFactory.getLogger(SQSReaderImpl.class);

    @Value("${cloud.aws.end-point.uri}")
    private String endpoint;

    @Value("${cloud.aws.region.static}")
    private String region;

    @Value("${cloud.aws.credentials.access-key}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secret-key}")
    private String secretKey;

    private static final String QUEUE_NAME = "demo1.fifo";

    private SqsClient sqsClient = SqsClient.builder()
            .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create("AKIAQRWEO4CLMYFZOFOJ", "RZlg/+qsDKtLGXgYtrBhCn/wep38UVyrHVzraYcy")))
            .region(Region.of("ap-southeast-1")).build();

    @Override
    public void sendMessage(String topicName, String message) {
        MessageSending messageSending = new MessageSending(topicName, message);
        String deduplicationId = "MESSAGE" + ":" + UUID.randomUUID().toString();
        SendMessageRequest sendMessageRequest = SendMessageRequest.builder()
                .queueUrl("https://sqs.ap-southeast-1.amazonaws.com/037992980630/demo1.fifo")
                .messageBody(JsonConverter.convertToJson(messageSending))
                .messageDeduplicationId(deduplicationId) // deduplication Id
                .messageGroupId("MESSAGE" + ":" + UUID.randomUUID().toString())
                .build();

        logger.info("Sending message #{} of {} to queue {}", 100, QUEUE_NAME);
        this.sqsClient.sendMessage(sendMessageRequest);
    }

    @Override
    public List<MessageSending> pullMessages(String queueUrl) {
        List<MessageSending> messages = new ArrayList<>();
        ReceiveMessageRequest receiveMessageRequest = ReceiveMessageRequest.builder()
                .queueUrl("https://sqs.ap-southeast-1.amazonaws.com/037992980630/demo1.fifo")
                .maxNumberOfMessages(3)
                .waitTimeSeconds(20)
                .build();
        List<Message> sqsMessages = this.sqsClient.receiveMessage(receiveMessageRequest).messages();
        for (Message sqsMessage : sqsMessages) {
            messages.add(JsonConverter.convertToObject(sqsMessage.body(), MessageSending.class));
        }
        return messages;
    }

}
