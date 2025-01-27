package org.atonic.cryptexsimple.controller;

import lombok.AllArgsConstructor;
import org.atonic.cryptexsimple.controller.payload.request.auth.CreateApiKeyRequest;
import org.atonic.cryptexsimple.controller.payload.response.AuthResponse;
import org.atonic.cryptexsimple.controller.payload.response.auth.ApiKeyResponse;
import org.atonic.cryptexsimple.model.entity.jpa.ApiKey;
import org.atonic.cryptexsimple.model.entity.jpa.User;
import org.atonic.cryptexsimple.service.ApiKeyService;
import org.atonic.cryptexsimple.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("api/v1/auth")
@AllArgsConstructor
public class AuthController {
    private final UserService userService;
    private final ApiKeyService apiKeyService;

    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> authenticateUser(@AuthenticationPrincipal Jwt jwt) {
        User user = userService.registerUser(jwt);
        return ResponseEntity.ok(AuthResponse.builder()
            .jwt(jwt)
            .email(user.getEmail())
            .build());
    }

    @PostMapping("/api-key/create")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public ResponseEntity<ApiKeyResponse> createApiKey(@RequestBody CreateApiKeyRequest request,
                                                       @AuthenticationPrincipal Jwt jwt) {
        User user = userService.getUser(jwt).orElseThrow();
        ApiKey apiKey = apiKeyService.createApiKey(user, request.getDescription(), request.getExpiresAt());
        return ResponseEntity.ok(ApiKeyResponse.builder()
            .apiKey(apiKey.getKeyValue())
            .description(apiKey.getDescription())
            .expiresAt(apiKey.getExpiresAt())
            .active(apiKey.isActive())
            .build());
    }

    @GetMapping("/api-key/key")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public ResponseEntity<ApiKeyResponse> getApiKey(@AuthenticationPrincipal Jwt jwt) {
        User user = userService.getUser(jwt).orElseThrow();
        Optional<ApiKey> apiKey = apiKeyService.getApiKey(user);

        return apiKey.map(key -> ResponseEntity.ok(ApiKeyResponse.builder()
            .apiKey(key.getKeyValue())
            .description(key.getDescription())
            .expiresAt(key.getExpiresAt())
            .active(key.isActive())
            .build())).orElseGet(() -> ResponseEntity.notFound().build());

    }

    @DeleteMapping("/api-key/delete")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public ResponseEntity<Void> deleteApiKey(@AuthenticationPrincipal Jwt jwt) {
        User user = userService.getUser(jwt).orElseThrow();
        Optional<ApiKey> apiKey = apiKeyService.getApiKey(user);
        if (apiKey.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        apiKeyService.deactivateApiKey(apiKey.get().getKeyValue());
        return ResponseEntity.noContent().build();
    }
}
