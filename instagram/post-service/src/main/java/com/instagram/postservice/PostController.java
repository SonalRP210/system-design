package com.instagram.postservice;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/posts")
public class PostController {

    private final Map<String, PostResponse> posts = new ConcurrentHashMap<>();

    @GetMapping("/health")
    public Map<String, Object> health() {
        return Map.of("service", "post-service", "status", "UP", "posts", posts.size());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PostResponse createPost(@Valid @RequestBody CreatePostRequest request) {
        PostResponse response = new PostResponse(
                UUID.randomUUID().toString(),
                request.userId(),
                request.caption(),
                request.mediaUrl(),
                Instant.now().toString());
        posts.put(response.id(), response);
        return response;
    }

    @GetMapping("/{postId}")
    public PostResponse getPost(@PathVariable String postId) {
        PostResponse response = posts.get(postId);
        if (response == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found");
        }
        return response;
    }

    @GetMapping
    public List<PostResponse> listPosts(@RequestParam(required = false) String userId) {
        if (userId == null || userId.isBlank()) {
            return new ArrayList<>(posts.values());
        }
        return posts.values().stream()
                .filter(post -> userId.equals(post.userId()))
                .toList();
    }

    public record CreatePostRequest(
            @NotBlank String userId,
            @NotBlank String caption,
            @NotBlank String mediaUrl) {
    }

    public record PostResponse(
            String id,
            String userId,
            String caption,
            String mediaUrl,
            String createdAt) {
    }
}
