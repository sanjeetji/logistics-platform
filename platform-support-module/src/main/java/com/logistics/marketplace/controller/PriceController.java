package com.logistics.marketplace.controller;

import com.logistics.marketplace.service.ScraperService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/marketplace")
@RequiredArgsConstructor
public class PriceController {

    private final ScraperService scraperService;

    @GetMapping("/scrape")
    public ResponseEntity<Map<String, Object>> scrapeProduct(@RequestParam String url) {
        return ResponseEntity.ok(scraperService.scrapeProductData(url));
    }
}
