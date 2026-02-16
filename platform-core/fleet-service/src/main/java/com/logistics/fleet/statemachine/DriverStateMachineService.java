package com.logistics.fleet.statemachine;

import com.logistics.fleet.model.Driver;
import com.logistics.fleet.model.DriverStateMachineContext;
import com.logistics.fleet.model.DriverStatus;
import com.logistics.fleet.repository.DriverRepository;
import com.logistics.fleet.repository.DriverStateMachineContextRepository;
import com.logistics.platform.common.exceptions.types.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DriverStateMachineService {

    private final StateMachineFactory<DriverState, DriverEvent> stateMachineFactory;
    private final DriverStateMachineContextRepository contextRepository;
    private final DriverRepository driverRepository;

    @Transactional
    public boolean transitionDriver(String driverEmail, DriverEvent event, String reason) {
        try {
            Driver driver = driverRepository.findByEmail(driverEmail)
                    .orElseThrow(() -> new ResourceNotFoundException("Driver not found: " + driverEmail));

            StateMachine<DriverState, DriverEvent> stateMachine = getOrCreateStateMachine(driverEmail);
            stateMachine.getExtendedState().getVariables().put("driver", driver);
            stateMachine.getExtendedState().getVariables().put("reason", reason);

            stateMachine.startReactively().block();

            Message<DriverEvent> message = MessageBuilder
                    .withPayload(event)
                    .setHeader("driverEmail", driverEmail)
                    .setHeader("reason", reason)
                    .build();

            boolean result = stateMachine.sendEvent(message);

            if (result) {
                DriverState newState = stateMachine.getState().getId();
                driver.setStatus(DriverStatus.valueOf(newState.name()));
                driverRepository.save(driver);

                persistStateMachine(driverEmail, stateMachine, event);
                log.info("Driver {} transitioned to {} via event {}", driverEmail, newState, event);
            } else {
                log.warn("State transition rejected for driver {} with event {}", driverEmail, event);
            }

            return result;
        } catch (Exception e) {
            log.error("Error transitioning driver state", e);
            return false;
        }
    }

    private StateMachine<DriverState, DriverEvent> getOrCreateStateMachine(String driverEmail) {
        Optional<DriverStateMachineContext> contextOpt = contextRepository.findByDriverEmail(driverEmail);
        StateMachine<DriverState, DriverEvent> stateMachine = stateMachineFactory.getStateMachine(driverEmail);

        if (contextOpt.isPresent()) {
            DriverStateMachineContext context = contextOpt.get();
            DriverState currentState = DriverState.valueOf(context.getCurrentState());

            stateMachine.getStateMachineAccessor()
                    .doWithAllRegions(access -> {
                        access.resetStateMachine(
                                new org.springframework.statemachine.support.DefaultStateMachineContext<>(
                                        currentState, null, null, null));
                    });
        }

        return stateMachine;
    }

    private void persistStateMachine(String driverEmail, StateMachine<DriverState, DriverEvent> stateMachine,
            DriverEvent event) {
        DriverState currentState = stateMachine.getState().getId();
        DriverStateMachineContext context = contextRepository.findByDriverEmail(driverEmail)
                .orElse(DriverStateMachineContext.builder()
                        .driverEmail(driverEmail)
                        .build());

        context.setCurrentState(currentState.name());
        context.setLastTransitionTime(LocalDateTime.now());
        context.setLastEvent(event.name());

        contextRepository.save(context);
    }
}
