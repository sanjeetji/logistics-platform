package com.logistics.fleet.statemachine;

import com.logistics.fleet.model.Driver;
import com.logistics.fleet.model.VerificationStatus;
import com.logistics.fleet.statemachine.events.DriverStatusChangedEvent;
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
@EnableStateMachineFactory(name = "driverStateMachineFactory")
@RequiredArgsConstructor
@Slf4j
public class DriverStateMachineConfig extends EnumStateMachineConfigurerAdapter<DriverState, DriverEvent> {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void configure(StateMachineStateConfigurer<DriverState, DriverEvent> states) throws Exception {
        states
                .withStates()
                .initial(DriverState.OFFLINE)
                .states(EnumSet.allOf(DriverState.class));
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<DriverState, DriverEvent> transitions) throws Exception {
        transitions
                // OFFLINE -> AVAILABLE
                .withExternal()
                .source(DriverState.OFFLINE).target(DriverState.AVAILABLE)
                .event(DriverEvent.GO_ONLINE)
                .guard(verifiedDriverGuard())
                .guard(activeVehicleGuard())
                .action(publishDriverStatusChangedAction())
                .and()
                // AVAILABLE -> ASSIGNED
                .withExternal()
                .source(DriverState.AVAILABLE).target(DriverState.ASSIGNED)
                .event(DriverEvent.ASSIGN)
                .action(publishDriverStatusChangedAction())
                .and()
                // ASSIGNED -> AVAILABLE (REJECTED)
                .withExternal()
                .source(DriverState.ASSIGNED).target(DriverState.AVAILABLE)
                .event(DriverEvent.REJECT_ASSIGNMENT)
                .action(publishDriverStatusChangedAction())
                .and()
                // ASSIGNED -> EN_ROUTE_PICKUP
                .withExternal()
                .source(DriverState.ASSIGNED).target(DriverState.EN_ROUTE_PICKUP)
                .event(DriverEvent.START_PICKUP)
                .action(publishDriverStatusChangedAction())
                .and()
                // EN_ROUTE_PICKUP -> AT_PICKUP
                .withExternal()
                .source(DriverState.EN_ROUTE_PICKUP).target(DriverState.AT_PICKUP)
                .event(DriverEvent.ARRIVE_PICKUP)
                .action(publishDriverStatusChangedAction())
                .and()
                // AT_PICKUP -> EN_ROUTE_DELIVERY
                .withExternal()
                .source(DriverState.AT_PICKUP).target(DriverState.EN_ROUTE_DELIVERY)
                .event(DriverEvent.START_DELIVERY)
                .action(publishDriverStatusChangedAction())
                .and()
                // EN_ROUTE_DELIVERY -> AT_DELIVERY
                .withExternal()
                .source(DriverState.EN_ROUTE_DELIVERY).target(DriverState.AT_DELIVERY)
                .event(DriverEvent.ARRIVE_DELIVERY)
                .action(publishDriverStatusChangedAction())
                .and()
                // AT_DELIVERY -> AVAILABLE (COMPLETED)
                .withExternal()
                .source(DriverState.AT_DELIVERY).target(DriverState.AVAILABLE)
                .event(DriverEvent.COMPLETE_DELIVERY)
                .action(publishDriverStatusChangedAction())
                .and()
                // AVAILABLE -> ON_BREAK
                .withExternal()
                .source(DriverState.AVAILABLE).target(DriverState.ON_BREAK)
                .event(DriverEvent.TAKE_BREAK)
                .action(publishDriverStatusChangedAction())
                .and()
                // ON_BREAK -> AVAILABLE
                .withExternal()
                .source(DriverState.ON_BREAK).target(DriverState.AVAILABLE)
                .event(DriverEvent.END_BREAK)
                .action(publishDriverStatusChangedAction())
                .and()
                // GLOBAL Transitions to OFFLINE
                .withExternal()
                .source(DriverState.AVAILABLE).target(DriverState.OFFLINE).event(DriverEvent.GO_OFFLINE)
                .action(publishDriverStatusChangedAction())
                .and()
                .withExternal()
                .source(DriverState.ON_BREAK).target(DriverState.OFFLINE).event(DriverEvent.GO_OFFLINE)
                .action(publishDriverStatusChangedAction())
                .and()
                .withExternal()
                .source(DriverState.ASSIGNED).target(DriverState.OFFLINE).event(DriverEvent.GO_OFFLINE)
                .action(publishDriverStatusChangedAction());
    }

    @Bean
    public Guard<DriverState, DriverEvent> verifiedDriverGuard() {
        return context -> {
            Driver driver = (Driver) context.getExtendedState().getVariables().get("driver");
            return driver != null && driver.getVerificationStatus() == VerificationStatus.VERIFIED;
        };
    }

    @Bean
    public Guard<DriverState, DriverEvent> activeVehicleGuard() {
        return context -> {
            Driver driver = (Driver) context.getExtendedState().getVariables().get("driver");
            return driver != null && driver.getCurrentVehicleId() != null;
        };
    }

    @Bean
    public Action<DriverState, DriverEvent> publishDriverStatusChangedAction() {
        return context -> {
            Driver driver = (Driver) context.getExtendedState().getVariables().get("driver");
            String reason = (String) context.getExtendedState().getVariables().get("reason");

            if (driver != null) {
                DriverStatusChangedEvent statusEvent = DriverStatusChangedEvent.builder()
                        .driverEmail(driver.getEmail())
                        .previousStatus(context.getSource() != null ? context.getSource().getId().name() : null)
                        .newStatus(context.getTarget().getId().name())
                        .timestamp(LocalDateTime.now())
                        .reason(reason)
                        .build();

                kafkaTemplate.send("driver.status.changed", driver.getEmail(), statusEvent);
                log.info("Published DriverStatusChangedEvent for driver: {}", driver.getEmail());
            }
        };
    }
}
