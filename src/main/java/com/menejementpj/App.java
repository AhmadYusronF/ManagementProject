package com.menejementpj;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {

    private static Scene scene;

    private static Stage primaryStageInstance = null;

    private static int currentLoggedInHidden_uid = -1;
    private static int currentLoggedInUserID = -1;
    private static int currentLoggedInGroupID = -1;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStageInstance = stage;
        scene = new Scene(loadFXML("login"), 1280, 800);
  
        stage.setScene(scene);
        stage.setTitle("Login - Management Project");
        stage.setResizable(false);

        stage.show();
    }

    // static void setRoot(String fxml) throws IOException {
    // scene.setRoot(loadFXML(fxml));
    // }
    public static void setRoot(String fxml, String title) throws IOException {
        scene.setRoot(loadFXML(fxml));
        primaryStageInstance.setTitle(title); // Update the window title
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

    public static void setUserSession(int hiddenUid, int userId, int groupId) {
        currentLoggedInHidden_uid = hiddenUid;
        currentLoggedInUserID = userId;
        currentLoggedInGroupID = groupId;
    }

    // Getters for session information
    public static int getCurrentLoggedInHidden_uid() {
        return currentLoggedInHidden_uid;
    }

    public static int getCurrentLoggedInUserID() {
        return currentLoggedInUserID;
    }

    public static int getCurrentLoggedInGroupID() {
        return currentLoggedInGroupID;
    }

}