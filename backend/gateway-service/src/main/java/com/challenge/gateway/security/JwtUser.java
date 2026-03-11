package com.challenge.gateway.security;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public record JwtUser(String email, String name, Collection<? extends GrantedAuthority> authorities) {
}
