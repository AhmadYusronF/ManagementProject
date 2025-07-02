package com.menejementpj;

import com.menejementpj.auth.Session;
import com.menejementpj.auth.UserData;
import com.menejementpj.model.Group;
import com.menejementpj.auth.GroupData;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {

    private static Scene scene;

    private static Stage primaryStageInstance = null;

    public static GroupData group = new GroupData();
    public static Session userSession = new Session();
    public static UserData user = new UserData();
    public static Group mygroup = new Group("", "", null, "", 0);

    @Override
    public void start(Stage stage) throws IOException {
        primaryStageInstance = stage;
        scene = new Scene(loadFXML("login"), 1280, 800);

        stage.setScene(scene);
        stage.setTitle("Login - Management Project");
        stage.setResizable(false);

        stage.show();
    }

    public static void setRoot(String fxml, String title) throws IOException {
        scene.setRoot(loadFXML(fxml));
        primaryStageInstance.setTitle(title);
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static Parent loadFXMLalter(String fxmlName) throws IOException {
        return FXMLLoader.load(App.class.getResource("/com/menejementpj/" + fxmlName + ".fxml"));
    }

    public static void main(String[] args) {
        launch();
    }

}