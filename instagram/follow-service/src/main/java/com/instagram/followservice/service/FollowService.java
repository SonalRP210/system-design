package com.instagram.followservice.service;

import com.instagram.followservice.api.dto.CreateFollowRequest;
import com.instagram.followservice.api.dto.FollowResponse;
import java.util.List;

public interface FollowService {

    FollowResponse follow(String followerId, CreateFollowRequest request);

    void unfollow(String followerId, String followedId);

    List<String> listFollowing(String userId);

    List<String> listFollowers(String userId);

    int countUsers();

    int countRelationships();
}
