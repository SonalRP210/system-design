package com.instagram.apigateway.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.config.WebFluxConfigurer;

/**
 * Ensures multipart and large bodies can pass through the gateway (defaults are often ~256KB).
 */
@Configuration
public class GatewayWebFluxConfig implements WebFluxConfigurer {

    private static final int MAX_IN_MEMORY = 40 * 1024 * 1024;

    @Override
    public void configureHttpMessageCodecs(ServerCodecConfigurer configurer) {
        configurer.defaultCodecs().maxInMemorySize(MAX_IN_MEMORY);
    }
}
