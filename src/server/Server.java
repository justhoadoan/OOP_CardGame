package server;

import gamemode.GraphicMode;
import games.Game;
import games.GameType;
import playable.Playable;
import playable.Player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import static java.util.Locale.filter;

public class Server implements NetworkManager {
    private ServerSocket serverSocket;
    private Game game;
    private Map<Integer, ClientHandler> clientHandlers;
    private volatile boolean isRunning;
    private int nextClientId;
    
    public Server(int port, Game game) {
        try {
            this.nextClientId = 0;
            this.clientHandlers = new HashMap<>();
            serverSocket = new ServerSocket(port);
            this.game = game;
            isRunning = false;
            
            // Get local IP address
            String serverIp = getLocalIpAddress();
            System.out.println("Server started at IP: " + serverIp + ", Port: " + port);
            
            if (game instanceof GraphicMode) {
                javax.swing.JOptionPane.showMessageDialog(null, 
                    "Server started at IP: " + serverIp + ", Port: " + port, 
                    "Server Info", 
                    javax.swing.JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (IOException e) {
            System.err.println("Error initializing server on port " + port);
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize server", e);
        }
    }
    
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
            if (parts.length <= 3) {
                return;
            }
            String action = parts[1];
            int clientId = Integer.parseInt(parts[2]);
            Playable player = game.getPlayers().stream().
                    filter(p -> p instanceof Player && ((Player) p).getId() == clientId).
                    findFirst().orElse(null);
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

