package server;

public interface NetworkManager {
    // Connection management
    void start();
    void close();

    // Message sending
    void sendMessage(String message);
    void sendMessageToClient(int clientId, String message);

    // Message reception
    void receiveMessage(String message);
}
