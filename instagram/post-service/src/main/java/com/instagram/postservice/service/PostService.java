package com.instagram.postservice.service;

import com.instagram.postservice.api.dto.CreatePostRequest;
import com.instagram.postservice.api.dto.PostResponse;
import com.instagram.postservice.api.dto.UpdatePostRequest;
import java.util.List;

public interface PostService {

    PostResponse createPost(CreatePostRequest request);

    PostResponse getPost(String postId);

    List<PostResponse> listPosts(String userId);

    PostResponse updatePost(String postId, UpdatePostRequest request);

    void deletePost(String postId);

    int countPosts();
}
