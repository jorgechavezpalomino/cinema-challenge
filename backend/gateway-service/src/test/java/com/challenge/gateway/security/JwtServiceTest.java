package com.challenge.gateway.security;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceTest {

    private final JwtService jwtService = new JwtService("very-secret-demo-key-for-cinema-checkout-2026");

    @Test
    void shouldGenerateAndParseToken() {
        String token = jwtService.generateToken("user@test.com", "Cinema User", "CUSTOMER");

        JwtUser user = jwtService.parse(token);

        assertThat(user.email()).isEqualTo("user@test.com");
        assertThat(user.name()).isEqualTo("Cinema User");
        assertThat(user.authorities()).extracting("authority").contains("ROLE_CUSTOMER");
    }

    @Test
    void shouldRejectInvalidToken() {
        assertThatThrownBy(() -> jwtService.parse("invalid-token"))
                .isInstanceOf(RuntimeException.class);
    }
}
