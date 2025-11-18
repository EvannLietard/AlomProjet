package com.alom.message.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

@Slf4j
@Configuration
public class KafkaTopicConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    /**
     * Bean AdminClient pour gérer les topics Kafka dynamiquement
     */
    @Bean
    public AdminClient adminClient() {
        Map<String, Object> configs = new HashMap<>();
        configs.put("bootstrap.servers", bootstrapServers);
        return AdminClient.create(configs);
    }

    /**
     * Crée un topic Kafka s'il n'existe pas déjà
     * 
     * @param topicName Le nom du topic à créer
     * @param numPartitions Le nombre de partitions (1 par défaut)
     * @param replicationFactor Le facteur de réplication (1 par défaut)
     */
    public void createTopicIfNotExists(String topicName, int numPartitions, short replicationFactor) {
        try (AdminClient adminClient = AdminClient.create(
                Collections.singletonMap("bootstrap.servers", bootstrapServers))) {
            
            // Vérifier si le topic existe déjà
            Set<String> existingTopics = adminClient.listTopics().names().get();
            
            if (!existingTopics.contains(topicName)) {
                log.info("Création du topic: {}", topicName);
                NewTopic newTopic = new NewTopic(topicName, numPartitions, replicationFactor);
                adminClient.createTopics(Collections.singleton(newTopic)).all().get();
                log.info("Topic créé avec succès: {}", topicName);
            } else {
                log.debug("Le topic existe déjà: {}", topicName);
            }
            
        } catch (InterruptedException | ExecutionException e) {
            log.error("Erreur lors de la création du topic {}: {}", topicName, e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Crée un topic avec les paramètres par défaut (1 partition, 1 replica)
     */
    public void createTopicIfNotExists(String topicName) {
        createTopicIfNotExists(topicName, 1, (short) 1);
    }
}
