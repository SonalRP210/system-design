package com.instagram.postservice.api;

import com.instagram.postservice.api.dto.CreatePostRequest;
import com.instagram.postservice.api.dto.PostIdResponse;
import com.instagram.postservice.api.dto.PostResponse;
import com.instagram.postservice.api.dto.UpdatePostRequest;
import com.instagram.postservice.config.DynamoDbProperties;
import com.instagram.postservice.media.MediaStorageService;
import com.instagram.postservice.security.UserIdentityResolver;
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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;
    private final DynamoDbProperties dynamoDbProperties;
    private final MediaStorageService mediaStorageService;
    private final UserIdentityResolver userIdentityResolver;

    public PostController(
            PostService postService,
            DynamoDbProperties dynamoDbProperties,
            MediaStorageService mediaStorageService,
            UserIdentityResolver userIdentityResolver) {
        this.postService = postService;
        this.dynamoDbProperties = dynamoDbProperties;
        this.mediaStorageService = mediaStorageService;
        this.userIdentityResolver = userIdentityResolver;
    }

    @GetMapping("/health")
    public Map<String, Object> health() {
        return Map.of(
                "service", "post-service",
                "status", "UP",
                "table", dynamoDbProperties.getTableName(),
                "followTable", dynamoDbProperties.getFollowTableName(),
                "posts", postService.countPosts());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PostResponse createPost(@Valid @RequestBody CreatePostRequest request) {
        return postService.createPost(request);
    }

    @PostMapping("/upload")
    @ResponseStatus(HttpStatus.CREATED)
    public PostIdResponse uploadPost(
            @RequestHeader(value = "X-User-Id", required = false) String xUserId,
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestParam("file") MultipartFile file,
            @RequestParam("caption") String caption) {
        validateUploadRequest(file, caption);

        String userId = userIdentityResolver.resolveUserId(xUserId, authorization);
        String mediaUrl = mediaStorageService.store(file);
        PostResponse created = postService.createPost(new CreatePostRequest(userId, caption, mediaUrl));
        return new PostIdResponse(created.postId());
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

    private void validateUploadRequest(MultipartFile file, String caption) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("file is required");
        }
        String contentType = file.getContentType();
        if (contentType == null || !(contentType.startsWith("image/") || contentType.startsWith("video/"))) {
            throw new IllegalArgumentException("Only image and video uploads are supported");
        }
        if (caption == null || caption.isBlank()) {
            throw new IllegalArgumentException("caption is required");
        }
        if (caption.length() > 2200) {
            throw new IllegalArgumentException("caption must be at most 2200 characters");
        }
    }
}
