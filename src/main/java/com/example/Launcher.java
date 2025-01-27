package com.example;

import com.example.Controller.ServerConsole;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Launcher extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        new Thread(() -> {
            ServerConsole.main(new String[]{});
        }).start();

        Parent root = FXMLLoader.load(getClass().getResource("/clientform.fxml"));
        Scene scene = new Scene( root);
        Stage stage1 = new Stage();
        stage1.setScene(scene);
        stage.setTitle("Server");
        stage1.show();



    }


}
