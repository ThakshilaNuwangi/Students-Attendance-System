package controller;

import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import security.SecurityContextHolder;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserProfileFormController {
    public TextField txtUsername;
    public TextField txtName;
    public TextField txtPassword;
    public TextField txtRole;
    public Button btnChangePassword;
    public Button btnSaveChanges;
    public Button btnReset;

    public void initialize() {
        setInitialValues();
    }

    public void btnChangePasswordOnAction(ActionEvent actionEvent) {
        txtPassword.setEditable(true);
        txtPassword.requestFocus();
    }

    public void btnSaveChangesOnAction(ActionEvent actionEvent) {
        Connection connection = DBConnection.getInstance().getConnection();
        try {
            PreparedStatement stm = connection.prepareStatement("UPDATE user SET  password=? WHERE username=?");
            stm.setString(1, txtPassword.getText());
            stm.setString(2,txtUsername.getText());
            int affectedRows = stm.executeUpdate();
            if (affectedRows!=1){
                new Alert(Alert.AlertType.ERROR, "Please try again").show();
            } else {
                new Alert(Alert.AlertType.CONFIRMATION, "Password changed successfully").show();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void btnResetOnAction(ActionEvent actionEvent) {
        setInitialValues();
    }

    private void setInitialValues(){
        Connection connection = DBConnection.getInstance().getConnection();
        try {
            PreparedStatement stm = connection.prepareStatement("SELECT * FROM user WHERE username=?");
            stm.setString(1, SecurityContextHolder.getPrincipal().getUsername());
            ResultSet rst = stm.executeQuery();
            while (rst.next()){
                txtUsername.setText(rst.getString("username"));
                txtName.setText(rst.getString("name"));
                txtPassword.setText(rst.getString("password"));
                txtRole.setText(rst.getString("role"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
