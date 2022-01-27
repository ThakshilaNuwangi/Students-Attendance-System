package controller;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SplashScreenController {
    public Label lblStatus;

    public void initialize() {
        establishDBConnection();
    }

    private void establishDBConnection() {
        new Thread(() -> {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                DriverManager.getConnection("jdbc:mysql://localhost:3306/student_attendance_system", "root", "mysql");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                if (e.getSQLState().equals("42000")) {
                    Platform.runLater(() -> loadImportDBForm());
                }
            }
        }).start();

    }

    private void loadImportDBForm() {
        SimpleObjectProperty<File> fileProperty = new SimpleObjectProperty<>();
        try {
            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("/view/ImportDBForm.fxml"));
            AnchorPane root = fxmlLoader.load();
            ImportDBFormController controller = fxmlLoader.getController();
            controller.initFileProperty(fileProperty);
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.sizeToScene();
            stage.setTitle("Student Attendance System : First time boot");
            stage.setResizable(false);
            stage.centerOnScreen();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(lblStatus.getScene().getWindow());
            stage.showAndWait();

            if (fileProperty.getValue() == null) {
                /*Create new database*/
                lblStatus.setText("Creating a new Database");

                new Thread(() -> {
                    try {
                        sleep(100);
                        Platform.runLater(() -> lblStatus.setText("Loading Database script"));
                        InputStream inputStream = this.getClass().getResourceAsStream("/assets/db-script.sql");
                        byte[] buffer = new byte[inputStream.available()];
                        inputStream.read(buffer);
                        String script = new String(buffer);

                        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306?allowMultiQueries=true", "root", "mysql");
                        Platform.runLater(() -> lblStatus.setText("Executing Database Script"));
                        Statement statement = connection.createStatement();
                        statement.execute(script);
                        connection.close();
                        sleep(100);

                        Platform.runLater(() -> lblStatus.setText("Obtaining a new Database connection"));
                        connection = DriverManager.getConnection("jdbc:mysql://localhost:3306", "root", "mysql");
                        sleep(100);

                        Platform.runLater(() -> lblStatus.setText("Setting up the UI"));
                    } catch (IOException | SQLException e) {
                        e.printStackTrace();
                    }
                }).start();
            } else {
                /*Todo : Restore the backup*/
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
