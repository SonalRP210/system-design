package com.instagram.followservice.service;

import com.instagram.followservice.api.dto.CreateFollowRequest;
import com.instagram.followservice.api.dto.FollowResponse;
import com.instagram.followservice.entity.FollowEntity;
import com.instagram.followservice.repository.FollowRepository;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class DefaultFollowService implements FollowService {

    private final FollowRepository followRepository;

    public DefaultFollowService(FollowRepository followRepository) {
        this.followRepository = followRepository;
    }

    @Override
    public FollowResponse follow(String followerId, CreateFollowRequest request) {
        String followedId = request.followedId();
        if (followerId.equals(followedId)) {
            throw new IllegalArgumentException("A user cannot follow themselves");
        }

        if (followRepository.exists(followerId, followedId)) {
            throw new IllegalArgumentException("Follow relationship already exists");
        }

        FollowEntity followEntity = new FollowEntity();
        followEntity.setFollowerId(followerId);
        followEntity.setFollowedId(followedId);
        followEntity.setCreatedAt(Instant.now().toString());

        return mapToResponse(followRepository.save(followEntity));
    }

    @Override
    public void unfollow(String followerId, String followedId) {
        followRepository.delete(followerId, followedId);
    }

    @Override
    public List<String> listFollowing(String userId) {
        return followRepository.findFollowingByFollowerId(userId);
    }

    @Override
    public List<String> listFollowers(String userId) {
        return followRepository.findFollowersByFollowedId(userId);
    }

    @Override
    public int countUsers() {
        return followRepository.countUsers();
    }

    @Override
    public int countRelationships() {
        return followRepository.countRelationships();
    }

    private FollowResponse mapToResponse(FollowEntity followEntity) {
        return new FollowResponse(
                followEntity.getFollowerId(),
                followEntity.getFollowedId(),
                followEntity.getCreatedAt());
    }
}
