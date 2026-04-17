package com.instagram.postservice.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreatePostRequest(
        @NotBlank String userId,
        @NotBlank @Size(max = 2200) String caption,
        @NotBlank String mediaUrl) {
}
