package com.logistics.dispatch.service;

import com.logistics.dispatch.model.DispatchJob;
import com.logistics.dispatch.model.DispatchStatus;
import com.logistics.platform.common.dto.order.TransportOrderDto;
import com.logistics.dispatch.strategy.DispatchStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class DispatchJobProcessor {

    private final Map<String, DispatchStrategy> dispatchStrategies;
    private final com.logistics.dispatch.repository.DispatchJobRepository jobRepository;
    private final DispatchService dispatchService;
    private final com.logistics.dispatch.event.DispatchEventProducer eventProducer;

    public DispatchJobProcessor(
            Map<String, DispatchStrategy> dispatchStrategies,
            com.logistics.dispatch.repository.DispatchJobRepository jobRepository,
            @org.springframework.context.annotation.Lazy DispatchService dispatchService,
            com.logistics.dispatch.event.DispatchEventProducer eventProducer) {
        this.dispatchStrategies = dispatchStrategies;
        this.jobRepository = jobRepository;
        this.dispatchService = dispatchService;
        this.eventProducer = eventProducer;
    }

    @Async("dispatchExecutor")
    public void processAssignmentAsync(TransportOrderDto orderDto, DispatchJob job, String strategyName) {
        log.info("Starting async dispatch processing for order: {} using strategy: {}", orderDto.getOrderId(),
                strategyName);

        try {
            DispatchStrategy strategy = dispatchStrategies.get(strategyName);
            if (strategy == null) {
                log.warn("Strategy {} not found, defaulting to STANDARD_DISPATCH", strategyName);
                strategy = dispatchStrategies.get("STANDARD_DISPATCH");
            }

            job.setStatus(DispatchStatus.SEARCHING);
            jobRepository.save(job);

            boolean success = strategy.dispatch(orderDto, job);

            if (success && job.getMatchedDriverId() != null) {
                log.info("Driver matched for order {}: {}. Proceeding with assignment.", orderDto.getOrderId(),
                        job.getMatchedDriverId());
                dispatchService.assignOrderToDriver(orderDto.getOrderId(), Long.parseLong(job.getMatchedDriverId()),
                        null);

                // Publish success event
                eventProducer.publishAssignmentSuccess(orderDto.getOrderId(), job.getMatchedDriverId());
            } else {
                log.warn("No driver matched for order {} during async processing", orderDto.getOrderId());
                job.setStatus(DispatchStatus.FAILED);
                job.setLastErrorMessage("No candidates found or scoring failed");
                jobRepository.save(job);

                // Publish failure event
                eventProducer.publishAssignmentFailure(orderDto.getOrderId(), job.getLastErrorMessage());
            }

        } catch (Exception e) {
            log.error("Error in async dispatch processing for order {}", orderDto.getOrderId(), e);
            job.setLastErrorMessage(e.getMessage());
            job.setStatus(DispatchStatus.FAILED);
            jobRepository.save(job);
        }
    }
}
