package org.atonic.cryptexsimple.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.atonic.cryptexsimple.model.entity.jpa.ApiKey;
import org.atonic.cryptexsimple.model.repository.jpa.ApiKeyRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ApiKeyAuthFilter extends OncePerRequestFilter {
    private final ApiKeyRepository apiKeyRepository;
    private static final String API_KEY_HEADER = "X-API-Key";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String apiKey = request.getHeader(API_KEY_HEADER);

        if (apiKey == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            Optional<ApiKey> key = apiKeyRepository.findByKeyValueAndActiveIsTrue(UUID.fromString(apiKey));

            if (key.isEmpty() || (key.get().getExpiresAt() != null &&
                key.get().getExpiresAt().isBefore(LocalDateTime.now()))) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            ApiKey validKey = key.get();
            validKey.setUpdatedAt(LocalDateTime.now());
            apiKeyRepository.save(validKey);

            // Create authentication token with user authorities
            List<SimpleGrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("API_ACCESS"),
                new SimpleGrantedAuthority("USER")
            );

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                validKey.getUser().getId(),
                null,
                authorities
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        filterChain.doFilter(request, response);
    }
}
