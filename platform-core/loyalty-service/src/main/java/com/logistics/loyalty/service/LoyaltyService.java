package com.logistics.loyalty.service;

import com.logistics.loyalty.model.LoyaltyProfile;
import com.logistics.loyalty.model.PointsTransaction;

import java.math.BigDecimal;
import java.util.List;

public interface LoyaltyService {
    LoyaltyProfile getLoyaltyProfile(String userId);
    void earnPoints(String userId, BigDecimal orderValue, String orderId);
    void redeemPoints(String userId, Integer points);
    List<PointsTransaction> getHistory(String userId);
}
