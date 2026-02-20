package com.logistics.order.statemachine;

import com.logistics.order.model.Order;
import com.logistics.order.model.OrderStatus;
import com.logistics.order.model.OrderStateMachineContext;
import com.logistics.order.repository.OrderRepository;
import com.logistics.order.repository.OrderStateMachineContextRepository;
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

/**
 * Service to manage order state machine lifecycle
 * Handles state transitions, persistence, and restoration
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderStateMachineService {

    @org.springframework.beans.factory.annotation.Qualifier("orderStateMachineFactory")
    private final StateMachineFactory<OrderState, OrderEvent> stateMachineFactory;
    private final OrderStateMachineContextRepository contextRepository;
    private final OrderRepository orderRepository;

    /**
     * Transition order to new state by sending event
     * 
     * @param orderId Order ID
     * @param event   Event to trigger transition
     * @param reason  Reason for transition
     * @return true if transition successful
     */
    @Transactional
    public boolean transitionOrder(String orderId, OrderEvent event, String reason) {
        try {
            Order order = orderRepository.findById(Long.parseLong(orderId))
                    .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));

            StateMachine<OrderState, OrderEvent> stateMachine = getOrCreateStateMachine(orderId);
            stateMachine.getExtendedState().getVariables().put("order", order);
            stateMachine.getExtendedState().getVariables().put("reason", reason);

            // Start state machine
            stateMachine.startReactively().block();

            // Send event
            Message<OrderEvent> message = MessageBuilder
                    .withPayload(event)
                    .setHeader("orderId", orderId)
                    .setHeader("reason", reason)
                    .build();

            boolean result = stateMachine.sendEvent(message);

            if (result) {
                // Update order status
                OrderState newState = stateMachine.getState().getId();
                order.setStatus(OrderStatus.valueOf(newState.name()));
                orderRepository.save(order);

                // Persist state machine context
                persistStateMachine(orderId, stateMachine, event);

                log.info("Order {} transitioned to {} via event {}", orderId, newState, event);
            } else {
                log.warn("State transition rejected for order {} with event {}", orderId, event);
            }

            return result;

        } catch (Exception e) {
            log.error("Error transitioning order state", e);
            return false;
        }
    }

    /**
     * Get current state of order
     */
    public OrderState getCurrentState(String orderId) {
        Optional<OrderStateMachineContext> contextOpt = contextRepository.findByOrderId(orderId);

        if (contextOpt.isPresent()) {
            return OrderState.valueOf(contextOpt.get().getCurrentState());
        }

        // Default to CREATED if no context found
        return OrderState.CREATED;
    }

    /**
     * Check if transition is valid
     */
    public boolean canTransition(String orderId, OrderEvent event) {
        try {
            StateMachine<OrderState, OrderEvent> stateMachine = getOrCreateStateMachine(orderId);

            // Check if event is accepted in current state
            return stateMachine.getTransitions().stream()
                    .anyMatch(t -> t.getSource().getId().equals(stateMachine.getState().getId())
                            && t.getTrigger() != null
                            && t.getTrigger().getEvent().equals(event));

        } catch (Exception e) {
            log.error("Error checking transition validity", e);
            return false;
        }
    }

    /**
     * Get or create state machine for order
     */
    private StateMachine<OrderState, OrderEvent> getOrCreateStateMachine(String orderId) {
        Optional<OrderStateMachineContext> contextOpt = contextRepository.findByOrderId(orderId);

        StateMachine<OrderState, OrderEvent> stateMachine = stateMachineFactory.getStateMachine(orderId);

        if (contextOpt.isPresent()) {
            // Restore state from context
            OrderStateMachineContext context = contextOpt.get();
            OrderState currentState = OrderState.valueOf(context.getCurrentState());

            // Set state machine to current state
            stateMachine.getStateMachineAccessor()
                    .doWithAllRegions(access -> {
                        access.resetStateMachine(
                                new org.springframework.statemachine.support.DefaultStateMachineContext<>(
                                        currentState, null, null, null));
                    });

            log.debug("Restored state machine for order {} to state {}", orderId, currentState);
        }

        return stateMachine;
    }

    /**
     * Persist state machine context
     */
    private void persistStateMachine(String orderId, StateMachine<OrderState, OrderEvent> stateMachine,
            OrderEvent event) {
        OrderState currentState = stateMachine.getState().getId();

        OrderStateMachineContext context = contextRepository.findByOrderId(orderId)
                .orElse(OrderStateMachineContext.builder()
                        .orderId(orderId)
                        .build());

        context.setCurrentState(currentState.name());
        context.setLastTransitionTime(LocalDateTime.now());
        context.setLastEvent(event.name());

        contextRepository.save(context);

        log.debug("Persisted state machine context for order {}: state={}, event={}",
                orderId, currentState, event);
    }

    /**
     * Reset state machine (for testing or error recovery)
     */
    @Transactional
    public void resetStateMachine(String orderId) {
        contextRepository.deleteByOrderId(orderId);
        log.info("Reset state machine for order {}", orderId);
    }
}
