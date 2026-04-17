package com.instagram.feedservice.api.dto;

import java.util.List;

public record FeedPageResponse(
        List<FeedPostResponse> posts,
        String nextCursor) {
}
