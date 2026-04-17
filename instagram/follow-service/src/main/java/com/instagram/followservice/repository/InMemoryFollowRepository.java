package com.instagram.followservice.repository;

import com.instagram.followservice.entity.FollowEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryFollowRepository implements FollowRepository {

    private final Map<String, Set<String>> followingByUser = new ConcurrentHashMap<>();

    @Override
    public FollowEntity save(FollowEntity followEntity) {
        followingByUser
                .computeIfAbsent(followEntity.getFollowerId(), ignored -> ConcurrentHashMap.newKeySet())
                .add(followEntity.getFollowedId());
        return followEntity;
    }

    @Override
    public boolean delete(String followerId, String followedId) {
        Set<String> following = followingByUser.get(followerId);
        if (following == null) {
            return false;
        }

        boolean removed = following.remove(followedId);
        if (following.isEmpty()) {
            followingByUser.remove(followerId);
        }
        return removed;
    }

    @Override
    public boolean exists(String followerId, String followedId) {
        return followingByUser.getOrDefault(followerId, Set.of()).contains(followedId);
    }

    @Override
    public List<String> findFollowingByFollowerId(String followerId) {
        return followingByUser.getOrDefault(followerId, Set.of()).stream().sorted().toList();
    }

    @Override
    public List<String> findFollowersByFollowedId(String followedId) {
        List<String> followers = new ArrayList<>();
        followingByUser.forEach((followerId, followees) -> {
            if (followees.contains(followedId)) {
                followers.add(followerId);
            }
        });
        followers.sort(String::compareTo);
        return followers;
    }

    @Override
    public int countUsers() {
        return followingByUser.size();
    }

    @Override
    public int countRelationships() {
        return followingByUser.values().stream().mapToInt(Set::size).sum();
    }
}
