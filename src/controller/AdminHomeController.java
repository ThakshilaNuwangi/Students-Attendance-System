package controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import security.SecurityContextHolder;

import java.io.IOException;

public class AdminHomeController {
    public Label lblGreeting;
    public Button btnRecordAttendance;
    public Button btnViewReports;
    public Button btnUserProfiles;
    public Button btnUserManagement;
    public Button btnBackup;
    public Button btnSignOut;
    public AnchorPane root;

    public void initialize() {
        lblGreeting.setText("Welcome " + SecurityContextHolder.getPrincipal().getName() + "!");

        root.setOnKeyReleased(event -> {
            switch (event.getCode()){
                case F1:
                    btnRecordAttendance.fire();
                    break;
                case F6:
                    btnSignOut.fire();
                    break;
                case F3:
                    btnUserProfiles.fire();
                    break;
                case F2:
                    btnViewReports.fire();
                    break;
                case F4:
                    btnUserManagement.fire();
                    break;
                case F5:
                    btnBackup.fire();
                    break;
            }
        });
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

    public void btnUserProfilesOnAction(ActionEvent actionEvent) throws IOException {
        AnchorPane root = FXMLLoader.load(this.getClass().getResource("/view/UserProfileForm.fxml"));
        Scene userProfileScene = new Scene(root);
        Stage stage = new Stage();
        stage.setScene(userProfileScene);
        stage.setTitle("Students Attendance System : User Profile");
        stage.setResizable(false);
        stage.initOwner(lblGreeting.getScene().getWindow());
        stage.show();

        Platform.runLater(() -> {
            stage.sizeToScene();
            stage.centerOnScreen();
        });

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

    public void btnBackupOnAction(ActionEvent actionEvent) throws IOException {
        AnchorPane root = FXMLLoader.load(this.getClass().getResource("/view/BackupAndRestoreForm.fxml"));
        Scene recordAttendanceScene = new Scene(root);
        Stage stage = new Stage();
        stage.setScene(recordAttendanceScene);
        stage.setTitle("Students Attendance System : Backup and Restore");
        stage.setResizable(false);
        stage.initOwner(lblGreeting.getScene().getWindow());
        stage.show();

        Platform.runLater(() -> {
            stage.sizeToScene();
            stage.centerOnScreen();
        });
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