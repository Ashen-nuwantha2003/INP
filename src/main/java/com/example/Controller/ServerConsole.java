package com.example.Controller;

import com.example.Models.MangeClient;
import com.example.Models.MessageData;
import javafx.application.Platform;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;


public class ServerConsole {
    private static final int PORT = 4000;
    private CopyOnWriteArrayList<MangeClient> clients = new CopyOnWriteArrayList<>();

    public static void main(String[] args) {
        new ServerConsole().start();
    }


    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started on port " + PORT);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected");

                MangeClient clientHandler = new MangeClient(socket, this);
                clients.add(clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void broadcast(MessageData message) throws IOException {
        for (MangeClient client : clients) {
            try {
                client.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void removeClient(MangeClient clientHandler) {
        clients.remove(clientHandler);
    }


}
