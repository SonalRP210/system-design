package com.instagram.postservice.service;

import com.instagram.postservice.api.dto.CreatePostRequest;
import com.instagram.postservice.api.dto.PostResponse;
import com.instagram.postservice.api.dto.UpdatePostRequest;
import com.instagram.postservice.entity.PostEntity;
import com.instagram.postservice.exception.PostNotFoundException;
import com.instagram.postservice.repository.PostRepository;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class DefaultPostService implements PostService {

    private final PostRepository postRepository;

    public DefaultPostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Override
    public PostResponse createPost(CreatePostRequest request) {
        String now = Instant.now().toString();

        PostEntity postEntity = new PostEntity();
        postEntity.setPostId(UUID.randomUUID().toString());
        postEntity.setUserId(request.userId());
        postEntity.setCaption(request.caption());
        postEntity.setMediaUrl(request.mediaUrl());
        postEntity.setCreatedAt(now);
        postEntity.setUpdatedAt(now);

        return mapToResponse(postRepository.save(postEntity));
    }

    @Override
    public PostResponse getPost(String postId) {
        return mapToResponse(requirePost(postId));
    }

    @Override
    public List<PostResponse> listPosts(String userId) {
        List<PostEntity> posts = (userId == null || userId.isBlank())
                ? postRepository.findAll()
                : postRepository.findByUserId(userId);

        return posts.stream().map(this::mapToResponse).toList();
    }

    @Override
    public PostResponse updatePost(String postId, UpdatePostRequest request) {
        PostEntity postEntity = requirePost(postId);

        boolean changed = false;
        if (request.caption() != null && !request.caption().isBlank()) {
            postEntity.setCaption(request.caption());
            changed = true;
        }
        if (request.mediaUrl() != null && !request.mediaUrl().isBlank()) {
            postEntity.setMediaUrl(request.mediaUrl());
            changed = true;
        }

        if (!changed) {
            throw new IllegalArgumentException("At least one field (caption/mediaUrl) must be provided");
        }

        postEntity.setUpdatedAt(Instant.now().toString());
        return mapToResponse(postRepository.save(postEntity));
    }

    @Override
    public void deletePost(String postId) {
        requirePost(postId);
        postRepository.deleteById(postId);
    }

    @Override
    public int countPosts() {
        return postRepository.findAll().size();
    }

    private PostEntity requirePost(String postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));
    }

    private PostResponse mapToResponse(PostEntity postEntity) {
        return new PostResponse(
                postEntity.getPostId(),
                postEntity.getUserId(),
                postEntity.getCaption(),
                postEntity.getMediaUrl(),
                postEntity.getCreatedAt(),
                Objects.requireNonNullElse(postEntity.getUpdatedAt(), postEntity.getCreatedAt()));
    }
}
