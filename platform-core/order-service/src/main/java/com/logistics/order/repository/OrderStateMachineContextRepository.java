package com.logistics.order.repository;

import com.logistics.order.model.OrderStateMachineContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderStateMachineContextRepository extends JpaRepository<OrderStateMachineContext, Long> {
    Optional<OrderStateMachineContext> findByOrderId(String orderId);

    void deleteByOrderId(String orderId);
}
