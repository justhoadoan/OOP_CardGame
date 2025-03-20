package server;

import gamemode.GameMode;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private GameMode gameMode;
    private int id;
    private Scanner scanner;
    private String playerHand;


}
