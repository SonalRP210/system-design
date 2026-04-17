package com.instagram.postservice.service;

import com.instagram.postservice.api.dto.FeedResponse;
import com.instagram.postservice.api.dto.PostResponse;
import com.instagram.postservice.entity.PostEntity;
import com.instagram.postservice.feed.FeedCursorCodec;
import com.instagram.postservice.repository.FollowRepository;
import com.instagram.postservice.repository.PostRepository;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Service;

@Service
public class DefaultFeedService implements FeedService {

    private static final Comparator<PostEntity> NEWEST_FIRST =
            Comparator.comparing(PostEntity::getPostSortKey).reversed();

    private final PostRepository postRepository;
    private final FollowRepository followRepository;

    public DefaultFeedService(PostRepository postRepository, FollowRepository followRepository) {
        this.postRepository = postRepository;
        this.followRepository = followRepository;
    }

    @Override
    public FeedResponse getHomeFeed(String viewerUserId, String cursor, int limit) {
        int safeLimit = normalizeLimit(limit);
        String cursorSortKey = (cursor == null || cursor.isBlank()) ? null : FeedCursorCodec.decode(cursor);

        List<String> followees = followRepository.findFollowedUserIds(viewerUserId);
        int perUser = Math.min(200, Math.max(50, safeLimit * 5));

        List<PostEntity> merged = new ArrayList<>();
        for (String authorId : followees) {
            merged.addAll(postRepository.findRecentByUserId(authorId, perUser));
        }

        merged.sort(NEWEST_FIRST);

        List<PostEntity> window = new ArrayList<>();
        for (PostEntity post : merged) {
            if (cursorSortKey != null && post.getPostSortKey().compareTo(cursorSortKey) >= 0) {
                continue;
            }
            window.add(post);
            if (window.size() == safeLimit) {
                break;
            }
        }

        String nextCursor = window.size() == safeLimit
                ? FeedCursorCodec.encode(window.get(window.size() - 1).getPostSortKey())
                : null;

        List<PostResponse> responses = window.stream().map(this::mapToResponse).toList();
        return new FeedResponse(responses, nextCursor);
    }

    private static int normalizeLimit(int limit) {
        if (limit < 1) {
            throw new IllegalArgumentException("limit must be at least 1");
        }
        return Math.min(limit, 50);
    }

    private PostResponse mapToResponse(PostEntity postEntity) {
        return new PostResponse(
                postEntity.getPostId(),
                postEntity.getUserId(),
                postEntity.getCaption(),
                postEntity.getMediaUrl(),
                postEntity.getCreatedAt(),
                Objects.requireNonNullElse(postEntity.getUpdatedAt(), postEntity.getCreatedAt()));
    }
}
