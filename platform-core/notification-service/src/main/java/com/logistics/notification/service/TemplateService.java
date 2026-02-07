package com.logistics.notification.service;

import com.logistics.notification.model.NotificationTemplate;
import com.logistics.notification.repository.NotificationTemplateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Service for template management
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TemplateService {

    private final NotificationTemplateRepository templateRepository;

    /**
     * Create template
     */
    @Transactional
    public NotificationTemplate createTemplate(NotificationTemplate template) {
        template.setTemplateId("TPL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        return templateRepository.save(template);
    }

    /**
     * Get template by ID
     */
    public NotificationTemplate getTemplate(String templateId) {
        return templateRepository.findByTemplateId(templateId)
                .orElseThrow(() -> new RuntimeException("Template not found: " + templateId));
    }

    /**
     * Get all active templates
     */
    public List<NotificationTemplate> getActiveTemplates() {
        return templateRepository.findByActive(true);
    }

    /**
     * Render template with variables
     */
    public String renderTemplate(String templateBody, Map<String, String> variables) {
        String rendered = templateBody;
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            String placeholder = "{{" + entry.getKey() + "}}";
            rendered = rendered.replace(placeholder, entry.getValue());
        }
        return rendered;
    }

    /**
     * Update template
     */
    @Transactional
    public NotificationTemplate updateTemplate(String templateId, NotificationTemplate updates) {
        NotificationTemplate template = getTemplate(templateId);
        
        if (updates.getTemplateName() != null) {
            template.setTemplateName(updates.getTemplateName());
        }
        if (updates.getSubject() != null) {
            template.setSubject(updates.getSubject());
        }
        if (updates.getBody() != null) {
            template.setBody(updates.getBody());
        }
        if (updates.getActive() != null) {
            template.setActive(updates.getActive());
        }
        
        return templateRepository.save(template);
    }
}
