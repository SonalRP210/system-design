package com.instagram.postservice.exception;

public class PostNotFoundException extends RuntimeException {

    public PostNotFoundException(String postId) {
        super("Post not found for id: " + postId);
    }
}
