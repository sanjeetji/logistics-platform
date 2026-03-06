package com.logistics.tenant.exception;

import com.logistics.platform.common.dto.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global exception handler for the Feature Control Panel.
 *
 * Error Response Format (standard ApiResponse):
 * {
 * "success": false,
 * "message": "Feature 'ROUTE_OPTIMIZATION' is not enabled for your account.",
 * "data": null,
 * "errorCode": "FEATURE_DISABLED",
 * "featureKey": "ROUTE_OPTIMIZATION",
 * "timestamp": "2026-02-23T12:00:00"
 * }
 */
@RestControllerAdvice
public class FeatureControlPanelExceptionHandler {

        /**
         * HTTP 403 — Feature is disabled for this tenant.
         * Returned when a tenant calls an API for a feature SUPER_ADMIN has not
         * enabled.
         */
        @ExceptionHandler(FeatureNotEnabledException.class)
        public ResponseEntity<ApiResponse<FeatureErrorDetail>> handleFeatureNotEnabled(
                        FeatureNotEnabledException ex) {

                FeatureErrorDetail detail = new FeatureErrorDetail(
                                ex.getFeatureKey(),
                                ex.getTenantId(),
                                "If you believe this is an error, please contact support.");

                ApiResponse<FeatureErrorDetail> response = ApiResponse.<FeatureErrorDetail>builder()
                                .success(false)
                                .message("Feature '" + ex.getFeatureKey() + "' is not available for your account.")
                                .data(detail)
                                .errorCode("FEATURE_DISABLED")
                                .build();

                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        /**
         * Detail payload included in FEATURE_DISABLED responses.
         * Gives the frontend enough info to show a proper upgrade/contact dialog.
         */
        public record FeatureErrorDetail(
                        String featureKey,
                        Long tenantId,
                        String action) {
        }
}
