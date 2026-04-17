package com.instagram.postservice.api.dto;

/** Response for multipart create-with-media; client only needs the id for step 1. */
public record PostIdResponse(String postId) {}
