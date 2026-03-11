package com.challenge.gateway.service;

import com.challenge.gateway.api.dto.CheckoutRequest;
import com.challenge.gateway.api.dto.CompleteResponse;
import com.challenge.gateway.api.dto.PayuResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class CheckoutOrchestratorServiceTest {

    private MockRestServiceServer mockServer;
    private PayuService payuService;
    private CheckoutOrchestratorService orchestratorService;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder();
        mockServer = MockRestServiceServer.bindTo(builder).build();
        payuService = mock(PayuService.class);
        orchestratorService = new CheckoutOrchestratorService(builder.build(), "http://complete.local/api/complete", payuService);
    }

    @Test
    void shouldReturnSuccessWhenPayuApproves() {
        when(payuService.submitTransaction(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any()))
                .thenReturn(new PayuResponse(
                        "SUCCESS",
                        null,
                        new PayuResponse.TransactionResponse("1", "tx-1", "APPROVED", "APPROVED", "Approved", "1773206519325", "AUTH-1", null)
                ));

        mockServer.expect(requestTo("http://complete.local/api/complete"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("""
                        {"code":"0","message":"Compra registrada correctamente"}
                        """, MediaType.APPLICATION_JSON));

        CompleteResponse response = orchestratorService.checkout(sampleCheckoutRequest(), "127.0.0.1", "Mozilla", "cookie");

        assertThat(response.code()).isEqualTo("0");
        mockServer.verify();
    }

    @Test
    void shouldRejectWhenPayuReturnsRejectedState() {
        when(payuService.submitTransaction(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any()))
                .thenReturn(new PayuResponse(
                        "SUCCESS",
                        null,
                        new PayuResponse.TransactionResponse("1", "tx-2", "DECLINED", "DECLINED_TEST_MODE", "Rejected", "1773206519325", null, null)
                ));

        assertThatThrownBy(() -> orchestratorService.checkout(sampleCheckoutRequest(), "127.0.0.1", "Mozilla", "cookie"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("DECLINED_TEST_MODE");
    }

    @Test
    void shouldFailWhenPayuResponseIsIncomplete() {
        when(payuService.submitTransaction(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any()))
                .thenReturn(new PayuResponse("ERROR", "Gateway timeout", null));

        assertThatThrownBy(() -> orchestratorService.checkout(sampleCheckoutRequest(), "127.0.0.1", "Mozilla", "cookie"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Gateway timeout");
    }

    private CheckoutRequest sampleCheckoutRequest() {
        return new CheckoutRequest(
                "4907840000000005",
                "05/30",
                "777",
                "user@test.com",
                "APPROVED USER",
                "DNI",
                "12345678",
                BigDecimal.valueOf(29.90),
                List.of()
        );
    }
}
