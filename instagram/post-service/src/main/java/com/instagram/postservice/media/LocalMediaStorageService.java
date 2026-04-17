package com.instagram.postservice.media;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
@ConditionalOnProperty(prefix = "app.media", name = "storage-type", havingValue = "local")
public class LocalMediaStorageService implements MediaStorageService {

    private final MediaStorageProperties properties;

    public LocalMediaStorageService(MediaStorageProperties properties) {
        this.properties = properties;
    }

    @Override
    public String store(MultipartFile file) {
        try {
            Path storageDir = Path.of(properties.getStoragePath()).toAbsolutePath().normalize();
            Files.createDirectories(storageDir);

            String extension = extractExtension(file.getOriginalFilename());
            String filename = UUID.randomUUID() + extension;
            Path targetFile = storageDir.resolve(filename);
            Files.copy(file.getInputStream(), targetFile, StandardCopyOption.REPLACE_EXISTING);

            String baseUrl = normalizeBaseUrl(properties.getPublicBaseUrl());
            return baseUrl + "/" + filename;
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to store media", exception);
        }
    }

    private String extractExtension(String originalFilename) {
        String clean = StringUtils.hasText(originalFilename) ? originalFilename : "";
        int dotIndex = clean.lastIndexOf('.');
        return dotIndex >= 0 ? clean.substring(dotIndex) : "";
    }

    private String normalizeBaseUrl(String baseUrl) {
        if (!StringUtils.hasText(baseUrl)) {
            return "/media";
        }
        return baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
    }
}
