package com.example.sqsDemo.config;

import com.amazon.sqs.javamessaging.SQSConnection;
import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.amazonaws.auth.BasicAWSCredentials;
import com.example.sqsDemo.utils.SQSConstant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;

import javax.jms.JMSException;

@Configuration
public class SqsConfig {
    @Value("${cloud.aws.region.static}")
    private String region;

    @Value("${cloud.aws.credentials.access-key}")
    private String awsAccessKey;

    @Value("${cloud.aws.credentials.secret-key}")
    private String awsSecretKey;

    @Value("${cloud.aws.end-point.uri}")
    private String endpoint;

    @Bean
    public SQSConnection sqsConnection() {
        try {
            return new SQSConnectionFactory.Builder(region).build().createConnection(new BasicAWSCredentials(awsAccessKey, awsSecretKey));
        } catch (JMSException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Bean
    public SqsClient sqsClient() {
        return SqsClient.builder()
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(awsAccessKey, awsSecretKey)))
                .region(Region.of(region)).build();
    }


    @Bean
    public SqsClient sqsClient2() {
        return SqsClient.builder()
                .credentialsProvider(DefaultCredentialsProvider.create())
                .region(Region.of(region))
                .build();
    }

}
