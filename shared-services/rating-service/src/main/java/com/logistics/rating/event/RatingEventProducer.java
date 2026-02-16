package com.logistics.rating.event;

import com.logistics.platform.event.dto.RatingSubmittedEvent;
import com.logistics.platform.event.dto.ReviewModeratedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RatingEventProducer {

    private final StreamBridge streamBridge;

    public void publishRatingSubmitted(RatingSubmittedEvent event) {
        log.info("Publishing RatingSubmittedEvent for order: {}", event.getOrderId());
        streamBridge.send("ratingSubmittedSupplier-out-0", event);
    }

    public void publishReviewModerated(ReviewModeratedEvent event) {
        log.info("Publishing ReviewModeratedEvent for rating: {}", event.getRatingId());
        streamBridge.send("reviewModeratedSupplier-out-0", event);
    }
}
