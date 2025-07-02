package com.menejementpj.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import com.menejementpj.App;
import com.menejementpj.components.CreateNews;
import com.menejementpj.components.GroupOptionPopUpController;
import com.menejementpj.components.UserMemberController;
import com.menejementpj.db.DatabaseManager;
import com.menejementpj.utils.Utils;
import com.menejementpj.model.User;
import com.menejementpj.model.Group; // Import the Group model
import com.menejementpj.test.Debug;

import java.io.IOException;
import java.sql.SQLException;
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
        App.setRoot("beranda", "Beranda - Management Project");
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
        Utils.logout(event);
    }

    @FXML
    void handleCreateProjectClick(ActionEvent event) {
        showPopupCreateNews(event);
    }

    @FXML
    void handleGroupOption(ActionEvent event) {
        System.out.println("DEBUG: handleGroupOption - Options button touched.");
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/menejementpj/components/group/groupOptionPopUp.fxml")); // Adjust path
            Parent root = loader.load();
            GroupOptionPopUpController controller = loader.getController();

            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setScene(new Scene(root));
            popupStage.setTitle("Edit Group");

            controller.setDialogStage(popupStage);
            controller.setParentController(this);
            controller.setGroupToEdit(App.mygroup);

            popupStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to open group options popup.");
        }
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
        int currentUserId = App.userSession.getCurrentLoggedInUserID();
        int currentGroupId = App.userSession.getCurrentLoggedInGroupID();

        if (currentUserId == -1) {
            Debug.warn("Cannot leave group: No user logged in.");
            showAlert(Alert.AlertType.WARNING, "Not Logged In", "Please log in to leave a group.");
            return;
        }

        if (currentGroupId == -1) {
            Debug.warn("Cannot leave group: No group selected in session.");
            showAlert(Alert.AlertType.WARNING, "No Group Selected", "You are not currently in a group to leave.");
            return;
        }

        try {
            boolean success = DatabaseManager.leaveGroup(currentGroupId, currentUserId);

            if (success) {
                Debug.success("User " + currentUserId + " successfully left group " + currentGroupId);
                App.userSession.setCurrentLoggedInGroupID(0);
                Debug.success("Current Group ID after leaving: " + App.userSession.getCurrentLoggedInGroupID());
                App.setRoot("groupTab", "Groups - My Groups");
            } else {
                Debug.error("Failed to leave group " + currentGroupId + " for user " + currentUserId);
                showAlert(Alert.AlertType.ERROR, "Failed to Leave",
                        "Could not leave the group. You might not be a member or an error occurred.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            Debug.error("Navigation error after leaving group: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Navigation Error",
                    "An error occurred while navigating away from the group page.");
        } catch (SQLException e) {
            e.printStackTrace();
            Debug.error("Database error while leaving group: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Database Error",
                    "A database error occurred while trying to leave the group.");
        }
    }

    public void refresWindow() {
        if (App.mygroup != null) {
            groupNama.setText(App.mygroup.nama);
            groupCreate.setText("Create At: " + App.mygroup.createAt);
            groupDescrib.setText(App.mygroup.describ);
            groupNews.setText(App.mygroup.news);
            Debug.success("sukser refres " + App.mygroup.news);
        } else {
            Debug.warn("refresWindow called but App.mygroup is null.");
            groupNama.setText("");
            groupCreate.setText("");
            groupDescrib.setText("");
            groupNews.setText("");
        }
    }

    public void refreshAllGroupData() {
        System.out.println("DEBUG: GroupPageController.refreshAllGroupData - START. currentLoggedInGroupID: "
                + App.userSession.getCurrentLoggedInGroupID());

        int currentGroupId = App.userSession.getCurrentLoggedInGroupID();
        if (currentGroupId > 0) {
            try {
                Group currentGroup = DatabaseManager.getGroupById(currentGroupId);
                if (currentGroup != null) {
                    App.mygroup = currentGroup;
                    Debug.success("App.mygroup updated with details for group ID: " + currentGroupId);
                } else {
                    Debug.warn("No group found in DB for ID: " + currentGroupId + ". Navigating to group list.");
                    App.userSession.setCurrentLoggedInGroupID(0);
                    try {
                        App.setRoot("groupTab", "Groups - My Groups");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return;
                }
            } catch (SQLException e) {
                e.printStackTrace();
                Debug.error("Database error fetching group details: " + e.getMessage());
                try {
                    App.setRoot("groupTab", "Groups - My Groups"); // Navigate to group list on DB error
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                return;
            }
        } else {
            Debug.warn("refreshAllGroupData called without a valid current group ID. Navigating to group list.");
            try {
                App.setRoot("groupTab", "Groups - My Groups");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        refresWindow();

        try {
            memberContainer.getChildren().clear();

            List<User> users = DatabaseManager.getUserMember();
            System.out.println(
                    "DEBUG: GroupPageController.refreshAllGroupData - Members received from DatabaseManager.getUserMember(): "
                            + (users != null ? users.size() : "null"));

            if (users == null || users.isEmpty()) {
                Debug.warn("No members found for group ID: " + currentGroupId + " in GroupPageController.");
            } else {
                for (User user : users) {
                    FXMLLoader loader = new FXMLLoader(
                            getClass().getResource("/com/menejementpj/components/group/groupMemberCard.fxml"));
                    Parent cardRoot = loader.load();
                    UserMemberController controller = loader.getController();

                    controller.setData(user.id, user.userName, user.role);
                    memberContainer.getChildren().add(cardRoot);
                }
            }
            Debug.success("GroupPageController.refreshAllGroupData - Member list loaded successfully for group ID: "
                    + currentGroupId);

        } catch (Exception e) {
            Debug.error("Error loading group members in refreshAllGroupData: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("DEBUG: GroupPageController.refreshAllGroupData - END.");
    }

    @FXML
    private void initialize() {

        refreshAllGroupData();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
