package com.menejementpj.controller;

import java.io.IOException;

import com.menejementpj.App;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import com.menejementpj.test.Debug;
import java.util.Map;
import com.menejementpj.auth.UserData;
import com.menejementpj.components.ActivityLogCardController;
import com.menejementpj.db.DatabaseManager;
import com.menejementpj.model.ActivityLog;
import com.menejementpj.model.MemberTask;
import com.menejementpj.model.Project;
import com.menejementpj.model.Task;
import com.menejementpj.utils.Utils;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class BerandaController {
    @FXML
    private Label groubNews;

    @FXML
    private VBox activityLogContainer;

    @FXML
    private Label welcomeUser;

    @FXML
    private VBox taskContainer;

    @FXML
    private ImageView b;

    @FXML
    private Button btnChat;

    @FXML
    private Button btnGroub;

    @FXML
    private Button btnHome;

    @FXML
    private Button btnLogout;

    @FXML
    private Button btnProject;

    @FXML
    private StackPane myStackPane;

    @FXML
    void toggleLogout(ActionEvent event) throws IOException {
        Utils.logout();
    }

    @FXML
    void toggleGoToGroub(ActionEvent event) throws IOException {
        int currentGroupIdInSession = App.userSession.getCurrentLoggedInGroupID();
        System.out.println(
                "DEBUG: BerandaController.toggleGoToGroub - Current Group ID in session: " + currentGroupIdInSession);

        if (currentGroupIdInSession > 0) { // If a valid group ID is set
            System.out.println("DEBUG: BerandaController.toggleGoToGroub - Navigating to groupPage for ID: "
                    + currentGroupIdInSession);
            App.setRoot("components/group/groupPage", "GroubPage - myGroub");
        } else {
            System.out.println(
                    "DEBUG: BerandaController.toggleGoToGroub - No valid group ID in session. Navigating to groupTab.");
            App.setRoot("groupTab", "Group - join or create");
        }
    }

    @FXML
    void toggleGotoChat(ActionEvent event) {
        showPopup(event, "/com/menejementpj/chatPopUp.fxml", "Group Chat");
    }

    @FXML
    void toggleGotoProject(ActionEvent event) throws IOException {
        App.setRoot("projectTab", "Project");
    }

    @FXML
    void goToHome(ActionEvent event) {

    }

    @FXML
    private void initialize() {
        welcomeUser.setText("Welcome, " + App.user.getUsername()); //
        groubNews.setText(App.mygroup.news); //

        // This map will hold all tasks, grouped by the user they are assigned to.
        Map<UserData, List<MemberTask>> allTasksByUser = new LinkedHashMap<>();
        List<ActivityLog> allLogs = new ArrayList<>();

        try {
            // Get all projects for the current group.
            List<Project> projects = DatabaseManager.getProjectGrup(); //

            // Loop through each project to collect tasks and logs.
            if (projects != null) {
                for (Project p : projects) {
                    // Get all tasks for this project, already grouped by user.
                    Map<UserData, List<MemberTask>> tasksInProject = DatabaseManager.getTasksGroupedByMember(p.id); //

                    // Merge the tasks from this project into our main map.
                    for (Map.Entry<UserData, List<MemberTask>> entry : tasksInProject.entrySet()) {
                        UserData user = entry.getKey();
                        List<MemberTask> tasks = entry.getValue();
                        allTasksByUser.computeIfAbsent(user, k -> new ArrayList<>()).addAll(tasks); //
                    }

                    // Collect all activity logs.
                    allLogs.addAll(DatabaseManager.getActivityLogs(p.id)); //
                }
            }

            // --- Create Member Task Cards ---
            taskContainer.getChildren().clear(); // Clear existing cards.
            // Now, create one card for each user with their complete list of tasks.
            for (Map.Entry<UserData, List<MemberTask>> entry : allTasksByUser.entrySet()) {
                // We only want to display the tasks for the currently logged-in user.
                if (entry.getKey().getUserId() == App.userSession.getCurrentLoggedInUserID()) { //
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource(
                                "/com/menejementpj/components/project/memberTaskCard.fxml")); //
                        VBox cardNode = loader.load(); //
                        MemberTaskCardController controller = loader.getController(); //

                        controller.setData(entry.getKey().getUsername(), entry.getValue()); // Set user's name and all
                                                                                            // their tasks.
                        taskContainer.getChildren().add(cardNode); //
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            // --- Display Activity Logs ---
            activityLogContainer.getChildren().clear(); // Clear existing logs.
            if (allLogs.isEmpty()) {
                Debug.warn("Tidak ada Log yang ditemukan."); //
            } else {
                for (ActivityLog log : allLogs) {
                    FXMLLoader loader = new FXMLLoader(
                            getClass().getResource("/com/menejementpj/components/project/activityLogCard.fxml")); //
                    Parent logRoot = loader.load(); //
                    ActivityLogCardController controller = loader.getController(); //
                    controller.setData(log); //
                    activityLogContainer.getChildren().add(logRoot); //
                }
                Debug.success("Semua activity log berhasil ditampilkan!"); //
            }

        } catch (Exception e) {
            Debug.error("Gagal memuat data Beranda: " + e.getMessage()); // This is a general catch-all for robustness.
            e.printStackTrace();
        }
    }

    private void showPopup(ActionEvent event, String fxmlFile, String title) {
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
}
