package com.challenge.complete.service;

import com.challenge.complete.api.dto.CompleteRequest;
import com.challenge.complete.api.dto.CompleteResponse;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CompleteService {

    private final JdbcTemplate jdbcTemplate;

    public CompleteService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public CompleteResponse completePurchase(CompleteRequest request) {
        Map<String, Object> row = jdbcTemplate.queryForMap(
                "CALL sp_complete_purchase(?, ?, ?, ?, ?)",
                request.email(),
                request.name(),
                request.documentNumber(),
                request.operationDate(),
                request.transactionId()
        );
        return new CompleteResponse(String.valueOf(row.get("code")), String.valueOf(row.get("message")));
    }
}
