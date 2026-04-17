package com.instagram.apigateway;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api-gateway")
public class GatewayController {

    @GetMapping("/health")
    public Map<String, Object> health() {
        return Map.of(
                "service", "api-gateway",
                "status", "UP",
                "timestamp", Instant.now().toString());
    }

    @GetMapping("/routes")
    public List<RouteSummary> routes() {
        return List.of(
                new RouteSummary("/posts/**", "post-service"),
                new RouteSummary("/follows/**", "follow-service"));
    }

    public record RouteSummary(String pathPattern, String targetService) {
    }
}
