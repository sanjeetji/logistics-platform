package com.logistics.platform.utils.audit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
@ConditionalOnClass(KafkaTemplate.class)
public class AuditAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public AuditAspect auditAspect(KafkaTemplate<String, Object> kafkaTemplate, ObjectMapper objectMapper) {
        return new AuditAspect(kafkaTemplate, objectMapper);
    }
}
