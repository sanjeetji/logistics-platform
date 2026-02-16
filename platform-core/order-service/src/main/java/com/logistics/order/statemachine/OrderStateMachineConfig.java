package com.logistics.order.statemachine;

import com.logistics.order.model.Order;
import com.logistics.platform.event.dto.OrderStatusChangedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.guard.Guard;

import java.time.LocalDateTime;
import java.util.EnumSet;

/**
 * Order State Machine Configuration
 * Defines states, events, transitions, guards, and actions
 */
@Configuration
@EnableStateMachineFactory
@RequiredArgsConstructor
@Slf4j
public class OrderStateMachineConfig extends StateMachineConfigurerAdapter<OrderState, OrderEvent> {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void configure(StateMachineStateConfigurer<OrderState, OrderEvent> states) throws Exception {
        states
                .withStates()
                .initial(OrderState.CREATED)
                .states(EnumSet.allOf(OrderState.class))
                .end(OrderState.DELIVERED)
                .end(OrderState.CANCELLED)
                .end(OrderState.FAILED);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<OrderState, OrderEvent> transitions) throws Exception {
        transitions
                // CREATED -> VALIDATED
                .withExternal()
                .source(OrderState.CREATED).target(OrderState.VALIDATED)
                .event(OrderEvent.VALIDATE)
                .guard(orderValidationGuard())
                .action(publishOrderStatusChangedAction())
                .and()
                // VALIDATED -> ASSIGNED
                .withExternal()
                .source(OrderState.VALIDATED).target(OrderState.ASSIGNED)
                .event(OrderEvent.ASSIGN)
                .action(publishOrderStatusChangedAction())
                .and()
                // ASSIGNED -> PICKED_UP
                .withExternal()
                .source(OrderState.ASSIGNED).target(OrderState.PICKED_UP)
                .event(OrderEvent.PICKUP)
                .action(publishOrderStatusChangedAction())
                .and()
                // PICKED_UP -> IN_TRANSIT
                .withExternal()
                .source(OrderState.PICKED_UP).target(OrderState.IN_TRANSIT)
                .event(OrderEvent.START_TRANSIT)
                .action(publishOrderStatusChangedAction())
                .and()
                // IN_TRANSIT -> DELIVERED
                .withExternal()
                .source(OrderState.IN_TRANSIT).target(OrderState.DELIVERED)
                .event(OrderEvent.DELIVER)
                .action(publishOrderStatusChangedAction())
                .and()
                // Cancel from CREATED
                .withExternal()
                .source(OrderState.CREATED).target(OrderState.CANCELLED)
                .event(OrderEvent.CANCEL)
                .action(publishOrderStatusChangedAction())
                .and()
                // Cancel from VALIDATED
                .withExternal()
                .source(OrderState.VALIDATED).target(OrderState.CANCELLED)
                .event(OrderEvent.CANCEL)
                .action(publishOrderStatusChangedAction())
                .and()
                // Cancel from ASSIGNED
                .withExternal()
                .source(OrderState.ASSIGNED).target(OrderState.CANCELLED)
                .event(OrderEvent.CANCEL)
                .action(publishOrderStatusChangedAction())
                .and()
                // Fail from any non-terminal state
                .withExternal()
                .source(OrderState.CREATED).target(OrderState.FAILED)
                .event(OrderEvent.FAIL)
                .action(publishOrderStatusChangedAction())
                .and()
                .withExternal()
                .source(OrderState.VALIDATED).target(OrderState.FAILED)
                .event(OrderEvent.FAIL)
                .action(publishOrderStatusChangedAction())
                .and()
                .withExternal()
                .source(OrderState.ASSIGNED).target(OrderState.FAILED)
                .event(OrderEvent.FAIL)
                .action(publishOrderStatusChangedAction());
    }

    /**
     * Guard to validate order before transitioning to VALIDATED state
     */
    private Guard<OrderState, OrderEvent> orderValidationGuard() {
        return context -> {
            Order order = context.getExtendedState().get("order", Order.class);
            if (order == null) {
                log.warn("Order not found in state machine context");
                return false;
            }

            // Validate order has required fields
            boolean isValid = order.getCustomerId() != null
                    && order.getPickupLocation() != null
                    && order.getDropLocation() != null;

            if (!isValid) {
                log.warn("Order validation failed for order: {}", order.getId());
            }

            return isValid;
        };
    }

    /**
     * Action to publish OrderStatusChangedEvent when state transitions
     */
    private Action<OrderState, OrderEvent> publishOrderStatusChangedAction() {
        return context -> {
            try {
                Order order = context.getExtendedState().get("order", Order.class);
                if (order == null) {
                    log.error("Cannot publish event: Order not found in context");
                    return;
                }

                OrderState previousState = context.getSource() != null ? context.getSource().getId() : null;
                OrderState newState = context.getTarget().getId();
                OrderEvent event = context.getEvent();

                OrderStatusChangedEvent statusEvent = OrderStatusChangedEvent.builder()
                        .orderId(order.getId().toString())
                        .previousStatus(previousState != null ? previousState.name() : null)
                        .newStatus(newState.name())
                        .timestamp(LocalDateTime.now())
                        .changedBy("SYSTEM") // TODO: Get from security context
                        .reason(event != null ? event.name() : "UNKNOWN")
                        .build();

                kafkaTemplate.send("order.status.changed", order.getId().toString(), statusEvent);
                log.info("Published OrderStatusChangedEvent: {} -> {} for order: {}",
                        previousState, newState, order.getId());

            } catch (Exception e) {
                log.error("Error publishing OrderStatusChangedEvent", e);
            }
        };
    }
}
