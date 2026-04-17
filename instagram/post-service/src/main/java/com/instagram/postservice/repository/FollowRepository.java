package com.instagram.postservice.repository;

import com.instagram.postservice.entity.FollowEntity;
import java.util.List;

public interface FollowRepository {

    FollowEntity save(FollowEntity follow);

    void delete(String followerId, String followedId);

    List<String> findFollowedUserIds(String followerId);
}
