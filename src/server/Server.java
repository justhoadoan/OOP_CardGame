package server;

import games.Game;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;

public class Server implements NetworkManager {
    private ServerSocket serverSocket;
    private Game game;
    Map<Integer, ClientHandler> clientHandlers;
    private volatile boolean isRunning;
    private int clientId;
    public Server(int port, Game game) {
        try {
            serverSocket = new ServerSocket(port);
            this.game = game;
            isRunning = false;
            clientId = 0;
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
                     ClientHandler clientHandler = new ClientHandler(clientSocket, this, clientId);
                     clientHandlers.put(clientId, clientHandler);
                     clientId++;
                     clientHandler.start();
                 } catch (IOException e) {
                     System.err.println("Error accepting client connection");
                 }
             }
         }).start();
        
    }

    @Override
    public void close() {

    }

    @Override
    public void sendMessage(String message) {

    }

    @Override
    public void sendMessageToClient(int clientId, String message) {

    }

    @Override
    public void receiveMessage(String message) {

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

