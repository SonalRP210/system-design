package com.instagram.postservice.config;

import com.instagram.postservice.media.MediaStorageProperties;
import java.net.URI;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.checksums.RequestChecksumCalculation;
import software.amazon.awssdk.core.checksums.ResponseChecksumValidation;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

@Configuration
@ConditionalOnProperty(prefix = "app.media", name = "storage-type", havingValue = "s3")
public class S3ClientConfig {

    @Bean
    public S3Client s3Client(MediaStorageProperties media, DynamoDbProperties dynamo) {
        var builder = S3Client.builder()
                .region(Region.of(dynamo.getRegion()))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(dynamo.getAccessKey(), dynamo.getSecretKey())))
                // LocalStack and other S3-compatible stores reject SDK 2.30+ default checksum trailers.
                .requestChecksumCalculation(RequestChecksumCalculation.WHEN_REQUIRED)
                .responseChecksumValidation(ResponseChecksumValidation.WHEN_REQUIRED);

        String clientEndpoint = resolveClientEndpoint(media);
        if (StringUtils.hasText(clientEndpoint)) {
            builder.endpointOverride(URI.create(clientEndpoint.trim()));
            boolean pathStyle = media.isS3ForcePathStyle();
            builder.serviceConfiguration(S3Configuration.builder()
                    .pathStyleAccessEnabled(pathStyle)
                    .build());
        } else {
            builder.serviceConfiguration(S3Configuration.builder()
                    .pathStyleAccessEnabled(false)
                    .build());
        }

        return builder.build();
    }

    private static String resolveClientEndpoint(MediaStorageProperties media) {
        if (StringUtils.hasText(media.getS3ClientEndpoint())) {
            return media.getS3ClientEndpoint();
        }
        return media.getS3Endpoint();
    }
}
