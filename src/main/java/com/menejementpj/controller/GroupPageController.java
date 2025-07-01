package com.menejementpj.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import com.menejementpj.App;
import com.menejementpj.components.CreateNews;
import com.menejementpj.components.UserMemberController;
import com.menejementpj.db.DatabaseManager;
import com.menejementpj.model.User;
import com.menejementpj.test.Debug;

import java.io.IOException;
import java.util.List;

public class GroupPageController {
    @FXML
    private Label groupNama;

    @FXML
    private Label groupCreate;

    @FXML
    private Label groupDescrib;

    @FXML
    private Label groupNews;

    @FXML
    private VBox memberContainer;

    @FXML
    private Button createProjectButton;

    @FXML
    private Button btnHome;

    @FXML
    private Button btnGroub;

    @FXML
    private Button btnProject;

    @FXML
    private ImageView b;

    @FXML
    private Button btnChat;

    @FXML
    private Button btnLogout;

    @FXML
    private Button createProjectButton1;

    @FXML
    void toggleGoToHome(ActionEvent event) throws IOException {
        App.setRoot("beranda", "\"Beranda - Management Project\"");
    }

    @FXML
    void toggleGoToGroub(ActionEvent event) throws IOException {
        App.setRoot("groupPage", "GroupPage - myGroup");
    }

    @FXML
    void toggleGotoProject(ActionEvent event) throws IOException {
        App.setRoot("projectTab", "Project - myGroup");
    }

    @FXML
    void toggleGotoChat(ActionEvent event) throws IOException {
        showPopupChat(event, "/com/menejementpj/chatPopUp.fxml", "Group Chat");
    }

    @FXML
    void toggleLogout(ActionEvent event) throws IOException {
        App.setRoot("login", "Login - Management Project");
    }

    @FXML
    void handleCreateProjectClick(ActionEvent event) {
        showPopupCreateNews(event);
    }

    private void showPopupChat(ActionEvent event, String fxmlFile, String title) {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();

            Scene popupScene = new Scene(root);

            String cssPath = "/com/menejementpj/css/chatMessage.css";
            popupScene.getStylesheets().add(getClass().getResource(cssPath).toExternalForm());

            Stage popupStage = new Stage();
            popupStage.setTitle(title);
            popupStage.setScene(popupScene);
            popupStage.initModality(Modality.NONE);
            popupStage.setResizable(false);

            Stage ownerStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            popupStage.initOwner(ownerStage);

            popupStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void showPopupCreateNews(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/menejementpj/components/group/groupNewsPopUp.fxml"));
            Parent root = loader.load();

            CreateNews childController = loader.getController();
            childController.setParentController(this);

            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setScene(new Scene(root));
            popupStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleLeave(ActionEvent event) {

    }

    public void refresWindow() {
        groupNama.setText(App.mygroup.nama);
        groupCreate.setText("Create At: " + App.mygroup.createAt);
        groupDescrib.setText(App.mygroup.describ);
        groupNews.setText(App.mygroup.news);
        Debug.success("sukser refres " + App.mygroup.news);
    }

    @FXML
    private void initialize() {
        refresWindow();

        try {
            List<User> users = DatabaseManager.getUserMember();

            for (User user : users) {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/com/menejementpj/components/group/groupMemberCard.fxml"));
                Parent cardRoot = loader.load();
                UserMemberController controller = loader.getController();

                controller.setData(user.id, user.userName, user.role);
                memberContainer.getChildren().add(cardRoot);
            }

        } catch (Exception e) {
            // TODO: handle exception
            Debug.error(e.getMessage());
        }
    }
}
