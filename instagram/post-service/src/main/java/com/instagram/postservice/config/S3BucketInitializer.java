package com.instagram.postservice.config;

import com.instagram.postservice.media.MediaStorageProperties;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

/**
 * Ensures the media bucket exists (LocalStack init scripts can fail silently if {@code awslocal} is missing).
 */
@Component
@ConditionalOnProperty(prefix = "app.media", name = "storage-type", havingValue = "s3")
public class S3BucketInitializer {

    private static final Logger log = LoggerFactory.getLogger(S3BucketInitializer.class);

    private final S3Client s3Client;
    private final MediaStorageProperties mediaProperties;

    public S3BucketInitializer(S3Client s3Client, MediaStorageProperties mediaProperties) {
        this.s3Client = s3Client;
        this.mediaProperties = mediaProperties;
    }

    @PostConstruct
    void ensureBucketExists() {
        String bucket = mediaProperties.getBucket();
        if (!StringUtils.hasText(bucket)) {
            return;
        }
        try {
            s3Client.headBucket(HeadBucketRequest.builder().bucket(bucket).build());
            log.debug("S3 bucket already present: {}", bucket);
        } catch (S3Exception e) {
            if (e.statusCode() != 404) {
                throw e;
            }
            try {
                s3Client.createBucket(CreateBucketRequest.builder().bucket(bucket).build());
                log.info("Created S3 bucket: {}", bucket);
            } catch (S3Exception createError) {
                String code = createError.awsErrorDetails() != null
                        ? createError.awsErrorDetails().errorCode()
                        : "";
                if ("BucketAlreadyOwnedByYou".equals(code) || "BucketAlreadyExists".equals(code)) {
                    log.debug("S3 bucket race or already exists: {}", bucket);
                    return;
                }
                throw createError;
            }
        }
    }
}
