package com.instagram.postservice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.instagram.postservice.api.dto.CreatePostRequest;
import com.instagram.postservice.api.dto.PostResponse;
import com.instagram.postservice.api.dto.UpdatePostRequest;
import com.instagram.postservice.entity.PostEntity;
import com.instagram.postservice.exception.PostNotFoundException;
import com.instagram.postservice.repository.PostRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DefaultPostServiceTest {

    @Mock
    private PostRepository postRepository;

    private DefaultPostService service;

    @BeforeEach
    void setUp() {
        service = new DefaultPostService(postRepository);
    }

    @Test
    void createPostShouldPersistAndReturnCreatedPost() {
        when(postRepository.save(any(PostEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PostResponse response = service.createPost(new CreatePostRequest("user-1", "hello", "https://img/1.jpg"));

        ArgumentCaptor<PostEntity> captor = ArgumentCaptor.forClass(PostEntity.class);
        verify(postRepository).save(captor.capture());
        PostEntity saved = captor.getValue();

        assertThat(saved.getPostId()).isNotBlank();
        assertThat(saved.getUserId()).isEqualTo("user-1");
        assertThat(saved.getCaption()).isEqualTo("hello");
        assertThat(saved.getMediaUrl()).isEqualTo("https://img/1.jpg");
        assertThat(saved.getCreatedAt()).isNotBlank();
        assertThat(saved.getUpdatedAt()).isEqualTo(saved.getCreatedAt());
        assertThat(response.postId()).isEqualTo(saved.getPostId());
    }

    @Test
    void updatePostShouldRejectBlankUpdateRequest() {
        PostEntity existing = new PostEntity();
        existing.setPostId("p-1");
        existing.setUserId("user-1");
        existing.setCaption("old");
        existing.setMediaUrl("https://img/1.jpg");
        existing.setCreatedAt("2026-01-01T00:00:00Z");
        existing.setUpdatedAt("2026-01-01T00:00:00Z");

        when(postRepository.findById("p-1")).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> service.updatePost("p-1", new UpdatePostRequest("  ", "")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("At least one field");
        verify(postRepository, never()).save(any(PostEntity.class));
    }

    @Test
    void deletePostShouldThrowWhenPostDoesNotExist() {
        when(postRepository.findById("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deletePost("missing"))
                .isInstanceOf(PostNotFoundException.class);
        verify(postRepository, never()).deleteById(any(String.class));
    }
}
