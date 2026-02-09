package com.logistics.platform.utils.audit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logistics.platform.common.dto.event.AuditLogEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class AuditAspect {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${audit.kafka.topic:audit-logs}")
    private String auditTopic;

    @Around("@annotation(auditable)")
    public Object auditMethod(ProceedingJoinPoint joinPoint, Auditable auditable) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = null;
        String exceptionMessage = null;
        boolean isSuccess = true;

        // Capture initial state (arguments)
        String oldValue = "";
        try {
            if (joinPoint.getArgs().length > 0) {
                oldValue = objectMapper.writeValueAsString(joinPoint.getArgs());
            }
        } catch (Exception e) {
            log.warn("Failed to serialize arguments for audit log", e);
            oldValue = "Error serializing args";
        }

        try {
            result = joinPoint.proceed();
            return result;
        } catch (Throwable e) {
            isSuccess = false;
            exceptionMessage = e.getMessage();
            throw e;
        } finally {
            // Async publish to Kafka to not block main thread
            publishAuditLog(joinPoint, auditable, oldValue, result, isSuccess, exceptionMessage);
        }
    }

    private void publishAuditLog(ProceedingJoinPoint joinPoint, Auditable auditable, String oldValue, Object result,
            boolean isSuccess, String exceptionMessage) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        CompletableFuture.runAsync(() -> {
            try {
                String newValue = "";
                if (result != null) {
                    try {
                        newValue = objectMapper.writeValueAsString(result);
                    } catch (Exception e) {
                        newValue = "Error serializing result";
                    }
                } else if (!isSuccess) {
                    newValue = "Exception: " + exceptionMessage;
                }

                String currentUsername = "system";
                String tenantId = "default";

                if (authentication != null && authentication.isAuthenticated()) {
                    currentUsername = authentication.getName();
                    // Ideally extract tenant from details or context
                }

                AuditLogEvent event = AuditLogEvent.builder()
                        .entityId("N/A") // Difficult to flush out generic entity ID without specific return type
                                         // knowledge
                        .entityType(StringUtils.hasText(auditable.entityType()) ? auditable.entityType()
                                : joinPoint.getSignature().getDeclaringType().getSimpleName())
                        .action(StringUtils.hasText(auditable.action()) ? auditable.action()
                                : joinPoint.getSignature().getName())
                        .changedBy(currentUsername)
                        .tenantId(tenantId)
                        .oldValue(oldValue)
                        .newValue(newValue)
                        .timestamp(LocalDateTime.now())
                        .build();

                kafkaTemplate.send(auditTopic, event);
                log.debug("Published audit event: {}", event);

            } catch (Exception e) {
                log.error("Failed to publish audit log", e);
            }
        });
    }
}
