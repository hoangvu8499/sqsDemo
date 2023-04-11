package com.example.sqsDemo.service;

import com.example.sqsDemo.config.SqsConfig;
import com.example.sqsDemo.entity.MessageSending;
import com.example.sqsDemo.utils.JsonConverter;
import com.example.sqsDemo.utils.SQSConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class SQSReaderImpl implements SQSReader {


    private static final String QUEUE_URL = "arn:aws:sqs:us-west-2:123456789012:my-queue";



    Logger logger = LoggerFactory.getLogger(SQSReaderImpl.class);

    @Value("${cloud.aws.end-point.uri}")
    private String endpoint;
    //Using ARN
    //Tích hợp pull vs mes vs delete -- sẽ có issue khi pull về rồi xoá, thì nếu co vấn đề khi xử lý message sẽ mất luôn message

    @Autowired
    private  SqsClient sqsClient;

    @Autowired
    @Qualifier("sqsClient2")
    private  SqsClient sqsClient2;

    @Override
    public boolean sendMessage(String topicName, String message, String email) {
        MessageSending messageSending = new MessageSending(topicName, message, email, null, null);
        try {
            SendMessageRequest sendMessageRequest = SendMessageRequest.builder()
                    .queueUrl(endpoint)
                    .messageBody(JsonConverter.convertToJson(messageSending))
                    .messageDeduplicationId(UUID.randomUUID().toString())
                    .messageGroupId(topicName)
                    .build();

            logger.info("Sending message: "+ SQSConstant.QUEUE_NAME);
            return sqsClient2.sendMessage(sendMessageRequest).sdkHttpResponse().isSuccessful();
        } catch (Exception e) {
            logger.error("Problem when sending message");
            return false;
        }
    }

    @Override
    public boolean sendMessage2(String topicName, String message, String email) {
        MessageSending messageSending = new MessageSending(topicName, message, email, null, null);
        try {
            SendMessageRequest sendMessageRequest = SendMessageRequest.builder()
                    .queueUrl("arn:aws:sqs:ap-southeast-1:037992980630:demo1.fifo")
                    .messageBody(JsonConverter.convertToJson(messageSending))
                    .messageDeduplicationId(UUID.randomUUID().toString())
                    .messageGroupId(topicName)
                    .build();

            logger.info("Sending message: "+ SQSConstant.QUEUE_NAME);
            return sqsClient.sendMessage(sendMessageRequest).sdkHttpResponse().isSuccessful();
        } catch (Exception e) {
            logger.error("Problem when sending message");
            return false;
        }
    }

    @Override
    public List<MessageSending> pullMessages() {
        List<MessageSending> messages = new ArrayList<>();
        try {
            ReceiveMessageRequest receiveMessageRequest = ReceiveMessageRequest.builder()
                    .queueUrl(endpoint)
                    .maxNumberOfMessages(3)
                    .waitTimeSeconds(20)
                    .build();
            List<Message> sqsMessages = sqsClient.receiveMessage(receiveMessageRequest).messages();
            for (Message sqsMessage : sqsMessages) {
                MessageSending messageSending = JsonConverter.convertToObject(sqsMessage.body(), MessageSending.class);
                messageSending.setMessageId(sqsMessage.messageId());
                messageSending.setReceiptHandle(sqsMessage.receiptHandle());
                messages.add(messageSending);
            }
        } catch (Exception e) {
            logger.error("Problem when pulling message");
        }
        return messages;
    }

    public Flux<MessageSending> pullMessagesWebFlux() {
        return Flux.defer(() -> {
            ReceiveMessageRequest receiveMessageRequest = ReceiveMessageRequest.builder()
                    .queueUrl(endpoint)
                    .maxNumberOfMessages(3)
                    .waitTimeSeconds(20)
                    .build();
            List<Message> sqsMessages = sqsClient.receiveMessage(receiveMessageRequest).messages();
            return Flux.fromIterable(sqsMessages);
        }).map(sqsMessage -> {
            MessageSending messageSending = JsonConverter.convertToObject(sqsMessage.body(), MessageSending.class);
            messageSending.setMessageId(sqsMessage.messageId());
            messageSending.setReceiptHandle(sqsMessage.receiptHandle());
            return messageSending;
        }).onErrorResume(e -> {
            logger.error("Problem when pulling message", e);
            return Flux.empty();
        });
    }

    @Override
    public void deleteMessage(String messageId, String receiptHandle) {
        try {
            DeleteMessageRequest deleteRequest = DeleteMessageRequest.builder()
                    .queueUrl(endpoint)
                    .receiptHandle(receiptHandle)
                    .build();
            sqsClient.deleteMessage(deleteRequest);
            logger.info("Deleted message with ID " + messageId);
        } catch (Exception e) {
            logger.error("Problem when delete message with ID: " + messageId);
        }
    }

}
