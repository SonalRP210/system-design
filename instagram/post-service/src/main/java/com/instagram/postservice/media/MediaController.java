package com.instagram.postservice.media;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("/media")
@ConditionalOnProperty(prefix = "app.media", name = "storage-type", havingValue = "local")
public class MediaController {

    private final MediaStorageProperties properties;

    public MediaController(MediaStorageProperties properties) {
        this.properties = properties;
    }

    @GetMapping("/{filename:.+}")
    public ResponseEntity<Resource> getMedia(@PathVariable String filename) {
        try {
            Path root = Path.of(properties.getStoragePath()).toAbsolutePath().normalize();
            Path file = root.resolve(filename).normalize();
            if (!file.startsWith(root)) {
                throw new ResponseStatusException(NOT_FOUND, "Media not found");
            }

            Resource resource = new UrlResource(file.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new ResponseStatusException(NOT_FOUND, "Media not found");
            }

            MediaType contentType = resolveContentType(file);
            return ResponseEntity.ok()
                    .contentType(contentType)
                    .header(HttpHeaders.CACHE_CONTROL, "public, max-age=3600")
                    .body(resource);
        } catch (IOException exception) {
            throw new ResponseStatusException(NOT_FOUND, "Media not found");
        }
    }

    private MediaType resolveContentType(Path file) throws IOException {
        String contentType = Files.probeContentType(file);
        if (contentType == null || contentType.isBlank()) {
            return MediaType.APPLICATION_OCTET_STREAM;
        }
        return MediaType.parseMediaType(contentType);
    }
}
