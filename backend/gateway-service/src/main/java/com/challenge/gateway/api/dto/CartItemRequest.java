package com.challenge.gateway.api.dto;

import java.math.BigDecimal;

public record CartItemRequest(Long id, String name, BigDecimal price, Integer quantity) {
}
