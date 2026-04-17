package com.instagram.postservice.api.dto;

import jakarta.validation.constraints.Size;

public record UpdatePostRequest(
        @Size(max = 2200) String caption,
        String mediaUrl) {
}
