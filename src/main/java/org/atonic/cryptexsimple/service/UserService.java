package org.atonic.cryptexsimple.service;


import org.atonic.cryptexsimple.model.entity.User;

import java.util.Optional;

public interface UserService {
    User registerUser(User user);
    Optional<User> findByUserName(String username);
}
