package com.instagram.postservice.service;

import com.instagram.postservice.api.dto.FeedResponse;

public interface FeedService {

    FeedResponse getHomeFeed(String viewerUserId, String cursor, int limit);
}
