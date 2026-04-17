package com.instagram.postservice.api.dto;

import jakarta.validation.constraints.NotBlank;

public record FollowRequest(@NotBlank String followedId) {
}
