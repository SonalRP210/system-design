package com.instagram.postservice.media;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.media")
public class MediaStorageProperties {

    /** {@code local} (filesystem + {@code /media}) or {@code s3}. */
    private String storageType = "s3";

    private String storagePath = "./storage/post-media";
    private String publicBaseUrl = "";

    /** S3 bucket when {@code storage-type} is {@code s3}. */
    private String bucket = "";

    /** Optional object key prefix, no leading slash. */
    private String keyPrefix = "";

    /**
     * Optional endpoint override for the S3 Java client only (e.g. {@code http://localstack:4566} in
     * Docker). When empty, {@link #s3Endpoint} is used for the client if set.
     */
    private String s3ClientEndpoint = "";

    /** Optional public / same-host endpoint used in {@code mediaUrl} when {@code public-base-url} is empty. */
    private String s3Endpoint = "";

    /** Use path-style access when using a custom S3 endpoint (set true for LocalStack/MinIO). */
    private boolean s3ForcePathStyle = false;

    public String getStorageType() {
        return storageType;
    }

    public void setStorageType(String storageType) {
        this.storageType = storageType;
    }

    public String getStoragePath() {
        return storagePath;
    }

    public void setStoragePath(String storagePath) {
        this.storagePath = storagePath;
    }

    public String getPublicBaseUrl() {
        return publicBaseUrl;
    }

    public void setPublicBaseUrl(String publicBaseUrl) {
        this.publicBaseUrl = publicBaseUrl;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getKeyPrefix() {
        return keyPrefix;
    }

    public void setKeyPrefix(String keyPrefix) {
        this.keyPrefix = keyPrefix;
    }

    public String getS3Endpoint() {
        return s3Endpoint;
    }

    public void setS3Endpoint(String s3Endpoint) {
        this.s3Endpoint = s3Endpoint;
    }

    public String getS3ClientEndpoint() {
        return s3ClientEndpoint;
    }

    public void setS3ClientEndpoint(String s3ClientEndpoint) {
        this.s3ClientEndpoint = s3ClientEndpoint;
    }

    public boolean isS3ForcePathStyle() {
        return s3ForcePathStyle;
    }

    public void setS3ForcePathStyle(boolean s3ForcePathStyle) {
        this.s3ForcePathStyle = s3ForcePathStyle;
    }
}
