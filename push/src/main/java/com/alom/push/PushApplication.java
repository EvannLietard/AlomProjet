package com.alom.push;

import com.alom.push.tcp.TcpAuthServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.beans.factory.annotation.Autowired;

@SpringBootApplication
public class PushApplication {

    @Autowired
    private TcpAuthServer tcpAuthServer;

    public static void main(String[] args) {
        SpringApplication.run(PushApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void startTcpServer() {
        // Démarre le serveur TCP après l'initialisation Spring
        new Thread(() -> tcpAuthServer.start()).start();
        System.out.println("Application démarrée : HTTP + TCP actifs");
    }
}