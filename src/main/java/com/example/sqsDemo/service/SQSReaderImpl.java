package com.example.sqsDemo.service;

import com.example.sqsDemo.entity.MessageSending;
import com.example.sqsDemo.utils.JsonConverter;
import com.example.sqsDemo.utils.SQSConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class SQSReaderImpl implements SQSReader {
    Logger logger = LoggerFactory.getLogger(SQSReaderImpl.class);

    private SqsClient sqsClient = SqsClient.builder()
            .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(SQSConstant.ACCESS_KEY, SQSConstant.SECRET_KEY)))
            .region(Region.of(SQSConstant.REGION)).build();

    @Override
    public void sendMessage(String topicName, String message, String email) {
        MessageSending messageSending = new MessageSending(topicName, message, email, null, null);
        try {

            SendMessageRequest sendMessageRequest = SendMessageRequest.builder()
                    .queueUrl(SQSConstant.ENDPOINT)
                    .messageBody(JsonConverter.convertToJson(messageSending))
                    .messageDeduplicationId(UUID.randomUUID().toString())
                    .messageGroupId(UUID.randomUUID().toString())
                    .build();

            logger.info("Sending message: ", 100, SQSConstant.QUEUE_NAME);
            sqsClient.sendMessage(sendMessageRequest);
        } catch (Exception e) {
            logger.error("Problem when sending message");
        }
    }

    @Override
    public List<MessageSending> pullMessages() {
        List<MessageSending> messages = new ArrayList<>();
        try {
            ReceiveMessageRequest receiveMessageRequest = ReceiveMessageRequest.builder()
                    .queueUrl(SQSConstant.ENDPOINT)
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
                    .queueUrl(SQSConstant.ENDPOINT)
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
                    .queueUrl(SQSConstant.ENDPOINT)
                    .receiptHandle(receiptHandle)
                    .build();
            sqsClient.deleteMessage(deleteRequest);
            logger.info("Deleted message with ID " + messageId);

        } catch (Exception e) {
            logger.error("Problem when delete message with ID: " + messageId);
        }
    }

}
