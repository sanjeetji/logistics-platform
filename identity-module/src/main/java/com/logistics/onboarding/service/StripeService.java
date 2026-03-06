package com.logistics.onboarding.service;

import com.stripe.Stripe;
import com.stripe.model.Customer;
import com.stripe.model.PaymentMethod;
import com.stripe.model.Subscription;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.SubscriptionCreateParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class StripeService {
    
    @Value("${stripe.api-key}")
    private String stripeApiKey;
    
    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeApiKey;
    }
    
    /**
     * Create Stripe customer
     */
    public Customer createCustomer(String email, String name, String companyName) {
        try {
            CustomerCreateParams params = CustomerCreateParams.builder()
                    .setEmail(email)
                    .setName(name)
                    .setDescription("Tenant: " + companyName)
                    .build();
            
            Customer customer = Customer.create(params);
            log.info("Created Stripe customer: {}", customer.getId());
            return customer;
            
        } catch (Exception e) {
            log.error("Error creating Stripe customer", e);
            throw new RuntimeException("Failed to create Stripe customer", e);
        }
    }
    
    /**
     * Attach payment method to customer
     */
    public PaymentMethod attachPaymentMethod(String customerId, String paymentMethodId) {
        try {
            PaymentMethod paymentMethod = PaymentMethod.retrieve(paymentMethodId);
            
            Map<String, Object> params = new HashMap<>();
            params.put("customer", customerId);
            paymentMethod = paymentMethod.attach(params);
            
            log.info("Attached payment method {} to customer {}", paymentMethodId, customerId);
            return paymentMethod;
            
        } catch (Exception e) {
            log.error("Error attaching payment method", e);
            throw new RuntimeException("Failed to attach payment method", e);
        }
    }
    
    /**
     * Create subscription
     */
    public Subscription createSubscription(String customerId, String priceId, boolean startTrial, int trialDays) {
        try {
            SubscriptionCreateParams.Builder builder = SubscriptionCreateParams.builder()
                    .setCustomer(customerId)
                    .addItem(SubscriptionCreateParams.Item.builder()
                            .setPrice(priceId)
                            .build());
            
            if (startTrial) {
                builder.setTrialPeriodDays((long) trialDays);
            }
            
            Subscription subscription = Subscription.create(builder.build());
            log.info("Created subscription: {} for customer: {}", subscription.getId(), customerId);
            return subscription;
            
        } catch (Exception e) {
            log.error("Error creating subscription", e);
            throw new RuntimeException("Failed to create subscription", e);
        }
    }
    
    /**
     * Get price ID for subscription plan
     */
    public String getPriceIdForPlan(String plan) {
        // In production, these would be environment variables
        return switch (plan) {
            case "STARTER" -> "price_starter_monthly";
            case "GROWTH" -> "price_growth_monthly";
            case "ENTERPRISE" -> "price_enterprise_monthly";
            default -> throw new IllegalArgumentException("Unknown plan: " + plan);
        };
    }
    
    /**
     * Cancel subscription
     */
    public Subscription cancelSubscription(String subscriptionId) {
        try {
            Subscription subscription = Subscription.retrieve(subscriptionId);
            subscription = subscription.cancel();
            log.info("Cancelled subscription: {}", subscriptionId);
            return subscription;
            
        } catch (Exception e) {
            log.error("Error cancelling subscription", e);
            throw new RuntimeException("Failed to cancel subscription", e);
        }
    }
}
