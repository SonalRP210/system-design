package com.instagram.postservice.api;

import com.instagram.postservice.api.dto.FeedResponse;
import com.instagram.postservice.security.UserIdentityResolver;
import com.instagram.postservice.service.FeedService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/feed")
public class FeedController {

    private final FeedService feedService;
    private final UserIdentityResolver userIdentityResolver;

    public FeedController(FeedService feedService, UserIdentityResolver userIdentityResolver) {
        this.feedService = feedService;
        this.userIdentityResolver = userIdentityResolver;
    }

    @GetMapping
    public FeedResponse getHomeFeed(
            @RequestHeader(value = "X-User-Id", required = false) String xUserId,
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestParam(required = false) String cursor,
            @RequestParam(required = false, defaultValue = "20") int limit) {
        String viewerUserId = userIdentityResolver.resolveUserId(xUserId, authorization);
        return feedService.getHomeFeed(viewerUserId, cursor, limit);
    }
}
