package com.alom.push.kafka;

import com.alom.push.tcp.ClientManager;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class NotificationConsumer {

    private final ClientManager clientManager;

    @KafkaListener(topicPattern = "user-messages-.*", groupId = "push-service-group")
    public void consumeMessage(@Payload Map<String, Object> message,
                                @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        try {
            // Extract userId from topic name (user-messages-{userId})
            String userId = topic.replace("user-messages-", "");

            // Check if the user is connected
            if (!clientManager.isClientConnected(userId)) {
                return;
            }

            // Determine message type and format appropriately
            String formattedMessage = formatMessage(message);

            // Send message to TCP client
            clientManager.sendMessageToClient(userId, formattedMessage);

        } catch (Exception e) {
            // Error processing message
        }
    }

    private String formatMessage(Map<String, Object> message) {
        try {
            // Check if it's a channel message or private message
            if (message.containsKey("channelId")) {
                // Channel message
                String channelName = (String) message.get("channelName");
                String senderNickname = (String) message.get("senderNickname");
                String content = (String) message.get("content");
                return String.format("[CHANNEL:%s] %s: %s", channelName, senderNickname, content);
            } else {
                // Private message
                String senderNickname = (String) message.get("senderNickname");
                String content = (String) message.get("content");
                return String.format("[PRIVATE] %s: %s", senderNickname, content);
            }
        } catch (Exception e) {
            return message.toString();
        }
    }
}
