package com.logistics.fulfillment.orchestration;

import org.springframework.stereotype.Service;
import com.logistics.platform.common.dto.TenantAware;
import com.logistics.platform.utils.tenant.TenantContextUtils;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service

/**
 * Service to manage multi‑leg order orchestration.
 * Represents an order as a directed graph of {@link OrderLeg}s.
 * This is a simple in‑memory implementation suitable for a monolith.
 */
public class OrderGraphService {

    public enum ServiceType {
        SAME_DAY, SCHEDULED, EXPRESS, ECONOMY
    }

    /**
     * Represents a leg in the order flow (e.g., pickup → hub → line‑haul → hub →
     * last‑mile).
     */
    public static class OrderLeg implements TenantAware {
        private final String id;
        private String tenantId;
        private final String orderId;
        private String parentOrderId;
        private String fromLocation;
        private String toLocation;
        private ServiceType serviceType; // SAME_DAY, SCHEDULED, EXPRESS, ECONOMY
        private String status = "PENDING"; // status of the leg
        private String shipmentId; // ID grouping legs into a shipment
        private LocalDateTime promiseWindowStart;
        private LocalDateTime promiseWindowEnd;
        private LocalDateTime pickupSla;
        private LocalDateTime deliverySla;
        private final List<OrderLeg> nextLegs = new ArrayList<>();

        public OrderLeg(String orderId, String fromLocation, String toLocation, ServiceType serviceType) {
            this.id = UUID.randomUUID().toString();
            this.orderId = orderId;
            this.fromLocation = fromLocation;
            this.toLocation = toLocation;
            this.serviceType = serviceType;
        }

        public String getId() {
            return id;
        }

        @Override
        public String getTenantId() {
            return tenantId;
        }

        @Override
        public void setTenantId(String tenantId) {
            this.tenantId = tenantId;
        }

        public String getOrderId() {
            return orderId;
        }

        public String getParentOrderId() {
            return parentOrderId;
        }

        public void setParentOrderId(String parentOrderId) {
            this.parentOrderId = parentOrderId;
        }

        public String getFromLocation() {
            return fromLocation;
        }

        public String getToLocation() {
            return toLocation;
        }

        public ServiceType getServiceType() {
            return serviceType;
        }

        public String getStatus() {
            return status;
        }

        public String getShipmentId() {
            return shipmentId;
        }

        public void setShipmentId(String shipmentId) {
            this.shipmentId = shipmentId;
        }

        public LocalDateTime getPromiseWindowStart() {
            return promiseWindowStart;
        }

        public void setPromiseWindowStart(LocalDateTime promiseWindowStart) {
            this.promiseWindowStart = promiseWindowStart;
        }

        public LocalDateTime getPromiseWindowEnd() {
            return promiseWindowEnd;
        }

        public void setPromiseWindowEnd(LocalDateTime promiseWindowEnd) {
            this.promiseWindowEnd = promiseWindowEnd;
        }

        public LocalDateTime getPickupSla() {
            return pickupSla;
        }

        public void setPickupSla(LocalDateTime pickupSla) {
            this.pickupSla = pickupSla;
        }

        public LocalDateTime getDeliverySla() {
            return deliverySla;
        }

        public void setDeliverySla(LocalDateTime deliverySla) {
            this.deliverySla = deliverySla;
        }

        public List<OrderLeg> getNextLegs() {
            return nextLegs;
        }

        public void addNextLeg(OrderLeg leg) {
            this.nextLegs.add(leg);
        }

        // Setter for status
        public void setStatus(String status) {
            this.status = status;
        }

        public void setServiceType(ServiceType serviceType) {
            this.serviceType = serviceType;
        }

        // Setters for mutable locations
        public void setFromLocation(String fromLocation) {
            this.fromLocation = fromLocation;
        }

        public void setToLocation(String toLocation) {
            this.toLocation = toLocation;
        }
    }

