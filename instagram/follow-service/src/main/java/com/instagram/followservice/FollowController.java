package com.instagram.followservice;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/follows")
public class FollowController {

    private final Map<String, Set<String>> following = new ConcurrentHashMap<>();

    @GetMapping("/health")
    public Map<String, Object> health() {
        return Map.of("service", "follow-service", "status", "UP", "users", following.size());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FollowResponse follow(@Valid @RequestBody FollowRequest request) {
        following
                .computeIfAbsent(request.followerId(), ignored -> ConcurrentHashMap.newKeySet())
                .add(request.followeeId());
        return new FollowResponse(request.followerId(), request.followeeId(), true);
    }

    @DeleteMapping
    public FollowResponse unfollow(@RequestParam String followerId, @RequestParam String followeeId) {
        Set<String> followees = following.getOrDefault(followerId, ConcurrentHashMap.newKeySet());
        boolean removed = followees.remove(followeeId);
        return new FollowResponse(followerId, followeeId, removed);
    }

    @GetMapping("/{userId}/following")
    public List<String> following(@PathVariable String userId) {
        return following.getOrDefault(userId, Set.of()).stream().sorted().toList();
    }

    @GetMapping("/{userId}/followers")
    public List<String> followers(@PathVariable String userId) {
        return following.entrySet().stream()
                .filter(entry -> entry.getValue().contains(userId))
                .map(Map.Entry::getKey)
                .sorted()
                .toList();
    }

    public record FollowRequest(@NotBlank String followerId, @NotBlank String followeeId) {
    }

    public record FollowResponse(String followerId, String followeeId, boolean active) {
    }
}
