package server;


import gamemode.GameMode;
import gamemode.NonGraphicMode;
import games.GameType;
import server.networkinput.ClientInputHandler;
import server.networkinput.ClientInputHandlerFactory;

import java.io.*;
import java.net.*;

public class Client implements NetworkManager {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private GameMode gameMode;
    private int clientId;
    private String playerHand;
    private ClientInputHandler inputHandler;
    private GameType gameType;

    public Client(String host, int port, GameMode gameMode, GameType gameType) {
        this.gameMode = gameMode;
        this.gameType = gameType;
        this.playerHand = "";
        this.inputHandler = ClientInputHandlerFactory.ClientInputHandlerFactory.getHandler(gameType);
        try {
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
                while ((message = in.readLine()) != null) {
                    if (message.startsWith("STATE:")) {
                        String state = message.substring(6);
                        if (gameMode != null) {
                            gameMode.updateDisplay(playerHand, state, null);
                        }
                        if (gameMode instanceof NonGraphicMode) {
                            inputHandler.handleInput(this, state, playerHand);
                        }
                    } else if (message.startsWith("HAND:")) {
                        playerHand = message.substring(5);
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
}