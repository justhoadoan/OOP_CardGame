package server;


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
    private String serverIp = null;
    private int serverPort = 0;
    private final GameMode gameMode;
    private final GameType gameType;
    private CardSkin cardSkin = null;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String clientId;

    public Client(String host, int port, GameMode gameMode, GameType gameType, CardSkin skin) {
        this.serverIp = serverIp;
        this.serverPort = serverPort;
        this.gameMode = gameMode;
        this.gameType = gameType;
        this.cardSkin = cardSkin;
    }

    public void setClientId(int clientId) {

        this.clientId = String.valueOf(clientId);
    }

    public String getClientId() {
        return clientId;
    }

    @Override
    public void start() throws IOException {
        socket = new Socket(serverIp, serverPort);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        setClientId(Integer.parseInt(in.readLine()));
        String message;
        while ((message = in.readLine()) != null) {
            processMessage(message);

        }
    }

    private void processMessage(String message) {
        String[] parts = message.split(":");
        if (parts.length == 0) return;

        switch (parts[0]) {
            case "HAND":
                List<Card> playerHand = new ArrayList<>();
                for (int i = 1; i < parts.length; i += 2) {
                    String rank = parts[i];
                    String suit = parts[i + 1];
                    playerHand.add(new Card(suit, rank, cardSkin));
                }
                gameMode.updateDisplay(playerHand, null, null);
                handlePlayerAction();
                break;
            case "STATE":
                String publicState = parts[1];
                gameMode.updateDisplay(null, publicState, null);
                handlePlayerAction();
                break;
            case "WINNER":
                String winner = parts[1];
                gameMode.updateDisplay(null, null, winner);
                break;
        }
    }
    private void handlePlayerAction() {
        InputHandler inputHandler = gameMode.getInputHandler();
        String action = inputHandler.getPlayerAction();
        inputHandler.processAction(action);
    }


    @Override
    public void sendMessage(String message) {

        out.println(message);
    }

    @Override
    public void sendMessageToClient(int clientId, String message) {
        // doesn't apply to client
    }

    @Override
    public void receiveMessage(String message) {
        // doesn't apply to client
    }

    @Override
    public void close() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private Card createCardFromString(String cardStr) {
        String[] parts = cardStr.split(" of ");
        String rank = parts[0];
        String suit = parts[1];
        return new Card(rank, suit, cardSkin);
    }
}