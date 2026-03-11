package com.challenge.candystore.controller;

import com.challenge.candystore.model.CandyProduct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/candystore")
@CrossOrigin(origins = "*")
public class CandyController {

    private static final Logger log = LoggerFactory.getLogger(CandyController.class);

    @GetMapping
    public List<CandyProduct> getProducts() {
        log.info("Returning candy store catalog");
        return List.of(
                new CandyProduct(1L, "Combo Clasico", "Canchita grande y dos gaseosas.", BigDecimal.valueOf(29.90)),
                new CandyProduct(2L, "Nachos Picantes", "Nachos con queso y jalapenos.", BigDecimal.valueOf(18.50)),
                new CandyProduct(3L, "Duo Dulce", "Chocolate, gummy bears y bebida.", BigDecimal.valueOf(22.00)),
                new CandyProduct(4L, "Hot Dog Premium", "Pan brioche, salchicha jumbo y salsa especial.", BigDecimal.valueOf(16.90))
        );
    }
}
