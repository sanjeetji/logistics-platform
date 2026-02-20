package com.logistics.b2b.repository;

import com.logistics.b2b.model.RecurringOrderTemplate;
import com.logistics.b2b.model.RecurringFrequency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecurringOrderTemplateRepository extends JpaRepository<RecurringOrderTemplate, Long> {
    
    Optional<RecurringOrderTemplate> findByTemplateId(String templateId);
    
    List<RecurringOrderTemplate> findByClientId(Long clientId);
    
    List<RecurringOrderTemplate> findByActive(Boolean active);
    
    List<RecurringOrderTemplate> findByFrequency(RecurringFrequency frequency);
}
