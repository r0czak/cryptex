package org.atonic.cryptexsimple.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


@RestController
@RequestMapping("/api/v1")
public class HomeController {
    @GetMapping("/public")
    public ResponseEntity<Map<String, String>> publicEndpoint() {
        return ResponseEntity.ok(Map.of("message", "Called public API"));
    }

    @PreAuthorize("hasAnyAuthority('USER')")
    @GetMapping("/private")
    public ResponseEntity<Map<String, String>> privateEndpoint(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(Map.of("message", "Called private user API"));
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping("/admin")
    public ResponseEntity<Map<String, String>> adminEndpoint(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(Map.of("message", "Called Admin API"));
    }
}
