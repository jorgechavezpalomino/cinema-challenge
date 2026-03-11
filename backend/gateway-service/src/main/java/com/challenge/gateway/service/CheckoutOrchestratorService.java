package com.challenge.gateway.service;

import com.challenge.gateway.api.dto.CheckoutRequest;
import com.challenge.gateway.api.dto.CompleteRequest;
import com.challenge.gateway.api.dto.CompleteResponse;
import com.challenge.gateway.api.dto.PayuResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.client.RestClient;

@Service
public class CheckoutOrchestratorService {

    private static final Logger log = LoggerFactory.getLogger(CheckoutOrchestratorService.class);

    private final RestClient restClient;
    private final String completeUrl;
    private final PayuService payuService;

    public CheckoutOrchestratorService(
            RestClient restClient,
            @org.springframework.beans.factory.annotation.Value("${services.complete-url}") String completeUrl,
            PayuService payuService
    ) {
        this.restClient = restClient;
        this.completeUrl = completeUrl;
        this.payuService = payuService;
    }

    public CompleteResponse checkout(CheckoutRequest request, String ipAddress, String userAgent, String cookie) {
        PayuResponse payuResponse = payuService.submitTransaction(request, ipAddress, userAgent, cookie);
        if (payuResponse == null) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "PayU no respondio.");
        }
        if (!"SUCCESS".equalsIgnoreCase(payuResponse.code()) || payuResponse.transactionResponse() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, firstNonBlank(payuResponse.error(), "PayU devolvio un error."));
        }
        if (!"APPROVED".equalsIgnoreCase(payuResponse.transactionResponse().state())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    firstNonBlank(payuResponse.transactionResponse().responseCode(), "La transaccion fue rechazada por PayU.")
            );
        }
        log.info("Checkout approved by PayU for {}", request.email());
        return restClient.post()
                .uri(completeUrl)
                .body(new CompleteRequest(
                        request.email(),
                        request.name(),
                        request.documentNumber(),
                        payuResponse.transactionResponse().operationDate(),
                        payuResponse.transactionResponse().transactionId()
                ))
                .retrieve()
                .body(CompleteResponse.class);
    }

    private String firstNonBlank(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }
}
