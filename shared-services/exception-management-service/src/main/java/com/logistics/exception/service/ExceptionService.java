package com.logistics.exception.service;

import com.logistics.exception.model.ExceptionRecord;
import com.logistics.exception.repository.ExceptionRepository;
import com.logistics.platform.event.dto.BusinessExceptionEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExceptionService {

    private final ExceptionRepository exceptionRepository;

    @Transactional
    public ExceptionRecord saveException(BusinessExceptionEvent event) {
        log.info("Saving exception: {} from service: {}", event.getExceptionId(), event.getServiceName());

        ExceptionRecord record = ExceptionRecord.builder()
                .exceptionId(event.getExceptionId())
                .serviceName(event.getServiceName())
                .exceptionType(event.getExceptionType())
                .message(event.getMessage())
                .severity(event.getSeverity())
                .timestamp(event.getTimestamp())
                .metadata(event.getMetadata())
                .status(ExceptionRecord.ExceptionStatus.OPEN)
                .build();

        return exceptionRepository.save(record);
    }

    @Transactional
    public ExceptionRecord resolveException(String id, String resolvedBy, String notes) {
        Optional<ExceptionRecord> optionalRecord = exceptionRepository.findById(id);
        if (optionalRecord.isPresent()) {
            ExceptionRecord record = optionalRecord.get();
            record.setStatus(ExceptionRecord.ExceptionStatus.RESOLVED);
            record.setResolvedBy(resolvedBy);
            record.setResolutionNotes(notes);
            record.setResolvedAt(LocalDateTime.now());
            return exceptionRepository.save(record);
        } else {
            throw new RuntimeException("Exception record not found with ID: " + id);
        }
    }

    public List<ExceptionRecord> getAllExceptions() {
        return exceptionRepository.findAll();
    }

    public List<ExceptionRecord> getExceptionsByStatus(ExceptionRecord.ExceptionStatus status) {
        return exceptionRepository.findByStatus(status);
    }

    public List<ExceptionRecord> getExceptionsBySeverity(String severity) {
        return exceptionRepository.findBySeverity(severity);
    }

    public List<ExceptionRecord> getExceptionsByService(String serviceName) {
        return exceptionRepository.findByServiceName(serviceName);
    }
}
