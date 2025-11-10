package com.kaiounet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

public class ClientHandler implements Runnable {
    private Socket socket;
    private List<ClientHandler> clients;
    private PrintWriter out;
    private BufferedReader in;
    private String username;

    public ClientHandler(Socket socket, List<ClientHandler> clients) {
        this.socket = socket;
        this.clients = clients;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // First message from client is the username
            username = in.readLine();
            System.out.println(username + " has joined the chat.");
            broadcast(username + " has joined the chat.", this);

            // Listen for messages
            String message;
            while ((message = in.readLine()) != null) {
                // In case you want to debug and output the message in the server's output
                // System.out.println(username + ": " + message);
                broadcast(username + ": " + message, this);
            }
        } catch (IOException e) {
            System.err.println("Error handling client: " + e.getMessage());
        } finally {
            disconnect();
        }
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    private void broadcast(String message, ClientHandler sender) {
        for (ClientHandler client : clients) {
            if (client != sender) {
                client.sendMessage(message);
            }
        }
    }

    private void disconnect() {
        try {
            clients.remove(this);
            if (username != null) {
                System.out.println(username + " has left the chat.");
                broadcast(username + " has left the chat.", this);
            }
            socket.close();
        } catch (IOException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }
}
