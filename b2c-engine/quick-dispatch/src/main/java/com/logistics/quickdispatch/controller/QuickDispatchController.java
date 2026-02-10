package com.logistics.quickdispatch.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.logistics.platform.common.dto.response.ApiResponse;

@RestController
@RequestMapping("/api/v1/quick-dispatch")
@RequiredArgsConstructor
public class QuickDispatchController {

    @PostMapping("/request")
    public ResponseEntity<ApiResponse<String>> requestDispatch(@RequestBody Object request) {
        // Scaffolding for quick dispatch logic
        return ResponseEntity.ok(ApiResponse.success("Dispatch request received"));
    }
}
