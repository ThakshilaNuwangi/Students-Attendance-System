package controller;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import security.SecurityContextHolder;
import util.DBConnection;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDateTime;
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
    private String studentId;
    private Student student;

    public void initialize() {
        btnIn.setDisable(true);
        btnOut.setDisable(true);
        lblID.setText("");
        lblName.setText("");
        lblStatus.setText("");
        lblInfo.setVisible(false);
        updateLatestAttendance();

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), (event -> {
            lblDate.setText(String.format("%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS %1$tp", new Date()));
        })));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

        Connection connection = DBConnection.getInstance().getConnection();

        try {
            preparedStatement = connection.prepareStatement("SELECT * FROM student WHERE id=?");
        } catch (Exception e) {
            new Alert(Alert.AlertType.WARNING, "Failed to connect with Database").show();
            e.printStackTrace();
            Platform.runLater(() -> {
                ((Stage) (btnOut.getScene().getWindow())).close();
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
            preparedStatement.setString(1, txtId.getText());
            ResultSet rst = preparedStatement.executeQuery();

            if (rst.next()) {
                btnIn.setDisable(false);
                btnOut.setDisable(false);
                student = new Student(rst.getString("id"), rst.getString("name"), rst.getString("guardian_contact"));
                lblInfo.setVisible(true);
                lblID.setText(txtId.getText());
                lblName.setText(rst.getString("name"));
                InputStream inputStream = rst.getBlob("picture").getBinaryStream();
                imgProfile.setImage(new Image(inputStream));
                studentId = rst.getString("id");
                txtId.selectAll();
            } else {
                new Alert(Alert.AlertType.ERROR, "Invalid Student ID, Try Again!").show();
                txtId.selectAll();
                txtId.requestFocus();
                return;
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Something went wrong, Try Again!").show();
            txtId.selectAll();
            txtId.requestFocus();
            e.printStackTrace();
        }
    }

    public void btnInOnAction(ActionEvent actionEvent) {
        recordAttendance(true);
    }

    public void btnOutOnAction(ActionEvent actionEvent) {
        recordAttendance(false);
    }

    private void recordAttendance(boolean in) {
        Connection connection = DBConnection.getInstance().getConnection();

        String lastStatus = null;

        try {
            PreparedStatement stm = connection.prepareStatement("SELECT status ,date FROM attendance WHERE student_id=? ORDER BY date DESC LIMIT 1");
            stm.setString(1, studentId);
            ResultSet rst = stm.executeQuery();
            if (rst.next()) {
                lastStatus = rst.getString("status");
            }

            if ((lastStatus != null && lastStatus.equals("iN") && in) || (lastStatus != null && lastStatus.equals("OUT") && !in)) {
                Stage stage = new Stage();
                FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("/view/DangerAlert.fxml"));
                AnchorPane root = fxmlLoader.load();
                DangerAlertController controller = fxmlLoader.getController();
                SimpleBooleanProperty record = new SimpleBooleanProperty();
                controller.initData(studentId, lblName.getText(), rst.getTimestamp("date").toLocalDateTime(), in, record);
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setResizable(false);
                stage.setTitle("Alert!");
                stage.sizeToScene();
                stage.centerOnScreen();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.initOwner(lblStatus.getScene().getWindow());
                stage.setOnCloseRequest(event -> {
                    event.consume();
                });
                stage.showAndWait();
                if (!record.getValue()) {
                    return;
                }
            }
            PreparedStatement stm2 = connection.prepareStatement("INSERT INTO attendance(date, status, student_id, username) VALUES (NOW(),?,?,?)");
            stm2.setString(1, in ? "iN" : "OUT");
            stm2.setString(2, studentId);
            stm2.setString(3, SecurityContextHolder.getPrincipal().getUsername());
            if (stm2.executeUpdate() != 1) {
                throw new RuntimeException("Failed to add the attendance record");
            }
            //sendSMS(in);
            txtId.clear();
            txtIdOnAction(null);
            updateLatestAttendance();
        } catch (Throwable e) {
            new Alert(Alert.AlertType.ERROR, "Failed to save the attendance, Please try again");
        }
    }

    private void updateLatestAttendance() {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            Statement stm = connection.createStatement();
            ResultSet rst = stm.executeQuery("SELECT s.id, s.name, a.status, a.date FROM student s INNER JOIN attendance a on s.id=a.student_id\n" +
                    "ORDER BY date DESC LIMIT 1");
            if (rst.next()) {
                lblID.setText("ID: "+rst.getString("id"));
                lblName.setText("Name: "+rst.getString("name"));
                lblStatus.setText("Date: "+rst.getString("date")+" - "+ rst.getString("status"));
            } else {
                /*F Adding the first attendance record*/
                lblID.setText("ID: -");
                lblName.setText("Name: -");
                lblDate.setText("Date: -");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void sendSMS(boolean in) {
        try {
            URL url = new URL("https://api.smshub.lk/api/v2/send/single");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setDoOutput(true);
            String payload = String.format("{\n" +
                            "  \"message\": \"%s\",\n" +
                            "  \"phoneNumber\": \"%s\"\n" +
                            "}",
                    student.name + " has " + (in ? "entered to" : "exited from") + " at : " + LocalDateTime.now(),
                    student.guardianContact);
            urlConnection.getOutputStream().write(payload.getBytes());
            urlConnection.getOutputStream().close();
            System.out.println(urlConnection.getResponseCode());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class Student{
        String id;
        String name;
        String guardianContact;

        public Student(String id, String name, String guardianContact){
            this.id=id;
            this.name=name;
            this.guardianContact=guardianContact;
        }
    }
}
