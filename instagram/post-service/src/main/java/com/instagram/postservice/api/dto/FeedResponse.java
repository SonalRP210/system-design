package com.instagram.postservice.api.dto;

import java.util.List;

public record FeedResponse(List<PostResponse> posts, String nextCursor) {
}
