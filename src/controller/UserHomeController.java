package controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import security.SecurityContextHolder;

import java.io.IOException;

public class UserHomeController {
    public Label lblGreeting;
    public Button btnRecordAttendance;
    public Button btnSignOut;

    public void initialize(){
        lblGreeting.setText("Welcome "+SecurityContextHolder.getPrincipal().getName()+"!");
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

    public void btnSignOutOnAction(ActionEvent actionEvent) throws IOException {
        SecurityContextHolder.clear();
        AnchorPane root = FXMLLoader.load(this.getClass().getResource("/view/LoginForm.fxml"));
        Scene loginScene = new Scene(root);
        Stage stage = new Stage();
        stage.setScene(loginScene);
        stage.setTitle("Students Attendance System : Login");
        stage.setResizable(false);
        stage.show();

        Platform.runLater(()->{
            stage.sizeToScene();
            stage.centerOnScreen();
        });

        ((Stage)(lblGreeting.getScene().getWindow())).close();
    }

    public void btnUserProfileOnAction(ActionEvent actionEvent) {
    }

    public void btnViewReportsOnAction(ActionEvent actionEvent) {
    }

    public void recordAttendanceOnMouseClick(MouseEvent mouseEvent) {
        btnRecordAttendance.fire();
    }

    public void signOutOnMouseClick(MouseEvent mouseEvent) {
        btnSignOut.fire();
    }
}
