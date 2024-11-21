package org.atonic.cryptexsimple.model.repository;

import org.atonic.cryptexsimple.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByAuth0UserId(String auth0UserId);

    Boolean existsByEmail(String email);
}
