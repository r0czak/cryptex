package org.atonic.cryptexsimple.service;

import org.atonic.cryptexsimple.model.entity.jpa.ApiKey;
import org.atonic.cryptexsimple.model.entity.jpa.User;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface ApiKeyService {
    Optional<User> getUserFromApiKey(UUID keyValue);

    Optional<ApiKey> getApiKey(User user);

    ApiKey createApiKey(User user, String description, LocalDateTime expiresAt);

    void deactivateApiKey(UUID keyValue);
}
