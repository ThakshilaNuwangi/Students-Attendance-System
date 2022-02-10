package controller;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Duration;
import util.DBConnection;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class RecordAttendanceFormController {
    public TextField txtId;
    public ImageView imgProfile;
    public Button btnIn;
    public Button btnOut;
    public Label lblDate;
    public Label lblID;
    public Label lblName;
    public Label lblStatus;
    public Label lblInfo;
    private PreparedStatement preparedStatement;

    public void initialize(){
        btnIn.setDisable(true);
        btnOut.setDisable(true);
        lblID.setText("");
        lblName.setText("");
        lblStatus.setText("");
        lblInfo.setVisible(false);

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1),(event -> {
            lblDate.setText(String.format("%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS %1$tp" , new Date()));
        })));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

        Connection connection = DBConnection.getInstance().getConnection();

        try {
            preparedStatement = connection.prepareStatement("SELECT * FROM student WHERE id=?");
        } catch (Exception e) {
            new Alert(Alert.AlertType.WARNING,"Failed to connect with Database").show();
            e.printStackTrace();
            Platform.runLater(()->{
                ((Stage)(btnOut.getScene().getWindow())).close();
            });
        }
    }

    public void txtIdOnAction(ActionEvent actionEvent) {
        btnIn.setDisable(true);
        btnOut.setDisable(true);
        lblID.setText("");
        lblName.setText("");
        lblStatus.setText("");
        lblInfo.setVisible(false);
        imgProfile.setImage(new Image("view/assets/student.jpg"));

        if (txtId.getText().trim().isEmpty()) {
            return;
        }

        try {
            preparedStatement.setString(1,txtId.getText());
            ResultSet rst = preparedStatement.executeQuery();

            if (rst.next()) {
                btnIn.setDisable(false);
                btnOut.setDisable(false);
                lblInfo.setVisible(true);
                lblID.setText(txtId.getText());
                lblName.setText(rst.getString("name"));
                InputStream inputStream = rst.getBlob("picture").getBinaryStream();
                imgProfile.setImage(new Image(inputStream));
                txtId.selectAll();
            } else {
                new Alert(Alert.AlertType.ERROR,"Invalid Student ID, Try Again!").show();
                txtId.selectAll();
                txtId.requestFocus();
                return;
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR,"Something went wrong, Try Again!").show();
            txtId.selectAll();
            txtId.requestFocus();
            e.printStackTrace();
        }
    }

    public void btnInOnAction(ActionEvent actionEvent) {
    }

    public void btnOutOnAction(ActionEvent actionEvent) {
    }
}
