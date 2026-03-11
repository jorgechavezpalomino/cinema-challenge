package com.challenge.gateway.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
public class CatalogGatewayService {

    private final RestClient restClient;
    private final String premieresUrl;
    private final String candystoreUrl;

    public CatalogGatewayService(
            RestClient restClient,
            @Value("${services.premieres-url}") String premieresUrl,
            @Value("${services.candystore-url}") String candystoreUrl
    ) {
        this.restClient = restClient;
        this.premieresUrl = premieresUrl;
        this.candystoreUrl = candystoreUrl;
    }

    public List<Map<String, Object>> getPremieres() {
        return restClient.get().uri(premieresUrl).retrieve().body(new ParameterizedTypeReference<>() {});
    }

    public List<Map<String, Object>> getCandystore() {
        return restClient.get().uri(candystoreUrl).retrieve().body(new ParameterizedTypeReference<>() {});
    }
}
