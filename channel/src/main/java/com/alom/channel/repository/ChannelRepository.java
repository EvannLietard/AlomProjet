package com.alom.channel.repository;

import com.alom.channel.entity.Channel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChannelRepository extends MongoRepository<Channel, String> {
    
    /**
     * Trouve un channel par son nom
     */
    Optional<Channel> findByName(String name);
    
    /**
     * Vérifie si un channel existe par son nom
     */
    boolean existsByName(String name);
}
