package controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import security.Principal;
import security.SecurityContextHolder;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginFormController {
    public TextField txtUserName;
    public TextField txtPassword;
    public Button btnLogin;

    public void btnLoginOnAction(ActionEvent actionEvent) {
        if (isValidated()) {
            try {
                Connection connection = DBConnection.getInstance().getConnection();
                PreparedStatement stm = connection.prepareStatement("SELECT username, name, role FROM user WHERE username=? AND password=?");
                stm.setString(1, txtUserName.getText());
                stm.setString(2, txtPassword.getText());

                ResultSet rst = stm.executeQuery();

                String page = null;

                if (rst.next()) {
                    SecurityContextHolder.setPrincipal(new Principal(
                            txtUserName.getText(),
                            rst.getString("name"),
                            Principal.UserRole.valueOf(rst.getString("role"))));
                    if (rst.getString("role").equals("ADMIN")) {
                        page = "AdminHome.fxml";
                    } else {
                        page = "UserHome.fxml";
                    }
                    FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("/view/" + page));
                    AnchorPane root = fxmlLoader.load();
                    Scene scene = new Scene(root);

                    Stage primaryStage = (Stage) (btnLogin.getScene().getWindow());
                    primaryStage.setScene(scene);
                    primaryStage.setTitle("Student Attendance System: Home");
                    primaryStage.show();
                    Platform.runLater(() -> {
                        primaryStage.sizeToScene();
                        primaryStage.centerOnScreen();
                    });
                } else {
                    new Alert(Alert.AlertType.ERROR, "Invalid username or password").show();
                    txtUserName.requestFocus();
                    txtUserName.selectAll();
                }
            } catch (Exception e) {
                new Alert(Alert.AlertType.ERROR, "Something went wrong, Please try again").show();
                e.printStackTrace();
            }
        }
    }

    private boolean isValidated() {
        return !(txtUserName.getText().trim().length() < 4 || !txtUserName.getText().trim().matches("[A-Za-z0-9]+") || txtPassword.getText().trim().length() < 4);
    }
}
