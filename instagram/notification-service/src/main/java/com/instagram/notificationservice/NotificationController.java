package com.instagram.notificationservice;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.time.Instant;
import java.util.ArrayList;
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

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final Map<String, List<NotificationResponse>> notificationsByUser = new ConcurrentHashMap<>();

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("service", "notification-service", "status", "UP");
    }

    @PostMapping("/publish")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public NotificationResponse publish(@Valid @RequestBody NotificationRequest request) {
        NotificationResponse response = new NotificationResponse(
                UUID.randomUUID().toString(),
                request.userId(),
                request.type(),
                request.message(),
                Instant.now().toString());
        notificationsByUser
                .computeIfAbsent(request.userId(), ignored -> new ArrayList<>())
                .add(response);
        return response;
    }

    @GetMapping("/{userId}")
    public List<NotificationResponse> listForUser(@PathVariable String userId) {
        return notificationsByUser.getOrDefault(userId, List.of());
    }

    public record NotificationRequest(
            @NotBlank String userId,
            @NotBlank String type,
            @NotBlank String message) {
    }

    public record NotificationResponse(
            String id,
            String userId,
            String type,
            String message,
            String createdAt) {
    }
}
