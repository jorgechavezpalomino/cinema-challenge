package com.challenge.gateway.api;

import com.challenge.gateway.api.dto.CheckoutRequest;
import com.challenge.gateway.api.dto.CompleteResponse;
import com.challenge.gateway.service.CheckoutOrchestratorService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class CheckoutController {

    private final CheckoutOrchestratorService checkoutOrchestratorService;

    public CheckoutController(CheckoutOrchestratorService checkoutOrchestratorService) {
        this.checkoutOrchestratorService = checkoutOrchestratorService;
    }

    @PostMapping("/checkout")
    public CompleteResponse checkout(@RequestBody CheckoutRequest request, HttpServletRequest httpServletRequest) {
        return checkoutOrchestratorService.checkout(
                request,
                httpServletRequest.getRemoteAddr(),
                httpServletRequest.getHeader("User-Agent"),
                httpServletRequest.getHeader("Cookie")
        );
    }
}
