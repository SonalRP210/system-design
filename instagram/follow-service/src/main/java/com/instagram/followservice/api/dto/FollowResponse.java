package com.instagram.followservice.api.dto;

public record FollowResponse(
        String followerId,
        String followedId,
        String createdAt) {
}
