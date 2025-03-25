package server;


import card.Card;
import card.CardSkin;
import gamemode.GameMode;
import gamemode.NonGraphicMode;
import games.GameType;
import server.networkinput.ClientInputHandler;
import server.networkinput.ClientInputHandlerFactory;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Client implements NetworkManager {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private GameMode gameMode;
    private int clientId;

    private ClientInputHandler inputHandler;
    private GameType gameType;
    private CardSkin cardSkin;

    public Client(String host, int port, GameMode gameMode, GameType gameType) {
        this.gameMode = gameMode;
        this.gameType = gameType;
        this.cardSkin = cardSkin;
        this.inputHandler = ClientInputHandlerFactory.getInstance().getHandler(gameType);        try {
            socket = new Socket(host, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.clientId = -1;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setClientId(int clientId) {

        this.clientId = clientId;
    }

    public int getClientId() {
        return clientId;
    }

    @Override
    public void start() {
        new Thread(() -> {
            try {
                String message;
                List<Card> playerHand = new ArrayList<>();
                while ((message = in.readLine()) != null) {
                    if (message.startsWith("STATE:")) {
                        String state = message.substring(6);
                        gameMode.updateDisplay(null, state, null);

                        if (gameMode instanceof NonGraphicMode) {
                            inputHandler.handleInput(this, state, playerHand);
                        }
                    } else if (message.startsWith("HAND:")) {
                        String hand = message.substring(5);
                        if (!hand.isEmpty()) {
                            String[] cards = hand.split(",");
                            for (String cardStr : cards) {
                                playerHand.add(createCardFromString(cardStr));
                            }
                        }

                        if (gameMode != null) {
                            gameMode.updateDisplay(playerHand, null, null);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
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