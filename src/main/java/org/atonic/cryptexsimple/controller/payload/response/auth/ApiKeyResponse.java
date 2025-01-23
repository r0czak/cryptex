package org.atonic.cryptexsimple.controller.payload.response.auth;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class ApiKeyResponse {
    private UUID apiKey;
    private String description;
    private LocalDateTime expiresAt;
    private boolean active;
}
