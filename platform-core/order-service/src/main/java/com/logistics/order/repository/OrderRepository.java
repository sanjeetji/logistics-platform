package com.logistics.order.repository;

import com.logistics.order.model.TransportOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<TransportOrder, Long> {
    Optional<TransportOrder> findByOrderId(String orderId);
}
