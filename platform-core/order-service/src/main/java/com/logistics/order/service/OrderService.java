package com.logistics.order.service;

import com.logistics.order.model.TransportOrder;
import com.logistics.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;

    public List<TransportOrder> getAllOrders() {
        return orderRepository.findAll();
    }

    public Optional<TransportOrder> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    public TransportOrder createOrder(TransportOrder order) {
        if (order.getOrderId() == null) {
            order.setOrderId(UUID.randomUUID().toString());
        }
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        return orderRepository.save(order);
    }
}
