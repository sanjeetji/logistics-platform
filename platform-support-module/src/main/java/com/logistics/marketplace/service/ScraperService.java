package com.logistics.marketplace.service;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class ScraperService {

    // Mock User Agent to avoid being blocked immediately
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36";

    /**
     * Scrapes a generic e-commerce product page.
     * Note: Selectors (.price, h1) are generic and will likely need per-site strategy.
     */
    public Map<String, Object> scrapeProductData(String url) {
        Map<String, Object> data = new HashMap<>();
        data.put("url", url);
        
        try {
            log.info("Scraping URL: {}", url);
            Document doc = Jsoup.connect(url)
                    .userAgent(USER_AGENT)
                    .timeout(5000)
                    .get();

            // Try to find title
            String title = doc.title();
            // Try common h1
            Element h1 = doc.selectFirst("h1");
            if (h1 != null) {
                title = h1.text();
            }
            data.put("title", title);

            // Try to find price (Very brittle, heuristic based)
            // Look for elements with class/id containing 'price'
            Element priceEl = doc.selectFirst(".price, #price, [itemprop=price]");
            if (priceEl != null) {
                data.put("price_raw", priceEl.text());
                // Basic cleanup?
                data.put("price_parsed", parsePrice(priceEl.text()));
            } else {
                data.put("price_raw", "Not found");
            }
            
            data.put("status", "SUCCESS");

        } catch (IOException e) {
            log.error("Failed to scrape URL: {}", url, e);
            data.put("status", "FAILED");
            data.put("error", e.getMessage());
        }

        return data;
    }

    private BigDecimal parsePrice(String text) {
        try {
            // Remove non-numeric except dot/comma using Regex
            String clean = text.replaceAll("[^0-9.,]", "");
            return new BigDecimal(clean);
        } catch (Exception e) {
            return null; // Silent fail on parsing
        }
    }
}
