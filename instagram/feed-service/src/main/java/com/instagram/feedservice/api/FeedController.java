package com.instagram.feedservice.api;

import com.instagram.feedservice.api.dto.FeedPageResponse;
import com.instagram.feedservice.service.FeedService;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/feed")
public class FeedController {

    private final FeedService feedService;

    public FeedController(FeedService feedService) {
        this.feedService = feedService;
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("service", "feed-service", "status", "UP");
    }

    @GetMapping
    public FeedPageResponse getFeed(
            @RequestParam(required = false) String cursor,
            @RequestParam(required = false) Integer limit) {
        return feedService.getFeed(cursor, limit);
    }
}
