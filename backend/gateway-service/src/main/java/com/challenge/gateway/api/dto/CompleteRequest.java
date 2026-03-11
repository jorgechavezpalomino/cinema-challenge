package com.challenge.gateway.api.dto;

public record CompleteRequest(
        String email,
        String name,
        String documentNumber,
        String operationDate,
        String transactionId
) {
}
