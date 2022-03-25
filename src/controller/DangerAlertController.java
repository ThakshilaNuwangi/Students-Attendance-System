package controller;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class DangerAlertController {
    public Label lblId;
    public Label lblName;
    public Label lblDate;
    public Button btnContinue;
    public Button btnCancel;
    private SimpleBooleanProperty record;

    public void initialize () {
        playSiren();
    }

    public void btnContinueOnAction(ActionEvent actionEvent) {
        record.setValue(true);
        ((Stage)(lblId.getScene().getWindow())).close();
    }

    public void btnCancelOnAction(ActionEvent actionEvent) {
        ((Stage)(lblId.getScene().getWindow())).close();
    }

    public void initData(String studentId, String name, LocalDateTime date, boolean in, SimpleBooleanProperty record) {
        lblId.setText(studentId);
        lblName.setText(name);
        lblDate.setText(date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a")) + "-" + (in ? "IN" : "OUT"));
        this.record = record;
    }

    private void playSiren () {
        try {
            Media media = new Media(this.getClass().getResource("/assets/alert.wav").toURI().toString());
            MediaPlayer mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setCycleCount(1);
            mediaPlayer.play();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
