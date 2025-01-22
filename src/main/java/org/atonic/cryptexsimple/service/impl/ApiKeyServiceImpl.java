package org.atonic.cryptexsimple.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.atonic.cryptexsimple.model.entity.jpa.ApiKey;
import org.atonic.cryptexsimple.model.entity.jpa.User;
import org.atonic.cryptexsimple.model.repository.jpa.ApiKeyRepository;
import org.atonic.cryptexsimple.service.ApiKeyService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@AllArgsConstructor
public class ApiKeyServiceImpl implements ApiKeyService {
    private final ApiKeyRepository apiKeyRepository;

    @Override
    public Optional<User> getUserFromApiKey(UUID keyValue) {
        Optional<ApiKey> apiKeyOptional = apiKeyRepository.findByKeyValueAndActiveIsTrue(keyValue);
        if (apiKeyOptional.isPresent()) {
            apiKeyOptional.get().setUpdatedAt(LocalDateTime.now());
            apiKeyRepository.save(apiKeyOptional.get());
        }
        return apiKeyOptional.map(ApiKey::getUser);
    }

    @Override
    public Optional<ApiKey> getApiKey(User user) {
        return apiKeyRepository.findApiKeyByUser(user);
    }

    @Override
    public ApiKey createApiKey(User user, String description, LocalDateTime expiresAt) {
        UUID key = UUID.randomUUID();
        ApiKey apiKey = new ApiKey();
        apiKey.setKeyValue(key);
        apiKey.setUser(user);
        apiKey.setDescription(description);
        apiKey.setActive(true);
        apiKey.setCreatedAt(LocalDateTime.now());
        apiKey.setExpiresAt(expiresAt);

        return apiKeyRepository.save(apiKey);
    }

    @Override
    public void deactivateApiKey(UUID keyValue) {
        apiKeyRepository.findByKeyValue(keyValue).ifPresent(apiKey -> {
            apiKey.setActive(false);
            apiKeyRepository.save(apiKey);
        });
    }
}
