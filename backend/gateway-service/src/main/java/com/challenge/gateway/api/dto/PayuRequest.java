package com.challenge.gateway.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public record PayuRequest(
        boolean test,
        String language,
        String command,
        Merchant merchant,
        Transaction transaction
) {
    public record Merchant(String apiKey, String apiLogin) {
    }

    public record Transaction(
            Order order,
            Payer payer,
            CreditCard creditCard,
            Map<String, Integer> extraParameters,
            String type,
            String paymentMethod,
            String paymentCountry,
            String deviceSessionId,
            String ipAddress,
            String cookie,
            String userAgent
    ) {
    }

    public record Order(
            String accountId,
            String referenceCode,
            String description,
            String language,
            String signature,
            @JsonProperty("additionalValues") Map<String, AmountValue> additionalValues,
            Buyer buyer
    ) {
    }

    public record AmountValue(String value, String currency) {
    }

    public record Buyer(
            String merchantBuyerId,
            String fullName,
            String emailAddress,
            String contactPhone,
            String dniNumber,
            Address shippingAddress
    ) {
    }

    public record Payer(
            String merchantPayerId,
            String fullName,
            String emailAddress,
            String contactPhone,
            String dniNumber,
            String dniType,
            Address billingAddress
    ) {
    }

    public record CreditCard(
            String number,
            String securityCode,
            String expirationDate,
            String name
    ) {
    }

    public record Address(
            String street1,
            String city,
            String state,
            String country,
            String postalCode,
            String phone
    ) {
    }
}
