package com.example.Controller;

import com.example.Models.MessageData;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.*;
import java.net.Socket;

public class ClientController {
    @FXML
    private Button btnConnect;
    @FXML private Button btnDisconnect;
    @FXML private Button btnSendClient;
    @FXML private Button btnSendImage;
    @FXML private TextArea txtClientArea;
    @FXML private TextField txtClientFiled;
    @FXML private TextField usernameField;
    @FXML private VBox messageContainer;

    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private String username;
    private Socket socket;

    public void initialize() {
        btnDisconnect.setDisable(true);
    }

    @FXML
    void connectToServer(ActionEvent event) {
        username = usernameField.getText().trim();
        if (username.isEmpty()) {
            showAlert("Please enter your username!");
            return;
        }

        new Thread(() -> {
            try {
                socket = new Socket("localhost", 4000);
                outputStream = new ObjectOutputStream(socket.getOutputStream());
                inputStream = new ObjectInputStream(socket.getInputStream());

                MessageData connectMessage = new MessageData(username, "connected", MessageData.MessageType.TEXT);
                outputStream.writeObject(connectMessage);
                outputStream.flush();

                Platform.runLater(() -> {
                    usernameField.setDisable(true);
                    btnConnect.setDisable(true);
                    btnDisconnect.setDisable(false);
                    txtClientArea.appendText("Connected to server!\n");
                });

                startMessageListener();
            } catch (IOException e) {
                Platform.runLater(() -> showAlert("Failed to connect to server"));
            }
        }).start();
    }

    @FXML
    void disconnect(ActionEvent event) {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
            Platform.runLater(() -> {
                usernameField.setDisable(false);
                btnConnect.setDisable(false);
                btnDisconnect.setDisable(true);
                txtClientArea.appendText("Disconnected from server\n");
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void sendClientSaveOnAction(ActionEvent event) {
        String message = txtClientFiled.getText().trim();
        if (!message.isEmpty()) {
            sendMessage(new MessageData(username, message, MessageData.MessageType.TEXT));
            txtClientFiled.clear();
        }
    }

    @FXML
    void sendImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif")
        );
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            try {
                byte[] fileData = new FileInputStream(file).readAllBytes();
                MessageData message = new MessageData(username, "📷 Image: " + file.getName(), MessageData.MessageType.IMAGE);
                message.setFileData(fileData);
                message.setFileName(file.getName());
                sendMessage(message);
            } catch (IOException e) {
                showAlert("Failed to send image");
            }
        }
    }

    private void sendMessage(MessageData message) {
        try {
            outputStream.writeObject(message);
            outputStream.flush();
        } catch (IOException e) {
            showAlert("Failed to send message");
        }
    }

    private void startMessageListener() {
        new Thread(() -> {
            try {
                while (true) {
                    MessageData message = (MessageData) inputStream.readObject();
                    Platform.runLater(() -> displayMessage(message));
                }
            } catch (Exception e) {
                Platform.runLater(() -> {
                    txtClientArea.appendText("\nDisconnected from server");
                    btnDisconnect.fire();
                });
            }
        }).start();
    }

    private void displayMessage(MessageData message) {
        if (message.getType() == MessageData.MessageType.IMAGE) {
            txtClientArea.appendText("\n" + message.getSender() + ": " + message.getContent());
            Image image = new Image(new ByteArrayInputStream(message.getFileData()));
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(200);
            imageView.setFitHeight(200);
            imageView.setPreserveRatio(true);
            Platform.runLater(() -> messageContainer.getChildren().add(imageView));
        } else {
            txtClientArea.appendText("\n" + message.getSender() + ": " + message.getContent());
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.showAndWait();
    }

}
