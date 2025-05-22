// Package declaration for the server
package server;

// Import statements for various classes and interfaces used in the server
import games.Game;
import games.GameType;
import playable.Playable;
import playable.Player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

// Import static method for filtering locales
import static java.util.Locale.filter;

// Server class implementing NetworkManager interface
public class Server implements NetworkManager {
    // Server socket to listen for client connections
    private ServerSocket serverSocket;
    // Game instance to manage game logic
    private Game game;
    // Map to store client handlers by client ID
    private Map<Integer, ClientHandler> clientHandlers;
    // Flag to indicate if the server is running
    private volatile boolean isRunning;
    // Counter for assigning client IDs
    private int nextClientId;

    // Constructor to initialize the server with a port and game instance
    public Server(int port, Game game) {
        try {
            this.nextClientId = 0;
            this.clientHandlers = new HashMap<>();
            serverSocket = new ServerSocket(port != 0 ? port : 8888);
            this.game = game;
            isRunning = false;

            // Get local IP address
            String serverIp = getLocalIpAddress();
            System.out.println("Server started at IP: " + serverIp + ", Port: " + port);

            javax.swing.JOptionPane.showMessageDialog(null,
                    "Server started at IP: " + serverIp + ", Port: " + port,
                    "Server Info",
                    javax.swing.JOptionPane.INFORMATION_MESSAGE);
        } catch (BindException e) {
            System.err.println("Port " + port + " is already in use. Please choose a different port.");
            throw new RuntimeException("Port already in use", e);
        } catch (IOException e) {
            System.err.println("Error initializing server on port " + port);
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize server", e);
        }
    }

    // Method to get the local IP address of the server
    private String getLocalIpAddress() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                // Skip loopback and virtual interfaces
                if (iface.isLoopback() || !iface.isUp() || iface.isVirtual() || iface.isPointToPoint())
                    continue;

                Enumeration<InetAddress> addresses = iface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    // Only return IPv4 addresses
                    if (addr.getAddress().length == 4) {
                        return addr.getHostAddress();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Fallback to localhost if no IP found
        return "127.0.0.1";
    }

    // Method to set the game instance
    public void setGame(Game game) {
        this.game = game;
    }

    // Method to start the server
    @Override
    public void start() {
        isRunning = true;
        new Thread(() -> {
            while (isRunning) {
                try {
                    // Accept client connections
                    Socket clientSocket = serverSocket.accept();
                    int clientId = nextClientId++;
                    ClientHandler clientHandler = new ClientHandler(clientSocket, this, clientId);
                    clientHandlers.put(clientId, clientHandler);
                    clientHandler.start();
                    System.out.println("Client " + clientId + " connected");
                    if (game != null) {
                        Player newPlayer = new Player("Client-" + clientId, clientId);
                        game.addPlayer(newPlayer);
                        clientHandler.sendMessage("STATE:" + game.getPublicState());
                    }
                } catch (IOException e) {
                    if (isRunning) {
                        System.err.println("Accept failed: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    // Method to close the server and all client connections
    @Override
    public void close() {
        try {
            for (ClientHandler clientHandler : clientHandlers.values()) {
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

    // Method to send a message to all clients
    @Override
    public void sendMessage(String message) {
        for (ClientHandler clientHandler : clientHandlers.values()) {
            clientHandler.sendMessage(message);
        }
    }

    // Method to send a message to a specific client by ID
    @Override
    public void sendMessageToClient(int clientId, String message) {
        ClientHandler handler = clientHandlers.get(clientId);
        if (handler != null) {
            handler.sendMessage(message);
        }
    }

    // Method to receive and process messages from clients
    @Override
    public void receiveMessage(String message) {
        if (game == null || message == null || !message.startsWith("ACTION:")) {
            return;
        }
        try {
            String[] parts = message.split(":");
            if (parts.length <= 3) {
                return;
            }
            String action = parts[1];
            int clientId = Integer.parseInt(parts[2]);
            Playable player = game.getPlayers().stream()
                    .filter(p -> p instanceof Player && ((Player) p).getId() == clientId)
                    .findFirst().orElse(null);
            if (player == null) {
                System.err.println("Player not found: " + clientId);
            }
            switch (action.toLowerCase()) {
                case "raise":
                    if (game.getGameType() == GameType.POKER) {
                        int raiseAmount = Integer.parseInt(parts[3]);
                        game.playerRaise(player, raiseAmount);
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
        } catch (Exception e) {
            System.err.println("Error processing message: " + message);
        }
    }

    // Inner class to handle client connections
    private class ClientHandler extends Thread {
        private Socket socket;
        private Server server;
        private PrintWriter out;
        private BufferedReader in;
        private int clientId;
        private volatile boolean isRunning;

        // Constructor to initialize the client handler
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

        // Method to run the client handler thread
        // Java
        @Override
        public void run() {
            isRunning = true;
            try {
                // Send the client ID as the first message
                sendMessage(String.valueOf(clientId)); // Ensure this is a valid integer

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

        // Method to send a message to the client
        public void sendMessage(String message) {
            out.println(message);
        }

        // Method to close the client connection
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