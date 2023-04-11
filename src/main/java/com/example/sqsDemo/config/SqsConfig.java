package com.example.sqsDemo.config;

import com.example.sqsDemo.utils.SQSConstant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;

@Configuration
public class SqsConfig {
    @Value("${cloud.aws.region.static}")
    private String region;

    @Value("${cloud.aws.credentials.access-key}")
    private String awsAccessKey;

    @Value("${cloud.aws.credentials.secret-key}")
    private String awsSecretKey;

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
