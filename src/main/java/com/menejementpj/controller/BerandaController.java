package com.menejementpj.controller;

import com.menejementpj.type.*;
import com.menejementpj.*;
import com.menejementpj.db.*;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class BerandaController {

    // FXML Injections from your UI

    @FXML
    private Label userNameLabel;
    @FXML
    private Label groupLabel;
    @FXML
    private ListView<Task> myListView;
    @FXML
    private GridPane projectGrid;

    @FXML
    public void initialize() {

        // --- Refactored User and Group Label Updates ---
        updateLabelFromDB(userNameLabel, App.getCurrentLoggedInUserID(), DatabaseManager::getUsername, "User",
                "User ID");
        updateLabelFromDB(groupLabel, App.getCurrentLoggedInGroupID(), DatabaseManager::getGroupName, "Group",
                "Group ID");

        // --- Task List Initialization ---
        ObservableList<Task> taskItems = FXCollections.observableArrayList(loadTasksFromDatabase());
        myListView.setCellFactory(listView -> new TaskCell());
        myListView.setItems(taskItems);

        // --- Project Grid Initialization ---
        populateProjectGrid();
    }

    @FXML
    private void handleNavigateToGroupPage() {
        try {
            // Use the static setRoot method from your App class to change the scene's
            // content
            // We also pass a new title for the window.
            App.setRoot("fxml/groupPage", "My Group");
        } catch (IOException e) {
            System.err.println("Failed to load groupPage.fxml");
            e.printStackTrace();
        }
    }

    /**
     * A reusable helper method to fetch data by ID and update a label.
     * Reduces code duplication.
     */
    private void updateLabelFromDB(Label label, int id, Function<Integer, String> dbFetcher, String labelName,
            String errorIdName) {
        if (id != -1) {
            String value = dbFetcher.apply(id); // Calls the appropriate method (e.g., DatabaseManager.getUsername)
            if (value != null && !value.isEmpty()) {
                label.setText(value);
            } else {
                label.setText(labelName + " ID: " + id);
                System.err.println(labelName + " not found in database for ID: " + id);
            }
        } else {
            label.setText("No " + errorIdName + " Assigned");
            System.err.println("No " + errorIdName + " found (ID is -1).");
        }
    }

    /**
     * Clears and populates the project grid with cards from the database.
     */
    private void populateProjectGrid() {
        projectGrid.getChildren().clear(); // Clear any existing content
        List<Project> projects = loadProjectsFromDatabase();

        int column = 0;
        int row = 0;

        try {
            for (Project project : projects) {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/com/menejementpj/components/projectCard.fxml"));
                Group projectCard = loader.load();

                // Find the labels inside the card by their fx:id
                Label titleLabel = (Label) projectCard.lookup("#projectTitleLabel");
                Label descriptionLabel = (Label) projectCard.lookup("#projectDescriptionLabel");
                Label dateLabel = (Label) projectCard.lookup("#projectDateLabel");

                // Populate the labels with data
                titleLabel.setText(project.getTitle());
                descriptionLabel.setText(project.getDescription());
                dateLabel.setText(project.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

                // Add the finished card to the grid
                projectGrid.add(projectCard, column, row);

                column++;
                if (column == 3) { // Reset after 3 columns
                    column = 0;
                    row++;
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to load projectCard.fxml");
            e.printStackTrace();
        }
    }

    /**
     * PLACEHOLDER: Fetches project data from the database.
     * Replace the sample data with your real database query.
     */
    private List<Project> loadProjectsFromDatabase() {
        // 1. Get the currently logged-in user's group ID.
        int loggedInGroupID = App.getCurrentLoggedInGroupID();
        if (loggedInGroupID == -1) {
            System.err.println("Cannot load projects, no group is assigned to the user.");
            return new ArrayList<>(); // Return an empty list
        }

        // 2. Call your new DatabaseManager method with the group ID.
        // This will return the list of projects for that group.
        return DatabaseManager.getProjects(loggedInGroupID);
    }

    /**
     * PLACEHOLDER: Fetches task data from the database.
     * Replace the sample data with your real database query.
     */
    private List<Task> loadTasksFromDatabase() {
        // This is the final list of Task objects the UI will use.
        List<Task> formattedTasks = new ArrayList<>();

        // 1. Get the currently logged-in user's ID.
        int loggedInUserId = App.getCurrentLoggedInUserID();
        if (loggedInUserId == -1) {
            System.err.println("Cannot load tasks, no user is logged in.");
            return formattedTasks; // Return an empty list
        }

        // 2. Call your actual database method.
        // This returns the List<TaskItem> from your DatabaseManager.
        List<TaskItem> itemsFromDb = DatabaseManager.getTaskItems(loggedInUserId);

        // 3. Loop through the results from the database and convert them.
        for (TaskItem dbItem : itemsFromDb) {
            try {
                // Convert the status string (e.g., "IN PROGRESS") to a TaskStatus enum
                // (TaskStatus.IN_PROGRESS)
                String statusString = dbItem.getStatus().toUpperCase().replace(" ", "_");
                TaskStatus status = TaskStatus.valueOf(statusString);

                // Get the task name
                String taskName = dbItem.getName();

                // Create the new Task object that our UI uses and add it to the list
                formattedTasks.add(new Task(taskName, status));

            } catch (IllegalArgumentException e) {
                System.err.println(
                        "Warning: Could not parse status '" + dbItem.getStatus() + "' from database. Skipping task.");
            }
        }

        // 4. Return the fully converted and formatted list.
        return formattedTasks;
    }
}

class TaskCell extends ListCell<Task> {
    private final VBox content = new VBox();
    private final Label nameLabel = new Label();
    private final Label statusLabel = new Label();

    public TaskCell() {
        super();
        content.getChildren().addAll(nameLabel, statusLabel);
        content.setSpacing(4);
    }

    @Override
    protected void updateItem(Task task, boolean empty) {
        super.updateItem(task, empty);
        if (empty || task == null) {
            setGraphic(null);
        } else {
            nameLabel.setText(task.getTaskName());
            statusLabel.setText(task.getStatus().toString().replace("_", " "));

            // Clear previous styles and apply the new one based on status
            statusLabel.getStyleClass().removeAll("status-completed", "status-in-progress", "status-pending",
                    "status-review", "status-not-started");
            switch (task.getStatus()) {
                case COMPLETED:
                    statusLabel.getStyleClass().add("status-completed");
                    break;
                case IN_PROGRESS:
                    statusLabel.getStyleClass().add("status-in-progress");
                    break;
                case PENDING:
                    statusLabel.getStyleClass().add("status-pending");
                    break;
                case REVIEW:
                    statusLabel.getStyleClass().add("status-review");
                    break;
                case NOT_STARTED:
                    statusLabel.getStyleClass().add("status-not-started");
                    break;
            }
            setGraphic(content);
        }
    }
}