package com.logistics.chat.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class ChatBotService {

    private static final Map<String, String> FAQ_RESPONSES = new HashMap<>();

    static {
        FAQ_RESPONSES.put("order status",
                "To check your order status, please provide your order ID and I'll fetch the latest information for you.");
        FAQ_RESPONSES.put("track order",
                "You can track your order using the tracking ID sent to your email. Would you like me to look up your order?");
        FAQ_RESPONSES.put("delivery time",
                "Typical delivery times are 2-5 business days for standard shipping. For express delivery, it's 1-2 business days.");
        FAQ_RESPONSES.put("cancel order",
                "To cancel an order, please provide your order ID. Note that orders can only be cancelled before they're dispatched.");
        FAQ_RESPONSES.put("refund",
                "Refunds are processed within 5-7 business days after the item is received. Please provide your order ID for refund status.");
        FAQ_RESPONSES.put("contact support",
                "You can reach our support team at support@logistics.com or call 1-800-LOGISTICS (1-800-564-4784).");
        FAQ_RESPONSES.put("help",
                "I can help you with: order status, tracking, delivery times, cancellations, and refunds. How can I assist you today?");
    }

    /**
     * Process user message and return bot response
     */
    public String processMessage(String userMessage) {
        if (userMessage == null || userMessage.trim().isEmpty()) {
            return "Hello! How can I help you today?";
        }

        String lowerMessage = userMessage.toLowerCase().trim();

        // Check for FAQ keywords
        for (Map.Entry<String, String> entry : FAQ_RESPONSES.entrySet()) {
            if (lowerMessage.contains(entry.getKey())) {
                log.info("Bot responded to query about: {}", entry.getKey());
                return entry.getValue();
            }
        }

        // Order ID detection (simple pattern)
        if (lowerMessage.matches(".*\\b[A-Z]{2}\\d{6,}\\b.*")) {
            return "I found an order ID in your message. Let me fetch the details...";
        }

        // Greeting detection
        if (lowerMessage.matches("(hi|hello|hey|greetings).*")) {
            return "Hello! I'm here to help with your orders and deliveries. What can I do for you?";
        }

        // Default response
        return "I'm not sure I understand. You can ask me about order status, tracking, delivery times, or type 'help' for more options.";
    }

    /**
     * Check if message is a bot command
     */
    public boolean isBotCommand(String message) {
        if (message == null)
            return false;
        String lower = message.toLowerCase().trim();
        return lower.startsWith("/bot") || lower.startsWith("@bot");
    }

    /**
     * Get help message
     */
    public String getHelpMessage() {
        return "**Available Commands:**\n" +
                "- Ask about 'order status' with your order ID\n" +
                "- Inquire about 'delivery time'\n" +
                "- Request order 'tracking'\n" +
                "- Ask to 'cancel order'\n" +
                "- Check 'refund' status\n" +
                "- Type 'help' anytime for this message";
    }
}