    private final Map<String, List<OrderLeg>> tenantRoots = new HashMap<>();

    private List<OrderLeg> getAuthorizedTenantRoots() {
        List<String> authorizedTenantIds = TenantContextUtils.getAuthorizedTenantIds();
        List<OrderLeg> allRoots = new ArrayList<>();
        for (String tenantId : authorizedTenantIds) {
            List<OrderLeg> roots = tenantRoots.get(tenantId);
            if (roots != null) {
                allRoots.addAll(roots);
            }
        }
        return allRoots;
    }

    private List<OrderLeg> getCurrentTenantRoots() {
        String tenantId = TenantContextUtils.getTenantId();
        return tenantRoots.computeIfAbsent(tenantId, k -> new ArrayList<>());
    }

    /**
     * Create a new root leg for an order.
     */
    public OrderLeg createRootLeg(String orderId, String from, String to, ServiceType serviceType) {
        OrderLeg leg = new OrderLeg(orderId, from, to, serviceType);
        leg.setTenantId(TenantContextUtils.getTenantId());
        assignDefaultSlas(leg);
        getCurrentTenantRoots().add(leg);
        return leg;
    }

    /**
     * Add a subsequent leg to an existing leg.
     */
    public OrderLeg addLeg(OrderLeg parent, String from, String to, ServiceType serviceType) {
        OrderLeg leg = new OrderLeg(parent.getOrderId(), from, to, serviceType);
        assignDefaultSlas(leg);
        parent.addNextLeg(leg);
        return leg;
    }

    private void assignDefaultSlas(OrderLeg leg) {
        LocalDateTime now = LocalDateTime.now();
        switch (leg.getServiceType()) {
            case EXPRESS:
                leg.setPickupSla(now.plusHours(1));
                leg.setDeliverySla(now.plusHours(4));
                break;
            case SAME_DAY:
                leg.setPickupSla(now.plusHours(2));
                leg.setDeliverySla(now.plusHours(8));
                break;
            case ECONOMY:
                leg.setPickupSla(now.plusHours(4));
                leg.setDeliverySla(now.plusDays(1));
                break;
            case SCHEDULED:
                // For scheduled, we expect manual SLA setting or a separate logic
                break;
        }
    }

    /**
     * Retrieve all root legs (typically one per order).
     */
    /**
     * Split an existing root order into multiple new root orders.
     * The original root is retained (optional) and new roots are created based on
     * provided legs.
     */
    public List<OrderLeg> splitRoot(OrderLeg original, List<OrderLeg> newRoots) {
        // Remove original from roots list
        getCurrentTenantRoots().remove(original);
        // Add each new root leg to roots
        for (OrderLeg leg : newRoots) {
            leg.setTenantId(TenantContextUtils.getTenantId());
            getCurrentTenantRoots().add(leg);
        }
        return new ArrayList<>(newRoots);
    }

    /**
     * Merge two root orders into a single order by linking the second root as a
     * child of the first.
     * Returns the merged root.
     */
    public OrderLeg mergeRoots(OrderLeg firstRoot, OrderLeg secondRoot) {
        // Attach second root as a next leg of the first root
        firstRoot.addNextLeg(secondRoot);
        // Remove second root from top-level roots list
        getCurrentTenantRoots().remove(secondRoot);
        return firstRoot;
    }

    /**
     * Create a multi‑stop route given a list of leg specifications.
     * Each legSpec is a String array: [fromLocation, toLocation, serviceType].
     * The first element becomes the root leg; subsequent elements are added as
     * child legs.
     */
    public OrderLeg createMultiStopRoute(String orderId, List<Object[]> legSpecs) {
        if (legSpecs == null || legSpecs.isEmpty()) {
            throw new IllegalArgumentException("Leg specifications must not be empty");
        }
        Object[] first = legSpecs.get(0);
        OrderLeg root = createRootLeg(orderId, (String) first[0], (String) first[1], (ServiceType) first[2]);
        OrderLeg current = root;
        for (int i = 1; i < legSpecs.size(); i++) {
            Object[] spec = legSpecs.get(i);
            OrderLeg next = addLeg(current, (String) spec[0], (String) spec[1], (ServiceType) spec[2]);
            current = next;
        }
        return root;
    }

