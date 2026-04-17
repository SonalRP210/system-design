package com.instagram.feedservice;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/feed")
public class FeedController {

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("service", "feed-service", "status", "UP");
    }

    @GetMapping("/{userId}")
    public List<FeedItem> getFeed(@PathVariable String userId, @RequestParam(defaultValue = "20") int limit) {
        int maxItems = Math.max(1, Math.min(limit, 50));
        return java.util.stream.IntStream.range(0, maxItems)
                .mapToObj(index -> new FeedItem(
                        "post-" + index,
                        "author-" + index,
                        "Generated feed item " + index + " for user " + userId,
                        Instant.now().minusSeconds(index * 60L).toString()))
                .toList();
    }

    public record FeedItem(String postId, String authorId, String summary, String publishedAt) {
    }
}
