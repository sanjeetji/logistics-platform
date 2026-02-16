package com.logistics.shipment.statemachine;

import com.logistics.shipment.model.Shipment;
import com.logistics.shipment.statemachine.events.ShipmentStatusChangedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.guard.Guard;

import java.time.LocalDateTime;
import java.util.EnumSet;

@Configuration
@EnableStateMachineFactory
@RequiredArgsConstructor
@Slf4j
public class ShipmentStateMachineConfig extends EnumStateMachineConfigurerAdapter<ShipmentState, ShipmentEvent> {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void configure(StateMachineStateConfigurer<ShipmentState, ShipmentEvent> states) throws Exception {
        states
                .withStates()
                .initial(ShipmentState.CREATED)
                .states(EnumSet.allOf(ShipmentState.class))
                .end(ShipmentState.DELIVERED)
                .end(ShipmentState.RETURNED)
                .end(ShipmentState.CANCELLED);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<ShipmentState, ShipmentEvent> transitions) throws Exception {
        transitions
                // CREATED -> ASSIGNED
                .withExternal()
                .source(ShipmentState.CREATED).target(ShipmentState.ASSIGNED)
                .event(ShipmentEvent.ASSIGN)
                .action(publishShipmentStatusChangedAction())
                .and()
                // ASSIGNED -> PICKED_UP
                .withExternal()
                .source(ShipmentState.ASSIGNED).target(ShipmentState.PICKED_UP)
                .event(ShipmentEvent.PICKUP)
                .guard(shipmentValidationGuard())
                .action(publishShipmentStatusChangedAction())
                .and()
                // CREATED -> PICKED_UP (direct)
                .withExternal()
                .source(ShipmentState.CREATED).target(ShipmentState.PICKED_UP)
                .event(ShipmentEvent.PICKUP)
                .guard(shipmentValidationGuard())
                .action(publishShipmentStatusChangedAction())
                .and()
                // PICKED_UP -> IN_TRANSIT
                .withExternal()
                .source(ShipmentState.PICKED_UP).target(ShipmentState.IN_TRANSIT)
                .event(ShipmentEvent.START_TRANSIT)
                .action(publishShipmentStatusChangedAction())
                .and()
                // IN_TRANSIT -> AT_HUB
                .withExternal()
                .source(ShipmentState.IN_TRANSIT).target(ShipmentState.AT_HUB)
                .event(ShipmentEvent.ARRIVE_HUB)
                .action(publishShipmentStatusChangedAction())
                .and()
                // AT_HUB -> IN_TRANSIT
                .withExternal()
                .source(ShipmentState.AT_HUB).target(ShipmentState.IN_TRANSIT)
                .event(ShipmentEvent.DEPART_HUB)
                .action(publishShipmentStatusChangedAction())
                .and()
                // IN_TRANSIT -> OUT_FOR_DELIVERY
                .withExternal()
                .source(ShipmentState.IN_TRANSIT).target(ShipmentState.OUT_FOR_DELIVERY)
                .event(ShipmentEvent.OUT_FOR_DELIVERY)
                .action(publishShipmentStatusChangedAction())
                .and()
                // OUT_FOR_DELIVERY -> DELIVERED
                .withExternal()
                .source(ShipmentState.OUT_FOR_DELIVERY).target(ShipmentState.DELIVERED)
                .event(ShipmentEvent.DELIVER)
                .action(publishShipmentStatusChangedAction())
                .and()
                // Transitions to RETURNED
                .withExternal()
                .source(ShipmentState.CREATED).target(ShipmentState.RETURNED).event(ShipmentEvent.RETURN)
                .and()
                .withExternal()
                .source(ShipmentState.PICKED_UP).target(ShipmentState.RETURNED).event(ShipmentEvent.RETURN)
                .and()
                .withExternal()
                .source(ShipmentState.IN_TRANSIT).target(ShipmentState.RETURNED).event(ShipmentEvent.RETURN)
                .and()
                // Transitions to CANCELLED
                .withExternal()
                .source(ShipmentState.CREATED).target(ShipmentState.CANCELLED).event(ShipmentEvent.CANCEL);
    }

    @Bean
    public Guard<ShipmentState, ShipmentEvent> shipmentValidationGuard() {
        return context -> {
            Shipment shipment = (Shipment) context.getExtendedState().getVariables().get("shipment");
            return shipment != null && shipment.getOrderIds() != null && !shipment.getOrderIds().isEmpty();
        };
    }

    @Bean
    public Action<ShipmentState, ShipmentEvent> publishShipmentStatusChangedAction() {
        return context -> {
            Shipment shipment = (Shipment) context.getExtendedState().getVariables().get("shipment");
            String reason = (String) context.getExtendedState().getVariables().get("reason");

            if (shipment != null) {
                ShipmentStatusChangedEvent statusEvent = ShipmentStatusChangedEvent.builder()
                        .shipmentId(shipment.getShipmentId())
                        .previousStatus(context.getSource().getId().name())
                        .newStatus(context.getTarget().getId().name())
                        .timestamp(LocalDateTime.now())
                        .reason(reason)
                        .build();

                kafkaTemplate.send("shipment.status.changed", shipment.getShipmentId(), statusEvent);
                log.info("Published ShipmentStatusChangedEvent for shipment: {}", shipment.getShipmentId());
            }
        };
    }
}
