package controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import util.DBConnection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CreateAdminFormController {
    public TextField txtName;
    public TextField txtUsername;
    public Rectangle rctOne;
    public Rectangle rctTwo;
    public Rectangle rctThree;
    public Rectangle rctFour;
    public Rectangle rctFive;
    public PasswordField txtPassword;
    public PasswordField txtConfirmPassword;
    public Button btnCreateAccount;

    public void initialize () {
        txtPassword.textProperty().addListener((observable, oldValue, newValue) -> {
            rctOne.setVisible(newValue.trim().length()>=1?true:false);
            rctTwo.setVisible(newValue.trim().length()>=2?true:false);
            rctThree.setVisible(newValue.trim().length()>=3?true:false);
            rctFour.setVisible(newValue.trim().length()>=4?true:false);
            rctFive.setVisible(newValue.trim().length()>=5?true:false);
        });
    }

    public void btnCreateAccountOnAction(ActionEvent actionEvent) {
        try {
            if (isValidated()) {
                Connection connection = DBConnection.getInstance().getConnection();
                PreparedStatement stm = connection.prepareStatement("INSERT INTO user(username, name, password, role) VALUES (?,?,?,?)");
                stm.setString(1, txtUsername.getText());
                stm.setString(2, txtName.getText());
                stm.setString(3, txtPassword.getText());
                stm.setString(4, "ADMIN");

                int affectedRows = stm.executeUpdate();

                if (affectedRows != 1) {

                }

                AnchorPane root = FXMLLoader.load(this.getClass().getResource("/view/LoginForm.fxml"));
                Scene loginScene = new Scene(root);
                Stage primaryStage = new Stage();
                primaryStage.setScene(loginScene);
                Platform.runLater(()->primaryStage.sizeToScene());
                primaryStage.setResizable(false);
                primaryStage.centerOnScreen();
                primaryStage.show();

                ((Stage)btnCreateAccount.getScene().getWindow()).close();
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isValidated () {
        String name = txtName.getText();
        String userName = txtUsername.getText();
        String password = txtPassword.getText();
        String confirmPassword = txtConfirmPassword.getText();

        if (!name.matches("[A-Za-z]+")) {
            new Alert(Alert.AlertType.ERROR, "Please enter a valid Name").show();
            txtName.selectAll();
            txtName.requestFocus();
            return false;
        } else if (userName.length()<4) {
            new Alert(Alert.AlertType.ERROR, "Username should have at least 4 characters").show();
            txtUsername.selectAll();
            txtUsername.requestFocus();
            return false;
        } else if (!userName.matches("[A-Za-z0-9]+")) {
            new Alert(Alert.AlertType.ERROR, "Please enter a valid username").show();
            txtUsername.selectAll();
            txtUsername.requestFocus();
            return false;
        } else if (password.length()<4) {
            new Alert(Alert.AlertType.ERROR, "Password must be at least 4 characters").show();
            txtPassword.selectAll();
            txtPassword.requestFocus();
            return false;
        } else if (!password.equals(confirmPassword)) {
            new Alert(Alert.AlertType.ERROR, "Password mismatch").show();
            txtConfirmPassword.selectAll();
            txtConfirmPassword.requestFocus();
            return false;
        }
        return true;
    }
}
