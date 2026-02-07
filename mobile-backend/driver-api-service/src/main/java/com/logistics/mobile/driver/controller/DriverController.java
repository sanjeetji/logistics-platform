package com.logistics.mobile.driver.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/driver")
public class DriverController {

    private final MessageSource messageSource;

    @Autowired
    public DriverController(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @GetMapping("/welcome")
    public ResponseEntity<Map<String, String>> welcome() {
        String message = messageSource.getMessage("welcome.message", null, LocaleContextHolder.getLocale());

        Map<String, String> response = new HashMap<>();
        response.put("message", message);
        response.put("locale", LocaleContextHolder.getLocale().toString());

        return ResponseEntity.ok(response);
    }
}
