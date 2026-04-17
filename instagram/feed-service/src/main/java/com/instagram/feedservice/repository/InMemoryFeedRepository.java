package com.instagram.feedservice.repository;

import com.instagram.feedservice.entity.FeedPostEntity;
import jakarta.annotation.PostConstruct;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryFeedRepository implements FeedRepository {

    private final List<FeedPostEntity> posts = new ArrayList<>();

    @PostConstruct
    void seed() {
        for (int i = 1; i <= 100; i++) {
            FeedPostEntity post = new FeedPostEntity();
            post.setPostId("post-" + i);
            post.setAuthorId("author-" + ((i % 7) + 1));
            post.setCaption("Generated feed post #" + i);
            post.setPublishedAt(Instant.now().minusSeconds(i * 45L).toString());
            posts.add(post);
        }
        posts.sort(Comparator.comparing(FeedPostEntity::getPublishedAt).reversed());
    }

    @Override
    public List<FeedPostEntity> findPage(int offset, int limit) {
        if (offset >= posts.size()) {
            return List.of();
        }
        int end = Math.min(offset + limit, posts.size());
        return posts.subList(offset, end);
    }

    @Override
    public int count() {
        return posts.size();
    }
}
