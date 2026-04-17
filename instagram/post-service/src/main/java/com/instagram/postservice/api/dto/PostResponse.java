package com.instagram.postservice.api.dto;

public record PostResponse(
        String postId,
        String userId,
        String caption,
        String mediaUrl,
        String createdAt,
        String updatedAt) {
}
