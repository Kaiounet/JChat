package com.kaiounet;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import io.github.cdimascio.dotenv.Dotenv;

public class ChatServer {
    private static final Dotenv dotenv = Dotenv.load();
    private static final int SERVER_PORT = Integer.parseInt(dotenv.get("SERVER_PORT"));
    private static final String SERVER_HOST = dotenv.get("SERVER_HOST");
    private static final int BACKLOG = 50;
    private static final InetAddress ADDRESS;
    private static List<ClientHandler> clients = new ArrayList<>();

    static {
        try {
            ADDRESS = InetAddress.getByName(SERVER_HOST);
            System.out.println("Server will bind to: " + ADDRESS.getHostAddress());
        } catch (UnknownHostException e) {
            System.err.println("Invalid SERVER_HOST: " + SERVER_HOST);
            throw new RuntimeException("Failed to resolve server host", e);
        }
    }

    public static void main(String[] args) {

        System.out.println("Chat Server starting on port " + SERVER_PORT + "...");

        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT, BACKLOG, ADDRESS)) {
            System.out.println("Server is running. Waiting for clients...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress());

                ClientHandler clientHandler = new ClientHandler(clientSocket, clients);
                clients.add(clientHandler);

                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }
}
