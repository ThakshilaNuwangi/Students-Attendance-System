package controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

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
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Backup Files", "*.dep8backup"));
        File file = fileChooser.showOpenDialog(btnBrowse.getScene().getWindow());
        txtPath.setText(file != null ? file.getAbsolutePath() : "");
        fileProperty.setValue(file);
    }

    public void btnOkOnAction(ActionEvent actionEvent) {
        if (rdoFirstBoot.isSelected()) {
            fileProperty.setValue(null);
        }
        ((Stage) btnOk.getScene().getWindow()).close();
    }

    public void initFileProperty(SimpleObjectProperty<File> fileProperty) {
        this.fileProperty = fileProperty;
    }
}
