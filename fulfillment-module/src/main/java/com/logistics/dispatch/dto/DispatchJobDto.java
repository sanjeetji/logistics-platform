package com.logistics.dispatch.dto;

import com.logistics.dispatch.model.DispatchStatus;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class DispatchJobDto {
    private Long id;
    private String orderId;
    private String matchedDriverId;
    private DispatchStatus status;
    private Integer attempts;
    private String lastErrorMessage;
    private LocalDateTime nextRetryAt;
}
