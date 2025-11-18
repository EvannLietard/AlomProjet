package com.alom.push.tcp;

import com.alom.push.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
public class TcpAuthServer {

    @Value("${tcp.port:9999}")
    private int tcpPort;

    @Value("${tcp.thread.pool.size:10}")
    private int threadPoolSize;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private ClientManager clientManager;

    private ServerSocket serverSocket;
    private volatile boolean running = false;
    private ExecutorService executorService;

    /**
     * Démarre le serveur TCP
     */
    public void start() {
        try {
            serverSocket = new ServerSocket(tcpPort);
            running = true;

            executorService = Executors.newFixedThreadPool(threadPoolSize);

            System.out.println("Serveur TCP démarré sur le port " + tcpPort );
            System.out.println("Pool de threads initialisé avec " + threadPoolSize + " threads");
            System.out.println("En attente de connexions...");

            
            while (running) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Nouvelle connexion : " + clientSocket.getRemoteSocketAddress());                
                executorService.execute(() -> handleClient(clientSocket));
            }
            
        } catch (IOException e) {
            if (running) {
                System.err.println("Erreur serveur : " + e.getMessage());
            }
        }
    }

    /**
     * Gère une connexion client
     */
    private void handleClient(Socket clientSocket) {
        Client client = null;

        try {
            client = new Client(clientSocket);

            client.sendMessage("Bienvenue sur notre serveur d'authentification");
            client.sendMessage("Veuillez entrer votre token d'authentification :");

            String token = client.readMessage();

            if (token == null || token.trim().isEmpty()) {
                client.sendMessage("Erreur : Token vide");
                System.out.println("Erreur : Token vide");
                return;
            }
            
            token = token.trim();
            System.out.println("Token reçu : " + token);
            
            String nickname = tokenService.getNickname(token);
            
            if (nickname != null) {
                client.sendMessage("Authentification réussie !");
                client.sendMessage("Bienvenue " + nickname + " !");
                System.out.println("Auth OK : " + nickname);

                Client authenticatedClient = new Client(clientSocket, nickname);
                clientManager.addClient(authenticatedClient);
                client = authenticatedClient;

                String line;
                while ((line = client.readMessage()) != null) {
                    System.out.println("Message reçu de " + nickname + " : " + line);
                }

            } else {
                client.sendMessage("Authentification échouée : Token invalide");
                System.out.println("Auth échouée : Token inconnu");
            }
            
        } catch (IOException e) {
            System.err.println("Erreur client : " + e.getMessage());
        } finally {
            if (client != null) {
                if (client.getNickname() != null) {
                    clientManager.removeClient(client.getNickname());
                }
                client.disconnect();
            }
            System.out.println("Connexion fermée");
        }
    }

    /**
     * Envoie un message à un client spécifique
     * @param nickname Le nickname du destinataire
     * @param message Le contenu du message
     * @return true si le message a été envoyé, false sinon
     */
    public boolean sendMessageToClient(String nickname, String message) {
        return clientManager.sendMessageToClient(nickname, message);
    }

    /**
     * Vérifie si un client est connecté
     */
    public boolean isClientConnected(String nickname) {
        return clientManager.isClientConnected(nickname);
    }

    /**
     * Arrête le serveur TCP
     */
    public void stop() {
        running = false;
        try {
            // Déconnecter tous les clients
            clientManager.disconnectAll();

            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }

            if (executorService != null) {
                executorService.shutdown();
                try {
                    if (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
                        executorService.shutdownNow();
                    }
                    System.out.println("Pool de threads arrêté");
                } catch (InterruptedException e) {
                    executorService.shutdownNow();
                    Thread.currentThread().interrupt();
                }
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de l'arrêt : " + e.getMessage());
        }
    }
}