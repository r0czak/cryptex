package org.atonic.cryptexsimple.service;


import org.atonic.cryptexsimple.model.entity.User;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Optional;

public interface UserService {
    User getOrRegisterUser(Jwt jwt);

    User registerUser(String auth0UserId, String email);

    Optional<User> findUserByAuth0UserId(String auth0UserId);
}
