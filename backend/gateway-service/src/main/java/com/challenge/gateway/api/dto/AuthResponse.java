package com.challenge.gateway.api.dto;

public record AuthResponse(String token, UserSummary user) {
}
