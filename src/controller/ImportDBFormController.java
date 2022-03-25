package controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class ImportDBFormController {
    public RadioButton rdoRestore;
    public ToggleGroup abc;
    public TextField txtPath;
    public Button btnBrowse;
    public RadioButton rdoFirstBoot;
    public Button btnOk;
    private SimpleObjectProperty<File> fileProperty;

    public void initialize () {
        rdoRestore.selectedProperty().addListener((observable, oldValue, newValue) -> {
            btnOk.setDisable(txtPath.getText().isEmpty() && newValue);
        });
    }

    public void btnBrowseOnAction(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select a Backup File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Backup Files", "*.sasbackup"));
        File file = fileChooser.showOpenDialog(btnBrowse.getScene().getWindow());
        txtPath.setText(file != null ? file.getAbsolutePath() : "");
        fileProperty.setValue(file);
    }

    public void btnOkOnAction(ActionEvent actionEvent) {
        if (rdoFirstBoot.isSelected()) {
            fileProperty.setValue(null);
        }
        if (fileProperty.getValue()!=null){
            ProcessBuilder mysqlProcessBuilder = new ProcessBuilder("mysql",
                    "-h", "localhost",
                    "--port", "3306",
                    "-u", "root",
                    "-pmysql");

            mysqlProcessBuilder.redirectInput(fileProperty.getValue());// < file
            try {
                Process mysqlRestore = mysqlProcessBuilder.start();
                int exitCode = mysqlRestore.waitFor();

                if (exitCode==0){
                    new Alert(Alert.AlertType.INFORMATION, "Restore process successfully completed!").show();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Restore process failed, please try again!").show();
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
        ((Stage) btnOk.getScene().getWindow()).close();
    }

    public void initFileProperty(SimpleObjectProperty<File> fileProperty) {
        this.fileProperty = fileProperty;
    }
}