    // Backorder handling
    public OrderLeg backorderLeg(OrderLeg leg) {
        leg.setStatus("BACKORDERED");
        return leg;
    }

    // Re‑routing handling
    public OrderLeg rerouteLeg(OrderLeg leg, String newFrom, String newTo) {
        leg.setFromLocation(newFrom);
        leg.setToLocation(newTo);
        leg.setStatus("REROUTED");
        return leg;
    }

    /**
     * Get all legs associated with a specific shipment ID.
     */
    public List<OrderLeg> getLegsByShipment(String shipmentId) {
        List<OrderLeg> result = new ArrayList<>();
        for (OrderLeg root : getAuthorizedTenantRoots()) {
            collectLegsByShipment(root, shipmentId, result);
        }
        return result;
    }

    private void collectLegsByShipment(OrderLeg leg, String shipmentId, List<OrderLeg> result) {
        if (shipmentId.equals(leg.getShipmentId())) {
            result.add(leg);
        }
        for (OrderLeg next : leg.getNextLegs()) {
            collectLegsByShipment(next, shipmentId, result);
        }
    }

    /**
     * Link a child order to a parent order.
     */
    public void linkOrders(String childOrderId, String parentOrderId) {
        for (OrderLeg root : getAuthorizedTenantRoots()) {
            if (childOrderId.equals(root.getOrderId())) {
                root.setParentOrderId(parentOrderId);
            }
        }
    }

    /**
     * Find all root legs that are children of the given parent order ID.
     */
    public List<OrderLeg> getChildOrders(String parentOrderId) {
        List<OrderLeg> children = new ArrayList<>();
        for (OrderLeg root : getAuthorizedTenantRoots()) {
            if (parentOrderId.equals(root.getParentOrderId())) {
                children.add(root);
            }
        }
        return children;
    }

    /**
     * Identify all legs that are at risk of missing their delivery promise.
     */
    public List<OrderLeg> getLegsAtRisk() {
        List<OrderLeg> riskLegs = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        for (OrderLeg root : getAuthorizedTenantRoots()) {
            collectRiskLegs(root, now, riskLegs);
        }
        return riskLegs;
    }

    private void collectRiskLegs(OrderLeg leg, LocalDateTime now, List<OrderLeg> result) {
        if (leg.getPromiseWindowEnd() != null && now.isAfter(leg.getPromiseWindowEnd())
                && !"COMPLETED".equals(leg.getStatus())) {
            result.add(leg);
        }
        for (OrderLeg next : leg.getNextLegs()) {
            collectRiskLegs(next, now, result);
        }
    }

    /**
     * Identify all legs that have breached their pickup or delivery SLA.
     */
    public List<OrderLeg> getSlaBreaches() {
        List<OrderLeg> breaches = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        for (OrderLeg root : getAuthorizedTenantRoots()) {
            collectSlaBreaches(root, now, breaches);
        }
        return breaches;
    }

    private void collectSlaBreaches(OrderLeg leg, LocalDateTime now, List<OrderLeg> result) {
        boolean breached = false;
        if (leg.getPickupSla() != null && now.isAfter(leg.getPickupSla()) && "PENDING".equals(leg.getStatus())) {
            breached = true;
        } else if (leg.getDeliverySla() != null && now.isAfter(leg.getDeliverySla())
                && !"COMPLETED".equals(leg.getStatus())) {
            breached = true;
        }

        if (breached) {
            result.add(leg);
        }
        for (OrderLeg next : leg.getNextLegs()) {
            collectSlaBreaches(next, now, result);
        }
    }
}
