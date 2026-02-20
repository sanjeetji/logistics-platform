package com.logistics.dispatch.engine;

import com.logistics.platform.common.dto.fleet.DriverLocationDto;
import com.logistics.platform.common.dto.order.TransportOrderDto;

public interface ScoringRule {
    double calculateScore(TransportOrderDto order, DriverLocationDto driver);

    int getWeight(); // 1-10
}
