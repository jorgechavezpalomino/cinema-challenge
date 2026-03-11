package com.challenge.complete.api;

import com.challenge.complete.api.dto.CompleteRequest;
import com.challenge.complete.api.dto.CompleteResponse;
import com.challenge.complete.service.CompleteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class CompleteController {

    private static final Logger log = LoggerFactory.getLogger(CompleteController.class);
    private final CompleteService completeService;

    public CompleteController(CompleteService completeService) {
        this.completeService = completeService;
    }

    @PostMapping("/complete")
    public CompleteResponse complete(@RequestBody CompleteRequest request) {
        log.info("Completing purchase for transaction {}", request.transactionId());
        return completeService.completePurchase(request);
    }
}
