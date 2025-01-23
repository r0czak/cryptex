package org.atonic.cryptexsimple.controller.payload.request.auth;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateApiKeyRequest {
    private String description;
    private LocalDateTime expiresAt;
}
