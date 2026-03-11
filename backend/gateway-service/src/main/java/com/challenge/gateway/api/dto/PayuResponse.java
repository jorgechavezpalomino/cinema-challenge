package com.challenge.gateway.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PayuResponse(
        String code,
        String error,
        TransactionResponse transactionResponse
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record TransactionResponse(
            String orderId,
            String transactionId,
            String state,
            String responseCode,
            String responseMessage,
            String operationDate,
            String authorizationCode,
            String paymentNetworkResponseErrorMessage
    ) {
    }
}
