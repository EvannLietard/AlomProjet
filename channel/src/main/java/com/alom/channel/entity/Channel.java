package com.alom.channel.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "channels")
public class Channel {
    
    @Id
    private String id;
    
    @Indexed(unique = true)
    private String name;
    
    @Builder.Default
    private Set<String> subscribers = new HashSet<>();
    
    public void addSubscriber(String userId) {
        if (this.subscribers == null) {
            this.subscribers = new HashSet<>();
        }
        this.subscribers.add(userId);
    }
    
    public void removeSubscriber(String userId) {
        if (this.subscribers != null) {
            this.subscribers.remove(userId);
        }
    }
}
