package com.alom.message.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class DynamicTopicListener {

    private final KafkaListenerEndpointRegistry registry;
    private final AdminClient adminClient;

    /**
     * Vérifie toutes les 5 secondes s'il y a de nouveaux topics
     * et redémarre le consumer pour qu'il les détecte
     */
    @Scheduled(fixedDelay = 5000, initialDelay = 10000)
    public void refreshTopics() {
        try {
            // Récupérer tous les topics Kafka
            Set<String> allTopics = adminClient.listTopics().names().get();
            
            // Filtrer uniquement les topics user-messages-*
            Set<String> userTopics = allTopics.stream()
                .filter(topic -> topic.startsWith("user-messages-"))
                .collect(Collectors.toSet());
            
            if (!userTopics.isEmpty()) {
                log.debug("Topics user-messages détectés: {}", userTopics);
                
                // Redémarrer le listener pour qu'il découvre les nouveaux topics
                MessageListenerContainer container = registry.getListenerContainer("messageListenerContainer");
                if (container != null && container.isRunning()) {
                    log.info("Redémarrage du consumer pour découverte des nouveaux topics");
                    container.stop();
                    container.start();
                }
            }
            
        } catch (InterruptedException | ExecutionException e) {
            log.error("Erreur lors de la vérification des topics: {}", e.getMessage());
            Thread.currentThread().interrupt();
        }
    }
}
