package com.instagram.postservice.service;

import com.instagram.postservice.api.dto.FollowRequest;
import com.instagram.postservice.entity.FollowEntity;
import com.instagram.postservice.repository.FollowRepository;
import java.time.Instant;
import org.springframework.stereotype.Service;

@Service
public class DefaultFollowService implements FollowService {

    private final FollowRepository followRepository;

    public DefaultFollowService(FollowRepository followRepository) {
        this.followRepository = followRepository;
    }

    @Override
    public void follow(String followerId, FollowRequest request) {
        String followedId = request.followedId().trim();
        if (followedId.isEmpty()) {
            throw new IllegalArgumentException("followedId is required");
        }
        if (followerId.equals(followedId)) {
            throw new IllegalArgumentException("Users cannot follow themselves");
        }

        FollowEntity entity = new FollowEntity();
        entity.setFollowerId(followerId);
        entity.setFollowedId(followedId);
        entity.setCreatedAt(Instant.now().toString());
        followRepository.save(entity);
    }

    @Override
    public void unfollow(String followerId, String followedId) {
        if (followedId == null || followedId.isBlank()) {
            throw new IllegalArgumentException("followedId is required");
        }
        followRepository.delete(followerId, followedId.trim());
    }
}
