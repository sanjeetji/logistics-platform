package com.logistics.order.repository;

import com.logistics.order.model.ScheduledOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ScheduledOrderRepository extends JpaRepository<ScheduledOrder, Long> {

    List<ScheduledOrder> findByCustomerId(String customerId);

    List<ScheduledOrder> findByNextExecutionTimeBeforeAndStatus(LocalDateTime dateTime,
            ScheduledOrder.ScheduledOrderStatus status);
}
