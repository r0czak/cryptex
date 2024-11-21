package org.atonic.cryptexsimple.controller.payload.response;

import lombok.Builder;
import lombok.Data;
import org.springframework.security.oauth2.jwt.Jwt;

@Data
@Builder
public class AuthResponse {
    Jwt jwt;
    String email;
}
