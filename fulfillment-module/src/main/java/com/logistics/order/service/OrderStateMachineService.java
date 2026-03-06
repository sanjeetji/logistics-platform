package com.logistics.order.service;

import com.logistics.order.model.OrderStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("manualOrderStateMachineService")
@Slf4j
public class OrderStateMachineService {

    // Define valid state transitions
    private static final Map<OrderStatus, Set<OrderStatus>> VALID_TRANSITIONS = new HashMap<>();

    static {
        // CREATED can transition to ASSIGNED or CANCELLED
        VALID_TRANSITIONS.put(OrderStatus.CREATED,
                Set.of(OrderStatus.ASSIGNED, OrderStatus.CANCELLED));

        // ASSIGNED can transition to PICKED_UP, PARTIALLY_PICKED_UP or CANCELLED
        VALID_TRANSITIONS.put(OrderStatus.ASSIGNED,
                Set.of(OrderStatus.PICKED_UP, OrderStatus.PARTIALLY_PICKED_UP, OrderStatus.CANCELLED));

        // PARTIALLY_PICKED_UP can transition to IN_TRANSIT or CANCELLED
        VALID_TRANSITIONS.put(OrderStatus.PARTIALLY_PICKED_UP,
                Set.of(OrderStatus.IN_TRANSIT, OrderStatus.CANCELLED));

        // PICKED_UP can transition to IN_TRANSIT or CANCELLED
        VALID_TRANSITIONS.put(OrderStatus.PICKED_UP,
                Set.of(OrderStatus.IN_TRANSIT, OrderStatus.CANCELLED));

        // IN_TRANSIT can transition to DELIVERED, PARTIALLY_DELIVERED or CANCELLED
        VALID_TRANSITIONS.put(OrderStatus.IN_TRANSIT,
                Set.of(OrderStatus.DELIVERED, OrderStatus.PARTIALLY_DELIVERED, OrderStatus.CANCELLED));

        // DELIVERED, PARTIALLY_DELIVERED and CANCELLED are terminal states
        VALID_TRANSITIONS.put(OrderStatus.DELIVERED, Set.of());
        VALID_TRANSITIONS.put(OrderStatus.PARTIALLY_DELIVERED, Set.of());
        VALID_TRANSITIONS.put(OrderStatus.CANCELLED, Set.of());
    }

    /**
     * Validates if a status transition is allowed
     */
    public boolean isValidTransition(OrderStatus currentStatus, OrderStatus newStatus) {
        if (currentStatus == null || newStatus == null) {
            return false;
        }

        Set<OrderStatus> allowedTransitions = VALID_TRANSITIONS.get(currentStatus);
        boolean isValid = allowedTransitions != null && allowedTransitions.contains(newStatus);

        if (!isValid) {
            log.warn("Invalid state transition attempted: {} -> {}", currentStatus, newStatus);
        }

        return isValid;
    }

    /**
     * Gets all valid next states for a given status
     */
    public Set<OrderStatus> getValidNextStates(OrderStatus currentStatus) {
        return VALID_TRANSITIONS.getOrDefault(currentStatus, Set.of());
    }

    /**
     * Checks if a status is a terminal state (no further transitions allowed)
     */
    public boolean isTerminalState(OrderStatus status) {
        Set<OrderStatus> nextStates = VALID_TRANSITIONS.get(status);
        return nextStates == null || nextStates.isEmpty();
    }

    /**
     * Validates transition and throws exception if invalid
     */
    public void validateTransition(OrderStatus currentStatus, OrderStatus newStatus) {
        if (!isValidTransition(currentStatus, newStatus)) {
            throw new IllegalStateException(
                    String.format("Invalid state transition from %s to %s. Allowed transitions: %s",
                            currentStatus, newStatus, getValidNextStates(currentStatus)));
        }
    }
}
