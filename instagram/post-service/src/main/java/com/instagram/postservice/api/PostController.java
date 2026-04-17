package com.instagram.postservice.api;

import com.instagram.postservice.api.dto.CreatePostRequest;
import com.instagram.postservice.api.dto.PostResponse;
import com.instagram.postservice.api.dto.UpdatePostRequest;
import com.instagram.postservice.config.DynamoDbProperties;
import com.instagram.postservice.service.PostService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;
    private final DynamoDbProperties dynamoDbProperties;

    public PostController(PostService postService, DynamoDbProperties dynamoDbProperties) {
        this.postService = postService;
        this.dynamoDbProperties = dynamoDbProperties;
    }

    @GetMapping("/health")
    public Map<String, Object> health() {
        return Map.of(
                "service", "post-service",
                "status", "UP",
                "table", dynamoDbProperties.getTableName(),
                "posts", postService.countPosts());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PostResponse createPost(@Valid @RequestBody CreatePostRequest request) {
        return postService.createPost(request);
    }

    @GetMapping("/{postId}")
    public PostResponse getPost(@PathVariable String postId) {
        return postService.getPost(postId);
    }

    @GetMapping
    public List<PostResponse> listPosts(@RequestParam(required = false) String userId) {
        return postService.listPosts(userId);
    }

    @PutMapping("/{postId}")
    public PostResponse updatePost(@PathVariable String postId, @Valid @RequestBody UpdatePostRequest request) {
        return postService.updatePost(postId, request);
    }

    @DeleteMapping("/{postId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePost(@PathVariable String postId) {
        postService.deletePost(postId);
    }
}
