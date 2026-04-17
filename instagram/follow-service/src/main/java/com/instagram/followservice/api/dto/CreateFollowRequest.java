package com.instagram.followservice.api.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateFollowRequest(@NotBlank String followedId) {
}
