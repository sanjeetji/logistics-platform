package com.logistics.loyalty.service;

import com.logistics.loyalty.model.LoyaltyProfile;
import com.logistics.loyalty.model.PointsTransaction;
import com.logistics.loyalty.repository.LoyaltyProfileRepository;
import com.logistics.loyalty.repository.PointsTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoyaltyServiceImpl implements LoyaltyService {

    private final LoyaltyProfileRepository loyaltyProfileRepository;
    private final PointsTransactionRepository pointsTransactionRepository;

    private static final int POINTS_PER_CURRENCY_UNIT = 10;
    private static final int SILVER_THRESHOLD = 1000;
    private static final int GOLD_THRESHOLD = 5000;
    private static final int PLATINUM_THRESHOLD = 10000;

    @Override
    public LoyaltyProfile getLoyaltyProfile(String userId) {
        return loyaltyProfileRepository.findByUserId(userId)
                .orElseGet(() -> createNewProfile(userId));
    }

    private LoyaltyProfile createNewProfile(String userId) {
        LoyaltyProfile profile = LoyaltyProfile.builder()
                .userId(userId)
                .currentPoints(0)
                .totalPointsEarned(0)
                .currentTier(LoyaltyProfile.Tier.BRONZE)
                .lastActivityDate(LocalDateTime.now())
                .build();
        return loyaltyProfileRepository.save(profile);
    }

    @Override
    @Transactional
    public void earnPoints(String userId, BigDecimal orderValue, String orderId) {
        LoyaltyProfile profile = getLoyaltyProfile(userId);

        double multiplier = getTierMultiplier(profile.getCurrentTier());
        int points = (int) (orderValue.doubleValue() * POINTS_PER_CURRENCY_UNIT * multiplier);

        if (points > 0) {
            profile.setCurrentPoints(profile.getCurrentPoints() + points);
            profile.setTotalPointsEarned(profile.getTotalPointsEarned() + points);
            profile.setLastActivityDate(LocalDateTime.now());
            
            checkTierUpgrade(profile);
            loyaltyProfileRepository.save(profile);

            recordTransaction(profile.getId(), PointsTransaction.TransactionType.EARN, points, orderId, "Points earned from order");
            log.info("User {} earned {} points for order {}", userId, points, orderId);
        }
    }

    @Override
    @Transactional
    public void redeemPoints(String userId, Integer points) {
        if (points <= 0) {
            throw new IllegalArgumentException("Points to redeem must be greater than zero");
        }

        LoyaltyProfile profile = getLoyaltyProfile(userId);

        if (profile.getCurrentPoints() < points) {
            throw new IllegalArgumentException("Insufficient points balance");
        }

        profile.setCurrentPoints(profile.getCurrentPoints() - points);
        profile.setLastActivityDate(LocalDateTime.now());
        loyaltyProfileRepository.save(profile);

        recordTransaction(profile.getId(), PointsTransaction.TransactionType.REDEEM, -points, null, "Points redemption");
        
        // In a real system, verify wallet credit or other reward here
        log.info("User {} redeemed {} points", userId, points);
    }

    @Override
    public List<PointsTransaction> getHistory(String userId) {
        LoyaltyProfile profile = getLoyaltyProfile(userId);
        return pointsTransactionRepository.findByProfileId(profile.getId());
    }

    private double getTierMultiplier(LoyaltyProfile.Tier tier) {
        switch (tier) {
            case SILVER: return 1.1;
            case GOLD: return 1.25;
            case PLATINUM: return 1.5;
            default: return 1.0;
        }
    }

    private void checkTierUpgrade(LoyaltyProfile profile) {
        int total = profile.getTotalPointsEarned();
        LoyaltyProfile.Tier newTier = profile.getCurrentTier();

        if (total >= PLATINUM_THRESHOLD) {
            newTier = LoyaltyProfile.Tier.PLATINUM;
        } else if (total >= GOLD_THRESHOLD) {
            newTier = LoyaltyProfile.Tier.GOLD;
        } else if (total >= SILVER_THRESHOLD) {
            newTier = LoyaltyProfile.Tier.SILVER;
        }

        if (newTier != profile.getCurrentTier()) {
            log.info("User {} upgraded to tier {}", profile.getUserId(), newTier);
            profile.setCurrentTier(newTier);
        }
    }

    private void recordTransaction(Long profileId, PointsTransaction.TransactionType type, Integer points, String refId, String desc) {
        PointsTransaction tx = PointsTransaction.builder()
                .profileId(profileId)
                .transactionType(type)
                .points(points)
                .referenceId(refId)
                .description(desc)
                .build();
        pointsTransactionRepository.save(tx);
    }
}
