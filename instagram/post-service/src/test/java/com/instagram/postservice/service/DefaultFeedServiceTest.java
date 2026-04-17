package com.instagram.postservice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.instagram.postservice.api.dto.FeedResponse;
import com.instagram.postservice.entity.PostEntity;
import com.instagram.postservice.entity.PostSortKey;
import com.instagram.postservice.feed.FeedCursorCodec;
import com.instagram.postservice.repository.FollowRepository;
import com.instagram.postservice.repository.PostRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DefaultFeedServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private FollowRepository followRepository;

    private DefaultFeedService feedService;

    @BeforeEach
    void setUp() {
        feedService = new DefaultFeedService(postRepository, followRepository);
    }

    @Test
    void getHomeFeedShouldMergeFollowedAuthorsNewestFirst() {
        when(followRepository.findFollowedUserIds("viewer")).thenReturn(List.of("a", "b"));

        PostEntity older = post("a", "2026-01-01T00:00:00Z", "p-old");
        PostEntity newer = post("b", "2026-02-01T00:00:00Z", "p-new");

        when(postRepository.findRecentByUserId("a", 50)).thenReturn(List.of(older));
        when(postRepository.findRecentByUserId("b", 50)).thenReturn(List.of(newer));

        FeedResponse feed = feedService.getHomeFeed("viewer", null, 1);

        assertThat(feed.posts()).extracting(p -> p.postId()).containsExactly("p-new");
        assertThat(feed.nextCursor()).isNotNull();
    }

    @Test
    void getHomeFeedShouldRespectCursor() {
        when(followRepository.findFollowedUserIds("viewer")).thenReturn(List.of("a"));

        PostEntity newest = post("a", "2026-02-02T00:00:00Z", "p-2");
        PostEntity middle = post("a", "2026-02-01T00:00:00Z", "p-1");
        PostEntity oldest = post("a", "2026-01-01T00:00:00Z", "p-0");

        when(postRepository.findRecentByUserId("a", 50)).thenReturn(List.of(newest, middle, oldest));

        String cursor = FeedCursorCodec.encode(middle.getPostSortKey());
        FeedResponse feed = feedService.getHomeFeed("viewer", cursor, 10);

        assertThat(feed.posts()).extracting(p -> p.postId()).containsExactly("p-0");
        assertThat(feed.nextCursor()).isNull();
    }

    @Test
    void getHomeFeedShouldRejectInvalidLimit() {
        assertThatThrownBy(() -> feedService.getHomeFeed("viewer", null, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("limit");
    }

    private static PostEntity post(String userId, String createdAt, String postId) {
        PostEntity entity = new PostEntity();
        entity.setUserId(userId);
        entity.setPostId(postId);
        entity.setCreatedAt(createdAt);
        entity.setUpdatedAt(createdAt);
        entity.setPostSortKey(PostSortKey.build(createdAt, postId));
        entity.setCaption("c");
        entity.setMediaUrl("https://example.test/" + postId);
        return entity;
    }
}
