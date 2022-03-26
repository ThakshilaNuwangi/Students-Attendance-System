package controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import security.SecurityContextHolder;

import java.io.IOException;

public class AdminHomeController {
    public Label lblGreeting;

    public void initialize() {
        lblGreeting.setText("Welcome " + SecurityContextHolder.getPrincipal().getName() + "!");
    }

    public void btnRecordAttendanceOnAction(ActionEvent actionEvent) throws IOException {
        AnchorPane root = FXMLLoader.load(this.getClass().getResource("/view/RecordAttendanceForm.fxml"));
        Scene recordAttendanceScene = new Scene(root);
        Stage stage = new Stage();
        stage.setScene(recordAttendanceScene);
        stage.setTitle("Students Attendance System : Record Attendance");
        stage.setResizable(false);
        stage.initOwner(lblGreeting.getScene().getWindow());
        stage.show();

        Platform.runLater(() -> {
            stage.sizeToScene();
            stage.centerOnScreen();
        });
    }

    public void btnViewReportsOnAction(ActionEvent actionEvent) {
    }

    public void btnUserProfilesOnAction(ActionEvent actionEvent) {
    }

    public void btnUserManagementOnAction(ActionEvent actionEvent) throws IOException {
        AnchorPane root = FXMLLoader.load(this.getClass().getResource("/view/UserManagementForm.fxml"));
        Scene recordAttendanceScene = new Scene(root);
        Stage stage = new Stage();
        stage.setScene(recordAttendanceScene);
        stage.setTitle("Students Attendance System : User Management");
        stage.setResizable(false);
        stage.initOwner(lblGreeting.getScene().getWindow());
        stage.show();

        Platform.runLater(() -> {
            stage.sizeToScene();
            stage.centerOnScreen();
        });
    }

    public void btnBackupOnAction(ActionEvent actionEvent) {
    }

    public void btnSignOutOnAction(ActionEvent actionEvent) throws IOException {
        SecurityContextHolder.clear();
        AnchorPane root = FXMLLoader.load(this.getClass().getResource("/view/LoginForm.fxml"));
        Scene loginScene = new Scene(root);
        Stage stage = new Stage();
        stage.setScene(loginScene);
        stage.setTitle("Students Attendance System : Login");
        stage.setResizable(false);
        stage.show();

        Platform.runLater(() -> {
            stage.sizeToScene();
            stage.centerOnScreen();
        });

        ((Stage)(lblGreeting.getScene().getWindow())).close();
    }
}