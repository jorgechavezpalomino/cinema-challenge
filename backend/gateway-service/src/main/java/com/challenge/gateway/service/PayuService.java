package com.challenge.gateway.service;

import com.challenge.gateway.api.dto.CheckoutRequest;
import com.challenge.gateway.api.dto.PayuRequest;
import com.challenge.gateway.api.dto.PayuResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

@Service
public class PayuService {

    private final RestClient restClient;
    private final String paymentsUrl;
    private final String apiLogin;
    private final String apiKey;
    private final String merchantId;
    private final String accountId;
    private final String currency;
    private final String country;
    private final String language;
    private final boolean test;

    public PayuService(
            RestClient restClient,
            @Value("${payu.payments-url}") String paymentsUrl,
            @Value("${payu.api-login}") String apiLogin,
            @Value("${payu.api-key}") String apiKey,
            @Value("${payu.merchant-id}") String merchantId,
            @Value("${payu.account-id}") String accountId,
            @Value("${payu.currency}") String currency,
            @Value("${payu.country}") String country,
            @Value("${payu.language}") String language,
            @Value("${payu.test}") boolean test
    ) {
        this.restClient = restClient;
        this.paymentsUrl = paymentsUrl;
        this.apiLogin = apiLogin;
        this.apiKey = apiKey;
        this.merchantId = merchantId;
        this.accountId = accountId;
        this.currency = currency;
        this.country = country;
        this.language = language;
        this.test = test;
    }

    public PayuResponse submitTransaction(CheckoutRequest request, String ipAddress, String userAgent, String cookie) {
        String referenceCode = "CINEMA-" + UUID.randomUUID();
        String amount = normalizeAmount(request.amount());
        String signatureSeed = String.join("~", apiKey, merchantId, referenceCode, amount, currency);
        String signature = DigestUtils.md5DigestAsHex(signatureSeed.getBytes(StandardCharsets.UTF_8));
        String documentType = mapDocumentType(request.documentType());
        String paymentMethod = inferPaymentMethod(request.cardNumber());
        String expirationDate = normalizeExpiration(request.expiration());
        String sessionId = UUID.randomUUID().toString().replace("-", "");
        String cardholderName = request.name().trim();
        PayuRequest.Address address = new PayuRequest.Address(
                "Av. Javier Prado Este 4200",
                "Lima",
                "Lima y Callao",
                country,
                "15023",
                "999999999"
        );

        PayuRequest payuRequest = new PayuRequest(
                test,
                language,
                "SUBMIT_TRANSACTION",
                new PayuRequest.Merchant(apiKey, apiLogin),
                new PayuRequest.Transaction(
                        new PayuRequest.Order(
                                accountId,
                                referenceCode,
                                "Cinema checkout " + cardholderName,
                                language,
                                signature,
                                Map.of("TX_VALUE", new PayuRequest.AmountValue(amount, currency)),
                                new PayuRequest.Buyer(
                                        request.documentNumber(),
                                        cardholderName,
                                        request.email(),
                                        "999999999",
                                        request.documentNumber(),
                                        address
                                )
                        ),
                        new PayuRequest.Payer(
                                request.documentNumber(),
                                cardholderName,
                                request.email(),
                                "999999999",
                                request.documentNumber(),
                                documentType,
                                address
                        ),
                        new PayuRequest.CreditCard(
                                request.cardNumber(),
                                request.cvv(),
                                expirationDate,
                                cardholderName
                        ),
                        Map.of("INSTALLMENTS_NUMBER", 1),
                        "AUTHORIZATION_AND_CAPTURE",
                        paymentMethod,
                        country,
                        sessionId,
                        blankToFallback(ipAddress, "127.0.0.1"),
                        blankToFallback(cookie, "cinema-session"),
                        blankToFallback(userAgent, "Mozilla/5.0")
                )
        );

        return restClient.post()
                .uri(paymentsUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .body(payuRequest)
                .retrieve()
                .body(PayuResponse.class);
    }

    private String normalizeAmount(BigDecimal amount) {
        return amount.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    private String normalizeExpiration(String expiration) {
        String[] parts = expiration.split("/");
        if (parts.length != 2) {
            throw new IllegalArgumentException("La fecha de expiracion debe usar el formato MM/YY.");
        }
        String month = parts[0].trim();
        String year = parts[1].trim();
        String fullYear = year.length() == 2 ? "20" + year : year;
        return fullYear + "/" + month;
    }

    private String mapDocumentType(String documentType) {
        return switch (documentType) {
            case "CE" -> "CE";
            case "PASAPORTE" -> "PP";
            default -> "DNI";
        };
    }

    private String inferPaymentMethod(String cardNumber) {
        if (cardNumber.startsWith("34") || cardNumber.startsWith("37")) {
            return "AMEX";
        }
        if (cardNumber.startsWith("36") || cardNumber.startsWith("30") || cardNumber.startsWith("38")) {
            return "DINERS";
        }
        if (cardNumber.startsWith("5") || cardNumber.matches("^2[2-7].*")) {
            return "MASTERCARD";
        }
        return "VISA";
    }

    private String blankToFallback(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }
}
