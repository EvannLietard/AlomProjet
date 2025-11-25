package com.alom.push.tcp;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Gestionnaire centralisé des clients connectés
 * Responsabilité : Gérer l'état et les opérations sur l'ensemble des clients
 */
@Component
public class ClientManager {

    private final ConcurrentHashMap<String, Client> connectedClients = new ConcurrentHashMap<>();

    /**
     * Enregistre un nouveau client
     * @param client Le client à enregistrer
     * @return true si ajouté, false si le nickname existe déjà
     */
    public boolean addClient(Client client) {
        if (client == null || client.getNickname() == null) {
            return false;
        }

        Client previousClient = connectedClients.putIfAbsent(client.getNickname(), client);

        if (previousClient != null) {
            System.out.println("Client " + client.getNickname() + " déjà connecté, remplacement");
            previousClient.disconnect();
            connectedClients.put(client.getNickname(), client);
        } else {
            System.out.println("Client " + client.getNickname() + " ajouté à la liste des connectés");
        }

        return true;
    }

    /**
     * Retire un client de la liste
     * @param nickname Le nickname du client à retirer
     * @return Le client retiré, ou null si non trouvé
     */
    public Client removeClient(String nickname) {
        Client client = connectedClients.remove(nickname);
        if (client != null) {
            System.out.println("Client " + nickname + " retiré de la liste des connectés");
        }
        return client;
    }

    /**
     * Récupère un client par son nickname
     * @param nickname Le nickname du client
     * @return Le client, ou null si non trouvé
     */
    public Client getClient(String nickname) {
        return connectedClients.get(nickname);
    }

    /**
     * Envoie un message à un client spécifique
     * @param nickname Le nickname du destinataire
     * @param message Le message à envoyer
     * @return true si envoyé avec succès, false sinon
     */
    public boolean sendMessageToClient(String nickname, String message) {
        Client client = connectedClients.get(nickname);

        if (client != null && client.isConnected()) {
            boolean sent = client.sendMessage(message);
            if (sent) {
                System.out.println("Message envoyé à " + nickname + " : " + message);
            } else {
                System.out.println("Échec de l'envoi du message à " + nickname);
                removeClient(nickname);
            }
            return sent;
        } else {
            System.out.println("Client " + nickname + " non connecté");
            return false;
        }
    }

    /**
     * Vérifie si un client est connecté
     * @param nickname Le nickname du client
     * @return true si connecté, false sinon
     */
    public boolean isClientConnected(String nickname) {
        Client client = connectedClients.get(nickname);
        return client != null && client.isConnected();
    }

    /**
     * Déconnecte tous les clients
     */
    public void disconnectAll() {
        System.out.println("Déconnexion de tous les clients (" + connectedClients.size() + ")");
        connectedClients.values().forEach(Client::disconnect);
        connectedClients.clear();
    }
}

