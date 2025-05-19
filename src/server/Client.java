package server;

// Import statements
import card.Card;
import card.CardSkin;
import gamemode.GameMode;
import games.GameType;
import input.InputHandler;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class Client implements NetworkManager {
    // Server connection details
    private String serverIp = null;
    private int serverPort = 0;
    private final GameMode gameMode;
    private final GameType gameType;
    private CardSkin cardSkin = null;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String clientId;

    // Constructor
    public Client(String host, int port, GameMode gameMode, GameType gameType, CardSkin skin) {
        this.serverIp = host;
        this.serverPort = port;
        this.gameMode = gameMode;
        this.gameType = gameType;
        this.cardSkin = skin;
    }

    // Set client ID
    public void setClientId(int clientId) {
        this.clientId = String.valueOf(clientId);
    }

    // Get client ID
    public String getClientId() {
        return clientId;
    }

    @Override
    public void start() throws IOException {
        socket = new Socket(serverIp, serverPort);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        // Read the first message as the client ID
        String firstMessage = in.readLine();
        try {
            setClientId(Integer.parseInt(firstMessage));
        } catch (NumberFormatException e) {
            throw new IOException("Invalid client ID received from server: " + firstMessage, e);
        }

        // Process subsequent messages
        String message;
        while ((message = in.readLine()) != null) {
            processMessage(message);
        }
    }
    // Process incoming messages from server
    private void processMessage(String message) {
        String[] parts = message.split(":");
        if (parts.length == 0) return;

        switch (parts[0]) {
            case "STATE":
                String publicState = parts[1];
                gameMode.updateDisplay(null, publicState, null);
                break;
            case "HAND":
                List<Card> playerHand = new ArrayList<>();
                for (int i = 1; i < parts.length; i += 2) {
                    String rank = parts[i];
                    String suit = parts[i + 1];
                    playerHand.add(new Card(suit, rank, cardSkin));
                }
                gameMode.updateDisplay(playerHand, null, null);
                break;
        }
    }

    // Handle player actions
    private void handlePlayerAction() {
        InputHandler inputHandler = gameMode.getInputHandler();
        String action = inputHandler.getPlayerAction();
        inputHandler.processAction(action);
    }

    @Override
    public void sendMessage(String message) {
        // Send message to server
        out.println(message);
    }

    @Override
    public void sendMessageToClient(int clientId, String message) {
        // Doesn't apply to client
    }

    @Override
    public void receiveMessage(String message) {
        // Doesn't apply to client
    }

    @Override
    public void close() {
        // Close resources
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}