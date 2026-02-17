package com.logistics.platform.utils.audit;

import java.util.Objects;
import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logistics.platform.event.dto.AuditLogEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class AuditAspect {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${app.audit.topic:audit.events}")
    private String auditTopic;

    @Around("@annotation(auditable)")
    public Object auditMethod(ProceedingJoinPoint joinPoint, Auditable auditable) throws Throwable {
        Object result = null;
        boolean isSuccess = true;

        try {
            result = joinPoint.proceed();
            return result;
        } catch (Throwable e) {
            isSuccess = false;
            throw e;
        } finally {
            publishAuditEvent(joinPoint, auditable, result, isSuccess);
        }
    }

    private void publishAuditEvent(ProceedingJoinPoint joinPoint, Auditable auditable, Object result,
            boolean isSuccess) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String principalName = auth != null ? auth.getName() : "anonymous";

            AuditLogEvent event = AuditLogEvent.builder()
                    .action(auditable.action())
                    .entityType(auditable.entityType())
                    .changedBy(principalName)
                    .timestamp(LocalDateTime.now())
                    .build();

            kafkaTemplate.send(Objects.requireNonNull(auditTopic), event);
            log.debug("Published audit event: {}", event);

        } catch (Exception e) {
            log.error("Failed to publish audit event", e);
        }
    }
}
