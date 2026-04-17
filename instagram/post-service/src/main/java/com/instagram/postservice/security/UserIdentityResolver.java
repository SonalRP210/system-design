package com.instagram.postservice.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class UserIdentityResolver {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String resolveUserId(String xUserIdHeader, String authorizationHeader) {
        if (StringUtils.hasText(xUserIdHeader)) {
            return xUserIdHeader.trim();
        }

        if (!StringUtils.hasText(authorizationHeader) || !authorizationHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Missing authenticated user identity");
        }

        String token = authorizationHeader.substring("Bearer ".length()).trim();
        String[] parts = token.split("\\.");
        if (parts.length < 2) {
            throw new IllegalArgumentException("Invalid bearer token format");
        }

        try {
            byte[] payloadBytes = Base64.getUrlDecoder().decode(parts[1]);
            JsonNode payload = objectMapper.readTree(new String(payloadBytes, StandardCharsets.UTF_8));
            String sub = payload.path("sub").asText();
            if (!StringUtils.hasText(sub)) {
                throw new IllegalArgumentException("Bearer token does not include subject");
            }
            return sub;
        } catch (Exception exception) {
            throw new IllegalArgumentException("Unable to resolve user identity from bearer token");
        }
    }
}
