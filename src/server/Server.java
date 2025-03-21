package server;

import games.Game;
import games.GameType;
import player.Playable;
import player.Player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.stream.Collectors;
import static java.util.Locale.filter;

public class Server implements NetworkManager {
    private ServerSocket serverSocket;
    private Game game;
    Map<Integer, ClientHandler> clientHandlers;
    private volatile boolean isRunning;
    private int nextClientId;
    public Server(int port, Game game) {
        try {
            this.nextClientId=0;
            serverSocket = new ServerSocket(port);
            this.game = game;
            isRunning = false;

        } catch (IOException e) {
            System.err.println("Error initializing server on port " + port);
        }
    }
    public void setGame(Game game) {
        this.game = game;
    }

    @Override
    public void start() {
         isRunning = true;
         new Thread(() -> {
             while (isRunning) {
                 try {
                     Socket clientSocket = serverSocket.accept();
                     int clientId = nextClientId++;
                     ClientHandler clientHandler = new ClientHandler(clientSocket, this, clientId);
                     clientHandlers.put(clientId, clientHandler);
                     clientHandler.start();
                     System.out.println("Client " + clientId + " connected");
                     if (game != null) {
                         Player newPlayer = new Player("Client-" + clientId, clientId);
                         game.addPlayer((Playable) newPlayer);
                         clientHandler.sendMessage("STATE:" + game.getPublicState());
                     }
                 } catch (IOException e) {
                     if (isRunning) {
                         System.err.println("Accept failed: " + e.getMessage());
                     }
                 }
             }
         }).start();
    }


    @Override
    public void close() {
        try{
            for(ClientHandler clientHandler : clientHandlers.values()) {
                clientHandler.close();
            }
            clientHandlers.clear();
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            System.out.println("Server closed");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendMessage(String message) {
        for(ClientHandler clientHandler : clientHandlers.values()) {
            clientHandler.sendMessage(message);

        }
    }

    @Override
    public void sendMessageToClient(int clientId, String message) {
        ClientHandler handler = clientHandlers.get(clientId);
        if (handler != null) {
            handler.sendMessage(message);
        }

    }

    @Override
    public void receiveMessage(String message) {
        if (game == null || message == null || !message.startsWith("ACTION:")) {
            return;
        }
        try {
            String[] parts = message.split(":");
            if (parts.length != 3) {
                return;
            }
            String action = parts[1];
            int clientId = Integer.parseInt(parts[2]);
            Playable player = game.getPlayers().stream().
                    filter(p -> p instanceof Player && ((Player) p).getId() == clientId).
                    findFirst().orElse(null);
            switch (action.toLowerCase()) {
                case "raise":
                    if (game.getGameType() == GameType.POKER) {
                        game.playerRaise(player);
                    }
                    break;
                case "fold":
                    if (game.getGameType() == GameType.POKER) {
                        game.playerFold(player);
                    }
                    break;
                case "hit":
                    if (game.getGameType() == GameType.BLACKJACK) {
                        game.playerHit(player);
                    }
                    break;
                case "stand":
                    if (game.getGameType() == GameType.BLACKJACK) {
                        game.playerStand(player);
                    }
                    break;
                default:
                    System.err.println("Unsupported action: " + action);
                    return;
            }
                game.broadcastState();
            }

        catch (Exception e) {
            System.err.println("Error processing message: " + message);
        }

    }

    // clienthandler to control the clients
    private class ClientHandler extends Thread {
        private Socket socket;
        private Server server;
        private PrintWriter out;
        private BufferedReader in;
        private int clientId;
        private volatile boolean isRunning;

        public ClientHandler(Socket socket, Server server, int clientId) {
            this.socket = socket;
            this.server = server;
            this.clientId = clientId;
            isRunning = false;
            try {
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            } catch (IOException e) {
                System.err.println("Error initializing ClientHandler for client " + clientId);
            }
        }

        @Override
        public void run() {
            isRunning = true;
            try {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    server.receiveMessage("ACTION:" + inputLine + ":" + clientId);
                }
            } catch (IOException e) {
                System.err.println("Error reading input from client " + clientId);

            } finally {
                close();
            }
        }

        public void sendMessage(String message) {
            out.println(message);
        }
        public void close() {
            try {
                isRunning = false;
                out.close();
                in.close();
                socket.close();
            } catch (IOException e) {
                System.err.println("Error closing client connection for client " + clientId);
            }
        }
    }
}

