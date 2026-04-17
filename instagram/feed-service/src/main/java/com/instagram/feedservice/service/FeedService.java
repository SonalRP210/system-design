package com.instagram.feedservice.service;

import com.instagram.feedservice.api.dto.FeedPageResponse;

public interface FeedService {

    FeedPageResponse getFeed(String cursor, Integer limit);
}
