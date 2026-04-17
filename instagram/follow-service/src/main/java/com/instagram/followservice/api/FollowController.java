package com.instagram.followservice.api;

import com.instagram.followservice.api.dto.CreateFollowRequest;
import com.instagram.followservice.api.dto.FollowResponse;
import com.instagram.followservice.service.FollowService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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

    private static final String AUTH_USER_HEADER = "X-User-Id";

    private final FollowService followService;

    public FollowController(FollowService followService) {
        this.followService = followService;
    }

    @GetMapping("/health")
    public Map<String, Object> health() {
        return Map.of(
                "service", "follow-service",
                "status", "UP",
                "users", followService.countUsers(),
                "relationships", followService.countRelationships());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FollowResponse follow(
            @RequestHeader(AUTH_USER_HEADER) String authenticatedUserId,
            @Valid @RequestBody CreateFollowRequest request) {
        return followService.follow(authenticatedUserId, request);
    }

    @DeleteMapping("/{followedId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unfollow(
            @RequestHeader(AUTH_USER_HEADER) String authenticatedUserId,
            @PathVariable String followedId) {
        followService.unfollow(authenticatedUserId, followedId);
    }

    @GetMapping("/me/following")
    public List<String> myFollowing(@RequestHeader(AUTH_USER_HEADER) String authenticatedUserId) {
        return followService.listFollowing(authenticatedUserId);
    }

    @GetMapping("/{userId}/following")
    public List<String> following(@PathVariable String userId) {
        return followService.listFollowing(userId);
    }

    @GetMapping("/{userId}/followers")
    public List<String> followers(@PathVariable String userId) {
        return followService.listFollowers(userId);
    }
}
