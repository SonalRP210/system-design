package com.instagram.followservice.repository;

import com.instagram.followservice.entity.FollowEntity;
import java.util.List;

public interface FollowRepository {

    FollowEntity save(FollowEntity followEntity);

    boolean delete(String followerId, String followedId);

    boolean exists(String followerId, String followedId);

    List<String> findFollowingByFollowerId(String followerId);

    List<String> findFollowersByFollowedId(String followedId);

    int countUsers();

    int countRelationships();
}
