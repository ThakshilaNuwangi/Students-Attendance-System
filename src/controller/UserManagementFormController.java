package controller;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import util.DBConnection;
import util.UserTM;

import java.sql.*;

public class UserManagementFormController {
    public TextField txtUsername;
    public TextField txtName;
    public PasswordField pwdPassword;
    public RadioButton rdoAdmin;
    public ToggleGroup role;
    public RadioButton rdoUser;
    public TableView<UserTM> tblUsers;
    public TableColumn colUsername;
    public TableColumn colName;
    public TableColumn colPassword;
    public TableColumn colRole;
    public TableColumn colOption;
    public Button btnReset;
    public Button btnSave;
    private UserTM selectedUser;

    public void initialize() {
        Connection connection = DBConnection.getInstance().getConnection();
        loadAllUsers();

        tblUsers.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("username"));
        tblUsers.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("name"));
        tblUsers.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("password"));
        tblUsers.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("role"));

        TableColumn<UserTM, Button> lastCol = (TableColumn<UserTM, Button>) tblUsers.getColumns().get(3);
        lastCol.setCellValueFactory(param -> {
            Button btnDelete = new Button("Delete");
            btnDelete.setOnAction(event -> {
                try {
                    PreparedStatement stm = connection.prepareStatement("DELETE FROM user WHERE username=?");
                    stm.setString(1, param.getValue().getUsername());
                    int res = stm.executeUpdate();
                    if (res!=1){
                        new Alert(Alert.AlertType.ERROR, "Could not the delete the user, please try again").show();
                    } else {
                        new Alert(Alert.AlertType.CONFIRMATION, "Deleted Successfully").show();
                    }
                    loadAllUsers();
                } catch (SQLIntegrityConstraintViolationException e) {
                    new Alert(Alert.AlertType.ERROR, "Please remove the attendance records saved by user before remove the user").show();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
            return new ReadOnlyObjectWrapper<>(btnDelete);
        });

        tblUsers.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue!=null) {
                selectedUser = new UserTM(newValue.getUsername(), newValue.getName(), newValue.getPassword(), newValue.getRole());
                txtUsername.setText(newValue.getUsername());
                txtName.setText(newValue.getName());
                pwdPassword.setText(newValue.getPassword());
                if (newValue.getRole() == "ADMIN") {
                    rdoAdmin.setSelected(true);
                } else {
                    rdoUser.setSelected(true);
                }
                btnSave.setText("Update");
            }
        });

    }

    public void btnResetOnAction(ActionEvent actionEvent) {
        txtUsername.clear();
        txtName.clear();
        pwdPassword.clear();
        rdoUser.setSelected(false);
        rdoAdmin.setSelected(false);
    }

    public void btnSaveOnAction(ActionEvent actionEvent) {
        if (!isValidated()) {
            return;
        }

        Connection connection = DBConnection.getInstance().getConnection();
        if (btnSave.getText().equals("Save")) {
            try {
                PreparedStatement stm = connection.prepareStatement("INSERT INTO user(username, name, password, role) VALUES (?,?,?,?)");
                stm.setString(1, txtUsername.getText());
                stm.setString(2, txtName.getText());
                stm.setString(3, pwdPassword.getText());
                stm.setString(4, rdoUser.isSelected() ? "USER" : "ADMIN");
                int affectedRows = stm.executeUpdate();

                if (affectedRows!=1) {
                    new Alert(Alert.AlertType.ERROR, "Please try again").show();
                } else {
                    new Alert(Alert.AlertType.CONFIRMATION, "User saved successfully").show();
                }
                loadAllUsers();
            } catch (SQLIntegrityConstraintViolationException e) {
                new Alert(Alert.AlertType.ERROR, "Username already exists, Please select a different username").show();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            try {
                PreparedStatement stm = connection.prepareStatement("UPDATE user SET username=?, name=?, password=?, role=? WHERE username=?");
                stm.setString(1, txtUsername.getText());
                stm.setString(2, txtName.getText());
                stm.setString(3, pwdPassword.getText());
                stm.setString(4, rdoUser.isSelected() ? "USER" : "ADMIN");
                stm.setString(5, selectedUser.getUsername());
                int affectedRows = stm.executeUpdate();
                if (affectedRows!=1) {
                    new Alert(Alert.AlertType.ERROR, "Please try again").show();
                } else {
                    new Alert(Alert.AlertType.CONFIRMATION, "User information updated successfully").show();
                }
                loadAllUsers();
            } catch (SQLIntegrityConstraintViolationException e) {
                new Alert(Alert.AlertType.ERROR, "Username already exists, Please select a different username").show();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        btnResetOnAction(null);
    }

    private void loadAllUsers() {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            PreparedStatement stm = connection.prepareStatement("SELECT * FROM user");
            ResultSet rst = stm.executeQuery();
            ObservableList<UserTM> users = tblUsers.getItems();
            users.clear();
            while (rst.next()){
                users.add(new UserTM(
                        rst.getString("username"),
                        rst.getString("name"),
                        rst.getString("password"),
                        rst.getString("role")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    private boolean isValidated() {
        if (txtUsername.getText().trim().isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Please enter a valid username").show();
            txtUsername.requestFocus();
            return false;
        } else if(txtName.getText().trim().isEmpty()){
            new Alert(Alert.AlertType.ERROR, "Please enter a valid name").show();
            txtName.requestFocus();
            return false;
        } else if(pwdPassword.getText().trim().isEmpty()){
            new Alert(Alert.AlertType.ERROR,"Please enter a valid password").show();
            pwdPassword.requestFocus();
            return false;
        } else if(!(rdoAdmin.isSelected() || rdoUser.isSelected())){
            new Alert(Alert.AlertType.ERROR, "Please select a role").show();
            rdoUser.requestFocus();
            return false;
        }
        return true;
    }
}
