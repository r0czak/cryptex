package org.atonic.cryptexsimple.controller;

import lombok.AllArgsConstructor;
import org.atonic.cryptexsimple.controller.payload.response.AuthResponse;
import org.atonic.cryptexsimple.model.entity.jpa.User;
import org.atonic.cryptexsimple.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/auth")
@AllArgsConstructor
public class AuthController {
    private final UserService userService;

    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> authenticateUser(@AuthenticationPrincipal Jwt jwt) {
        User user = userService.registerUser(jwt);
        return ResponseEntity.ok(AuthResponse.builder()
            .jwt(jwt)
            .email(user.getEmail())
            .build());
    }
}
