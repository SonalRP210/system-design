package com.instagram.feedservice.api.dto;

public record FeedPostResponse(
        String postId,
        String authorId,
        String caption,
        String publishedAt) {
}
