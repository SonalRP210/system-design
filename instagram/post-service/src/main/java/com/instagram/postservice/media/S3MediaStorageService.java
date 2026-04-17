package com.instagram.postservice.media;

import com.instagram.postservice.config.DynamoDbProperties;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.util.UUID;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@ConditionalOnProperty(prefix = "app.media", name = "storage-type", havingValue = "s3")
public class S3MediaStorageService implements MediaStorageService {

    private final S3Client s3Client;
    private final MediaStorageProperties properties;
    private final DynamoDbProperties dynamoProperties;

    public S3MediaStorageService(
            S3Client s3Client, MediaStorageProperties properties, DynamoDbProperties dynamoProperties) {
        this.s3Client = s3Client;
        this.properties = properties;
        this.dynamoProperties = dynamoProperties;
    }

    @PostConstruct
    void validate() {
        if (!StringUtils.hasText(properties.getBucket())) {
            throw new IllegalStateException("app.media.bucket is required when app.media.storage-type=s3");
        }
    }

    @Override
    public String store(MultipartFile file) {
        String extension = extractExtension(file.getOriginalFilename());
        String filename = UUID.randomUUID() + extension;
        String key = buildObjectKey(filename);
        String contentType = StringUtils.hasText(file.getContentType())
                ? file.getContentType()
                : "application/octet-stream";

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(properties.getBucket())
                .key(key)
                .contentType(contentType)
                .build();

        try {
            // Always buffer small uploads: avoids stream/size mismatches when proxied (e.g. via API Gateway).
            byte[] bytes = file.getBytes();
            s3Client.putObject(request, RequestBody.fromBytes(bytes));
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read upload for S3", e);
        }

        return buildPublicUrl(key);
    }

    private String buildObjectKey(String filename) {
        String prefix = properties.getKeyPrefix();
        if (!StringUtils.hasText(prefix)) {
            return filename;
        }
        String normalized = prefix.endsWith("/") ? prefix : prefix + "/";
        return normalized + filename;
    }

    private String buildPublicUrl(String key) {
        if (StringUtils.hasText(properties.getPublicBaseUrl())) {
            String base = properties.getPublicBaseUrl().trim();
            if (base.endsWith("/")) {
                base = base.substring(0, base.length() - 1);
            }
            return base + "/" + key;
        }
        if (StringUtils.hasText(properties.getS3Endpoint())) {
            String ep = properties.getS3Endpoint().trim();
            if (ep.endsWith("/")) {
                ep = ep.substring(0, ep.length() - 1);
            }
            return ep + "/" + properties.getBucket() + "/" + key;
        }
        String region = dynamoProperties.getRegion();
        return "https://" + properties.getBucket() + ".s3." + region + ".amazonaws.com/" + key;
    }

    private String extractExtension(String originalFilename) {
        String clean = StringUtils.hasText(originalFilename) ? originalFilename : "";
        int dotIndex = clean.lastIndexOf('.');
        return dotIndex >= 0 ? clean.substring(dotIndex) : "";
    }
}
