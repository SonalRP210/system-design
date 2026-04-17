package com.instagram.postservice.repository;

import com.instagram.postservice.entity.PostEntity;
import java.util.List;
import java.util.Optional;

public interface PostRepository {

    PostEntity save(PostEntity postEntity);

    Optional<PostEntity> findById(String postId);

    List<PostEntity> findAll();

    List<PostEntity> findByUserId(String userId);

    void deleteById(String postId);
}
