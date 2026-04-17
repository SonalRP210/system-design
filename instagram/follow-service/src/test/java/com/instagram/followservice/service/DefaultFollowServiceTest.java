package com.instagram.followservice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.instagram.followservice.api.dto.CreateFollowRequest;
import com.instagram.followservice.api.dto.FollowResponse;
import com.instagram.followservice.entity.FollowEntity;
import com.instagram.followservice.repository.FollowRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DefaultFollowServiceTest {

    @Mock
    private FollowRepository followRepository;

    private DefaultFollowService service;

    @BeforeEach
    void setUp() {
        service = new DefaultFollowService(followRepository);
    }

    @Test
    void followShouldPersistRelationship() {
        when(followRepository.exists("u1", "u2")).thenReturn(false);
        when(followRepository.save(any(FollowEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        FollowResponse response = service.follow("u1", new CreateFollowRequest("u2"));

        ArgumentCaptor<FollowEntity> captor = ArgumentCaptor.forClass(FollowEntity.class);
        verify(followRepository).save(captor.capture());
        FollowEntity saved = captor.getValue();

        assertThat(saved.getFollowerId()).isEqualTo("u1");
        assertThat(saved.getFollowedId()).isEqualTo("u2");
        assertThat(saved.getCreatedAt()).isNotBlank();
        assertThat(response.followerId()).isEqualTo("u1");
        assertThat(response.followedId()).isEqualTo("u2");
    }

    @Test
    void followShouldRejectSelfFollow() {
        assertThatThrownBy(() -> service.follow("u1", new CreateFollowRequest("u1")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot follow themselves");
    }

    @Test
    void followShouldRejectDuplicateRelationship() {
        when(followRepository.exists("u1", "u2")).thenReturn(true);

        assertThatThrownBy(() -> service.follow("u1", new CreateFollowRequest("u2")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already exists");
    }
}
