package com.alom.push.kafka;

import com.alom.push.tcp.ClientManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationConsumer {

    private final ClientManager clientManager;

    @KafkaListener(topicPattern = "user-messages-.*", groupId = "push-service-group")
    public void consumeMessage(@Payload Map<String, Object> message,
                                @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        try {
            // Extract userId from topic name (user-messages-{userId})
            String userId = topic.replace("user-messages-", "");
            log.info("Received message from topic: {} for userId: {}", topic, userId);

            // Check if the user is connected
            if (!clientManager.isClientConnected(userId)) {
                log.info("User {} is not connected, message will not be delivered", userId);
                return;
            }

            // Determine message type and format appropriately
            String formattedMessage = formatMessage(message);

            // Send message to TCP client
            clientManager.sendMessageToClient(userId, formattedMessage);
            log.info("Message sent to user {}: {}", userId, formattedMessage);

        } catch (Exception e) {
            log.error("Error processing message from topic {}: {}", topic, e.getMessage(), e);
        }
    }

    private String formatMessage(Map<String, Object> message) {
        try {
            // Check if it's a channel message or private message
            if (message.containsKey("channelId")) {
                // Channel message
                String channelName = (String) message.get("channelName");
                String sender = (String) message.get("sender");
                String content = (String) message.get("content");
                return String.format("[CHANNEL:%s] %s: %s", channelName, sender, content);
            } else {
                // Private message
                String sender = (String) message.get("sender");
                String content = (String) message.get("content");
                return String.format("[PRIVATE] %s: %s", sender, content);
            }
        } catch (Exception e) {
            log.error("Error formatting message: {}", e.getMessage());
            return message.toString();
        }
    }
}
