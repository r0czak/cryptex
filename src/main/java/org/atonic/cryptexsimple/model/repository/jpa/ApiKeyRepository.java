package org.atonic.cryptexsimple.model.repository.jpa;

import org.atonic.cryptexsimple.model.entity.jpa.ApiKey;
import org.atonic.cryptexsimple.model.entity.jpa.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ApiKeyRepository extends JpaRepository<ApiKey, Long> {
    Optional<ApiKey> findApiKeyByUser(User user);

    Optional<ApiKey> findByKeyValueAndActiveIsTrue(UUID keyValue);

    Optional<ApiKey> findByKeyValue(UUID keyValue);

    void deleteByKeyValue(UUID keyValue);
}
