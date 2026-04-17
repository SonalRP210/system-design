package com.instagram.feedservice.service;

import com.instagram.feedservice.api.dto.FeedPageResponse;
import com.instagram.feedservice.api.dto.FeedPostResponse;
import com.instagram.feedservice.entity.FeedPostEntity;
import com.instagram.feedservice.repository.FeedRepository;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class DefaultFeedService implements FeedService {

    private static final int DEFAULT_LIMIT = 20;
    private static final int MAX_LIMIT = 100;

    private final FeedRepository feedRepository;

    public DefaultFeedService(FeedRepository feedRepository) {
        this.feedRepository = feedRepository;
    }

    @Override
    public FeedPageResponse getFeed(String cursor, Integer limit) {
        int safeLimit = normalizeLimit(limit);
        int offset = decodeCursor(cursor);

        List<FeedPostEntity> page = feedRepository.findPage(offset, safeLimit);
        List<FeedPostResponse> posts = page.stream().map(this::toResponse).toList();

        int nextOffset = offset + posts.size();
        String nextCursor = nextOffset < feedRepository.count() ? encodeCursor(nextOffset) : null;
        return new FeedPageResponse(posts, nextCursor);
    }

    private FeedPostResponse toResponse(FeedPostEntity entity) {
        return new FeedPostResponse(
                entity.getPostId(),
                entity.getAuthorId(),
                entity.getCaption(),
                entity.getPublishedAt());
    }

    private int normalizeLimit(Integer limit) {
        if (limit == null) {
            return DEFAULT_LIMIT;
        }
        return Math.max(1, Math.min(limit, MAX_LIMIT));
    }

    private int decodeCursor(String cursor) {
        if (cursor == null || cursor.isBlank()) {
            return 0;
        }
        try {
            String decoded = new String(Base64.getUrlDecoder().decode(cursor), StandardCharsets.UTF_8);
            int offset = Integer.parseInt(decoded);
            if (offset < 0) {
                throw new IllegalArgumentException("cursor must represent a non-negative offset");
            }
            return offset;
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid cursor");
        }
    }

    private String encodeCursor(int offset) {
        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(Integer.toString(offset).getBytes(StandardCharsets.UTF_8));
    }
}
