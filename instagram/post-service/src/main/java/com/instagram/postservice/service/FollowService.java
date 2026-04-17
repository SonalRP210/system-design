package com.instagram.postservice.service;

import com.instagram.postservice.api.dto.FollowRequest;

public interface FollowService {

    void follow(String followerId, FollowRequest request);

    void unfollow(String followerId, String followedId);
}
