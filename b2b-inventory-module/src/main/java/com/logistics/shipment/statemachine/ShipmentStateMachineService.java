package com.logistics.shipment.statemachine;

import com.logistics.platform.common.exceptions.types.ResourceNotFoundException;
import com.logistics.shipment.model.Shipment;
import com.logistics.shipment.model.ShipmentStatus;
import com.logistics.shipment.model.ShipmentStateMachineContext;
import com.logistics.shipment.repository.ShipmentRepository;
import com.logistics.shipment.repository.ShipmentStateMachineContextRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
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
public class ShipmentStateMachineService {

    @org.springframework.beans.factory.annotation.Qualifier("shipmentStateMachineFactory")
    private final StateMachineFactory<ShipmentState, ShipmentEvent> stateMachineFactory;
    private final ShipmentStateMachineContextRepository contextRepository;
    private final ShipmentRepository shipmentRepository;

    @Transactional
    public boolean transitionShipment(String shipmentId, ShipmentEvent event, String reason) {
        try {
            Shipment shipment = shipmentRepository.findByShipmentId(shipmentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Shipment not found: " + shipmentId));

            StateMachine<ShipmentState, ShipmentEvent> stateMachine = getOrCreateStateMachine(shipmentId);
            stateMachine.getExtendedState().getVariables().put("shipment", shipment);
            stateMachine.getExtendedState().getVariables().put("reason", reason);

            stateMachine.startReactively().block();

            Message<ShipmentEvent> message = MessageBuilder
                    .withPayload(event)
                    .setHeader("shipmentId", shipmentId)
                    .setHeader("reason", reason)
                    .build();

            // Using modern sendEvent which returns a Flux/Mono
            boolean result = stateMachine.sendEvent(Mono.just(message)).blockLast().getRegion().getState() != null; // Simplified
                                                                                                                    // check
                                                                                                                    // for
                                                                                                                    // result

            if (result) {
                ShipmentState newState = stateMachine.getState().getId();
                shipment.setStatus(ShipmentStatus.valueOf(newState.name()));
                shipmentRepository.save(shipment);

                persistStateMachine(shipmentId, stateMachine, event);
                log.info("Shipment {} transitioned to {} via event {}", shipmentId, newState, event);
            } else {
                log.warn("State transition rejected for shipment {} with event {}", shipmentId, event);
            }

            return result;
        } catch (Exception e) {
            log.error("Error transitioning shipment state", e);
            return false;
        }
    }

    private StateMachine<ShipmentState, ShipmentEvent> getOrCreateStateMachine(String shipmentId) {
        Optional<ShipmentStateMachineContext> contextOpt = contextRepository.findByShipmentId(shipmentId);
        StateMachine<ShipmentState, ShipmentEvent> stateMachine = stateMachineFactory.getStateMachine(shipmentId);

        if (contextOpt.isPresent()) {
            ShipmentStateMachineContext context = contextOpt.get();
            ShipmentState currentState = ShipmentState.valueOf(context.getCurrentState());

            stateMachine.getStateMachineAccessor()
                    .doWithAllRegions(access -> {
                        access.resetStateMachineReactively(
                                new org.springframework.statemachine.support.DefaultStateMachineContext<>(
                                        currentState, null, null, null))
                                .block();
                    });
        }

        return stateMachine;
    }

    private void persistStateMachine(String shipmentId, StateMachine<ShipmentState, ShipmentEvent> stateMachine,
            ShipmentEvent event) {
        ShipmentState currentState = stateMachine.getState().getId();
        ShipmentStateMachineContext context = contextRepository.findByShipmentId(shipmentId)
                .orElse(ShipmentStateMachineContext.builder()
                        .shipmentId(shipmentId)
                        .build());

        context.setCurrentState(currentState.name());
        context.setLastTransitionTime(LocalDateTime.now());
        context.setLastEvent(event.name());

        contextRepository.save(context);
    }
}
