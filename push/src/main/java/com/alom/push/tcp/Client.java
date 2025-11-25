package com.alom.push.tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Représente un client TCP connecté au serveur
 * Gère la communication avec un client individuel
 */
public class Client {

    private final Socket socket;
    private final String nickname;
    private final BufferedReader in;
    private final PrintWriter out;
    private volatile boolean connected;

    /**
     * Constructeur d'un client authentifié
     * @param socket Le socket de connexion
     * @param nickname Le nickname du client
     * @throws IOException si la création des flux échoue
     */
    public Client(Socket socket, String nickname) throws IOException {
        this.socket = socket;
        this.nickname = nickname;
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream(), true);
        this.connected = true;
    }

    /**
     * Constructeur pour un client non encore authentifié
     * @param socket Le socket de connexion
     * @throws IOException si la création des flux échoue
     */
    public Client(Socket socket) throws IOException {
        this(socket, null);
    }

    /**
     * Envoie un message au client
     * @param message Le message à envoyer
     * @return true si le message a été envoyé, false sinon
     */
    public boolean sendMessage(String message) {
        if (out != null && connected) {
            out.println(message);
            return !out.checkError();
        }
        return false;
    }

    /**
     * Lit un message du client
     * @return Le message reçu, ou null si la connexion est fermée
     * @throws IOException en cas d'erreur de lecture
     */
    public String readMessage() throws IOException {
        if (in != null && connected) {
            return in.readLine();
        }
        return null;
    }

    /**
     * Ferme la connexion avec le client
     */
    public void disconnect() {
        connected = false;

        try {
            if (out != null) out.close();
            if (in != null) in.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            System.err.println("Erreur lors de la fermeture de la connexion : " + e.getMessage());
        }
    }

    /**
     * Vérifie si le client est connecté
     * @return true si connecté, false sinon
     */
    public boolean isConnected() {
        return connected && socket != null && !socket.isClosed();
    }

    /**
     * Retourne le nickname du client
     * @return Le nickname
     */
    public String getNickname() {
        return nickname;
    }
}