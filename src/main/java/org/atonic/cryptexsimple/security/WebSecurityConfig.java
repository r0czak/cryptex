package org.atonic.cryptexsimple.security;

import org.atonic.cryptexsimple.security.filter.ApiKeyAuthFilter;
import org.atonic.cryptexsimple.security.jwt.JwtConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.SecurityContextHolderFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
public class WebSecurityConfig {
    private final JwtConfig jwtConfig;

    @Value("${dev.auth0-id1}")
    private String auth0Id;

    @Value("${dev.email1}")
    private String email;

    public WebSecurityConfig(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
    }

    @Bean
    @Profile("prod")
    public SecurityFilterChain prodFilterChain(HttpSecurity http,
                                               ApiKeyAuthFilter apiKeyAuthFilter) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/api/v1/public").permitAll()
                .requestMatchers("/api/v1/**").hasAnyAuthority("USER", "ADMIN")
                .requestMatchers("/api/v2/**").hasAnyAuthority("API_ACCESS")
                .anyRequest().authenticated()
            )
            .cors(Customizer.withDefaults())
            .addFilterBefore(apiKeyAuthFilter, SecurityContextHolderFilter.class)
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt
                    .decoder(jwtConfig.jwtDecoder())
                    .jwtAuthenticationConverter(jwtConfig.jwtAuthenticationConverter()))
            );

        return http.build();
    }


    @Bean
    @Profile("dev")
    public SecurityFilterChain devFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(authorize -> authorize
                .anyRequest().permitAll()
            )
            .cors(Customizer.withDefaults())
            .addFilterAfter((request, response, chain) -> {
                Jwt jwt = Jwt.withTokenValue("token")
                    .header("alg", "RS256")
                    .claim("sub", auth0Id)
                    .claim("email", email)
                    .claim("permissions", List.of(
                        "read:orders",
                        "read:users",
                        "write:admin_role",
                        "write:orders",
                        "write:users"))
                    .build();

                List<SimpleGrantedAuthority> authorities = List.of(
                    new SimpleGrantedAuthority("USER"),
                    new SimpleGrantedAuthority("ADMIN"),
                    new SimpleGrantedAuthority("API_ACCESS")
                );

                JwtAuthenticationToken authentication = new JwtAuthenticationToken(jwt, authorities);

                SecurityContextHolder.getContext().setAuthentication(authentication);

                chain.doFilter(request, response);
            }, SecurityContextHolderFilter.class);

        return http.build();
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList("http://localhost:4040"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
        config.setAllowedHeaders(Arrays.asList("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }


}
