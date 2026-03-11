package com.challenge.gateway.api;

import com.challenge.gateway.service.CatalogGatewayService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class CatalogController {

    private final CatalogGatewayService catalogGatewayService;

    public CatalogController(CatalogGatewayService catalogGatewayService) {
        this.catalogGatewayService = catalogGatewayService;
    }

    @GetMapping("/premieres")
    public Object premieres() {
        return catalogGatewayService.getPremieres();
    }

    @GetMapping("/candystore")
    public Object candystore() {
        return catalogGatewayService.getCandystore();
    }
}
