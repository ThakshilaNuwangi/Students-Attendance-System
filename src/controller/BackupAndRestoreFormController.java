package controller;

import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;

public class BackupAndRestoreFormController {
    public Button btnBackup;
    public Button btnRestore;

    public void btnBackupOnAction(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose backup location");
        fileChooser.setInitialFileName(LocalDate.now()+"-sas-backup");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Backup files(*.sasbackup)","*.sasbackup"));
        File file = fileChooser.showSaveDialog(btnBackup.getScene().getWindow());

        if (file!=null){
            ProcessBuilder mysqlDumpProcessBuilder = new ProcessBuilder("mysqldump",
                    "-h", "localhost",
                    "--port", "3306",
                    "-u", "root",
                    "-pmysql",
                    "-d",
                    "--add-drop-databse",
                    "--database", "student_attendance_system");

            mysqlDumpProcessBuilder.redirectOutput(System.getProperty("os.nname").equalsIgnoreCase("windows") ? file : new File(file.getAbsolutePath() + ".sasbackup"));
            try {
                Process mysqlDump = mysqlDumpProcessBuilder.start();
                int exitCode = mysqlDump.waitFor();
                if (exitCode==0){
                    new Alert(Alert.AlertType.INFORMATION, "Backup process successfully completed!").show();
                } else{
                    new Alert(Alert.AlertType.ERROR, "Backup process failed, please try again!").show();
                }

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void btnRestoreOnAction(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select a file to backup");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Backup Files(*.sasbackup)","*.sasbackup"));
        File file = fileChooser.showOpenDialog(btnRestore.getScene().getWindow());

        if (file!=null){
            ProcessBuilder mysqlProcessBuilder = new ProcessBuilder("mysql",
                    "-h", "localhost",
                    "--port", "3306",
                    "-u", "root",
                    "-pmysql");

            mysqlProcessBuilder.redirectInput(file);      // < file
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
    }
}
