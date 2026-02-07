package com.logistics.dispatch.strategy;

import com.logistics.dispatch.model.DispatchJob;
import com.logistics.platform.common.dto.order.TransportOrderDto;

public interface DispatchStrategy {
    /**
     * Attempt to find a driver for the given order.
     * 
     * @param order The order to dispatch
     * @param job   The tracking job
     * @return boolean true if assignment initiated/successful, false if no driver
     *         found immediately
     */
    boolean dispatch(TransportOrderDto order, DispatchJob job);
}
