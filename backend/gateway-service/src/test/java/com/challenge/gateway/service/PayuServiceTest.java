package com.challenge.gateway.service;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PayuServiceTest {

    private final PayuService payuService = new PayuService(
            RestClient.builder().build(),
            "https://sandbox.api.payulatam.com/payments-api/4.0/service.cgi",
            "login",
            "key",
            "merchant",
            "account",
            "PEN",
            "PE",
            "es",
            true
    );

    @Test
    void shouldInferVisaPaymentMethod() {
        String paymentMethod = ReflectionTestUtils.invokeMethod(payuService, "inferPaymentMethod", "4907840000000005");

        assertThat(paymentMethod).isEqualTo("VISA");
    }

    @Test
    void shouldInferMastercardPaymentMethod() {
        String paymentMethod = ReflectionTestUtils.invokeMethod(payuService, "inferPaymentMethod", "5491610000000001");

        assertThat(paymentMethod).isEqualTo("MASTERCARD");
    }

    @Test
    void shouldNormalizeExpirationDate() {
        String expiration = ReflectionTestUtils.invokeMethod(payuService, "normalizeExpiration", "05/30");

        assertThat(expiration).isEqualTo("2030/05");
    }

    @Test
    void shouldFailWhenExpirationDateIsInvalid() {
        assertThatThrownBy(() -> ReflectionTestUtils.invokeMethod(payuService, "normalizeExpiration", "0530"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("MM/YY");
    }
}
