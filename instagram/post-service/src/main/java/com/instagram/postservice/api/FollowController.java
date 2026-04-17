package com.instagram.postservice.api;

import com.instagram.postservice.api.dto.FollowRequest;
import com.instagram.postservice.security.UserIdentityResolver;
import com.instagram.postservice.service.FollowService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/follows")
public class FollowController {

    private final FollowService followService;
    private final UserIdentityResolver userIdentityResolver;

    public FollowController(FollowService followService, UserIdentityResolver userIdentityResolver) {
        this.followService = followService;
        this.userIdentityResolver = userIdentityResolver;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void follow(
            @RequestHeader(value = "X-User-Id", required = false) String xUserId,
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @Valid @RequestBody FollowRequest request) {
        String followerId = userIdentityResolver.resolveUserId(xUserId, authorization);
        followService.follow(followerId, request);
    }

    @DeleteMapping("/{followedId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unfollow(
            @RequestHeader(value = "X-User-Id", required = false) String xUserId,
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @PathVariable String followedId) {
        String followerId = userIdentityResolver.resolveUserId(xUserId, authorization);
        followService.unfollow(followerId, followedId);
    }
}
