package com.example.Models;

import com.example.Controller.ServerConsole;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class MangeClient implements Runnable{
    private Socket socket;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private String clientName;
    private ServerConsole server;

    public MangeClient(Socket socket, ServerConsole server) {
        this.socket = socket;
        this.server = server;
        try {
            this.outputStream = new ObjectOutputStream(socket.getOutputStream());
            this.inputStream = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {

            MessageData clientData = (MessageData) inputStream.readObject();
            this.clientName = clientData.getSender();
            server.broadcast(new MessageData("Server", clientName + " has joined the chat recently!!", MessageData.MessageType.TEXT));


            while (true) {
                MessageData message = (MessageData) inputStream.readObject();
                server.broadcast(message);
            }
        } catch (Exception e) {
            server.removeClient(this);
            try {
                server.broadcast(new MessageData("Server", clientName + " has left the chat!!!", MessageData.MessageType.TEXT));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void sendMessage(MessageData message) throws IOException {
        outputStream.writeObject(message);
        outputStream.flush();
    }

    public String getClientName() {
        return clientName;
    }

}
