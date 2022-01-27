package controller;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import util.DBConnection;

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
                Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/student_attendance_system", "root", "mysql");

                Platform.runLater(() -> lblStatus.setText("Setting up the UI"));
                sleep(100);

                Platform.runLater(()->{
                    loadLoginForm(connection);
                });

            }catch (SQLException  | ClassNotFoundException e) {
                if (e instanceof SQLException && ((SQLException)e).getSQLState().equals("42000")) {
                    Platform.runLater(this::loadImportDBForm);
                } else {
                    shutDownApp(e);
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
            stage.setOnCloseRequest(event -> {
                event.consume();
            });
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

                        Platform.runLater(() -> {
                            lblStatus.setText("Setting up the UI");
                            sleep(100);
                            loadCreateAdminForm();
                        });
                    } catch (IOException | SQLException e) {
                        if (e instanceof SQLException) {
                            dropDatabase();
                        }
                        shutDownApp(e);
                    }
                }).start();
            } else {
                /*Todo : Restore the backup*/
            }
        } catch (IOException e) {
            shutDownApp(e);
        }
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void loadCreateAdminForm() {
        try {
            Stage stage = new Stage();
            AnchorPane root = FXMLLoader.load(this.getClass().getResource("/view/CreateAdminForm.fxml"));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Student Attendance System : Create Admin Form");
            stage.setResizable(false);
            stage.centerOnScreen();
            stage.sizeToScene();
            stage.show();
            ((Stage) lblStatus.getScene().getWindow()).close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadLoginForm(Connection connection) {
        DBConnection.getInstance().init(connection);

        try {
            Stage stage = new Stage();
            AnchorPane root = FXMLLoader.load(this.getClass().getResource("/view/LoginForm.fxml"));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Student Attendance System : Login Form");
            stage.setResizable(false);
            stage.centerOnScreen();
            stage.sizeToScene();
            stage.show();

            ((Stage)lblStatus.getScene().getWindow()).close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void shutDownApp(Throwable t) {
        Platform.runLater(()->lblStatus.setText("Failed to initialize the app"));

        if (t!=null) {
            t.printStackTrace();
        }

        System.exit(1);
    }

    private void dropDatabase(){
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306", "root", "mysql");
            Statement stm = connection.createStatement();
            stm.execute("DROP DATABASE IF EXISTS dep8_student_attendance");
            connection.close();
        } catch (SQLException e) {
            shutDownApp(e);
        }
    }
}
