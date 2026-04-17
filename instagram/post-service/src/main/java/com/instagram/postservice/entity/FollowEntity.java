package com.instagram.postservice.entity;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@DynamoDbBean
public class FollowEntity {

    private String followerId;
    private String followedId;
    private String createdAt;

    @DynamoDbPartitionKey
    @DynamoDbAttribute("followerId")
    public String getFollowerId() {
        return followerId;
    }

    public void setFollowerId(String followerId) {
        this.followerId = followerId;
    }

    @DynamoDbSortKey
    @DynamoDbAttribute("followedId")
    public String getFollowedId() {
        return followedId;
    }

    public void setFollowedId(String followedId) {
        this.followedId = followedId;
    }

    @DynamoDbAttribute("createdAt")
    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
