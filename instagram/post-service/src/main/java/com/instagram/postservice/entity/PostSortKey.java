package com.instagram.postservice.entity;

public final class PostSortKey {

    private PostSortKey() {
    }

    public static String build(String createdAt, String postId) {
        return createdAt + "#" + postId;
    }
}
