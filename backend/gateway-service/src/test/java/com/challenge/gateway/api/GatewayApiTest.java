package com.challenge.gateway.api;

import com.challenge.gateway.api.dto.CompleteResponse;
import com.challenge.gateway.security.JwtService;
import com.challenge.gateway.service.CatalogGatewayService;
import com.challenge.gateway.service.CheckoutOrchestratorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class GatewayApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @MockBean
    private CatalogGatewayService catalogGatewayService;

    @MockBean
    private CheckoutOrchestratorService checkoutOrchestratorService;

    @Test
    void shouldReturnGuestToken() throws Exception {
        mockMvc.perform(post("/api/auth/guest"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.user.guest").value(true));
    }

    @Test
    void shouldProxyPremieresCatalog() throws Exception {
        when(catalogGatewayService.getPremieres()).thenReturn(List.of(Map.of(
                "id", 1,
                "title", "Mision Estelar",
                "description", "Desc",
                "imageUrl", "https://image"
        )));

        mockMvc.perform(get("/api/premieres"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Mision Estelar"));
    }

    @Test
    void shouldProxyCandystoreCatalog() throws Exception {
        when(catalogGatewayService.getCandystore()).thenReturn(List.of(Map.of(
                "id", 1,
                "name", "Combo Clasico",
                "description", "Desc",
                "price", 29.9
        )));

        mockMvc.perform(get("/api/candystore"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Combo Clasico"));
    }

    @Test
    void shouldCheckoutWhenAuthenticated() throws Exception {
        when(checkoutOrchestratorService.checkout(
                any(),
                nullable(String.class),
                nullable(String.class),
                nullable(String.class)
        ))
                .thenReturn(new CompleteResponse("0", "Compra registrada correctamente"));

        String token = jwtService.generateToken("user@test.com", "User", "CUSTOMER");

        mockMvc.perform(post("/api/checkout")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "cardNumber": "4907840000000005",
                                  "expiration": "05/30",
                                  "cvv": "777",
                                  "email": "user@test.com",
                                  "name": "APPROVED USER",
                                  "documentType": "DNI",
                                  "documentNumber": "12345678",
                                  "amount": 29.90,
                                  "items": []
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"));
    }
}
