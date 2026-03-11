package com.challenge.candystore.model;

import java.math.BigDecimal;

public record CandyProduct(Long id, String name, String description, BigDecimal price) {
}
