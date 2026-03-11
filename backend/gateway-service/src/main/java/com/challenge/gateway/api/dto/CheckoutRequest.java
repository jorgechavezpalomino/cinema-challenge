package com.challenge.gateway.api.dto;

import java.math.BigDecimal;
import java.util.List;

public record CheckoutRequest(
        String cardNumber,
        String expiration,
        String cvv,
        String email,
        String name,
        String documentType,
        String documentNumber,
        BigDecimal amount,
        List<CartItemRequest> items
) {
}
