package com.instagram.postservice.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition;
import software.amazon.awssdk.services.dynamodb.model.BillingMode;
import software.amazon.awssdk.services.dynamodb.model.CreateTableRequest;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableRequest;
import software.amazon.awssdk.services.dynamodb.model.KeySchemaElement;
import software.amazon.awssdk.services.dynamodb.model.KeyType;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;
import software.amazon.awssdk.services.dynamodb.model.ScalarAttributeType;

@Component
public class DynamoDbTableInitializer implements ApplicationRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(DynamoDbTableInitializer.class);

    private final DynamoDbClient dynamoDbClient;
    private final DynamoDbProperties dynamoDbProperties;

    public DynamoDbTableInitializer(DynamoDbClient dynamoDbClient, DynamoDbProperties dynamoDbProperties) {
        this.dynamoDbClient = dynamoDbClient;
        this.dynamoDbProperties = dynamoDbProperties;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!dynamoDbProperties.isAutoCreateTable()) {
            LOGGER.info("DynamoDB auto table creation disabled for {}", dynamoDbProperties.getTableName());
            return;
        }

        String tableName = dynamoDbProperties.getTableName();
        if (tableExists(tableName)) {
            LOGGER.info("DynamoDB table {} already exists", tableName);
            return;
        }

        LOGGER.info("Creating DynamoDB table {}", tableName);
        dynamoDbClient.createTable(CreateTableRequest.builder()
                .tableName(tableName)
                .attributeDefinitions(AttributeDefinition.builder()
                        .attributeName("postId")
                        .attributeType(ScalarAttributeType.S)
                        .build())
                .keySchema(KeySchemaElement.builder()
                        .attributeName("postId")
                        .keyType(KeyType.HASH)
                        .build())
                .billingMode(BillingMode.PAY_PER_REQUEST)
                .build());
    }

    private boolean tableExists(String tableName) {
        try {
            dynamoDbClient.describeTable(DescribeTableRequest.builder().tableName(tableName).build());
            return true;
        } catch (ResourceNotFoundException exception) {
            return false;
        }
    }
}
