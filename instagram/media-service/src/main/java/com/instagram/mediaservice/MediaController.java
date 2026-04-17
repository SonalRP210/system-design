package com.instagram.mediaservice;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/media")
public class MediaController {

    private final Map<String, MediaMetadata> media = new ConcurrentHashMap<>();

    @GetMapping("/health")
    public Map<String, Object> health() {
        return Map.of("service", "media-service", "status", "UP", "assets", media.size());
    }

    @PostMapping("/upload")
    @ResponseStatus(HttpStatus.CREATED)
    public MediaMetadata upload(@Valid @RequestBody UploadMediaRequest request) {
        String mediaId = UUID.randomUUID().toString();
        MediaMetadata metadata = new MediaMetadata(
                mediaId,
                request.fileName(),
                request.contentType(),
                request.sizeBytes(),
                "https://cdn.example.com/instagram/" + mediaId,
                Instant.now().toString());
        media.put(mediaId, metadata);
        return metadata;
    }

    @GetMapping("/{mediaId}")
    public MediaMetadata getMedia(@PathVariable String mediaId) {
        MediaMetadata metadata = media.get(mediaId);
        if (metadata == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Media not found");
        }
        return metadata;
    }

    @GetMapping
    public List<MediaMetadata> listMedia() {
        return media.values().stream().toList();
    }

    public record UploadMediaRequest(
            @NotBlank String fileName,
            @NotBlank String contentType,
            @Positive long sizeBytes) {
    }

    public record MediaMetadata(
            String id,
            String fileName,
            String contentType,
            long sizeBytes,
            String cdnUrl,
            String createdAt) {
    }
}
