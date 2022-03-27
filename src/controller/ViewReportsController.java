package controller;

import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import util.AttendanceTM;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ViewReportsController {
    public TextField txtSearch;
    public TableView<AttendanceTM> tblAttendance;
    public TableColumn colReportId;
    public TableColumn colDate;
    public TableColumn colStatus;
    public TableColumn colStudentId;
    public TableColumn colUsername;

    public void initialize() {
        tblAttendance.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("reportId"));
        tblAttendance.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("date"));
        tblAttendance.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("status"));
        tblAttendance.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("studentId"));
        tblAttendance.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("username"));

        loadTable();
    }

    private void loadTable(){
        Connection connection = DBConnection.getInstance().getConnection();
        try {
            PreparedStatement stm = connection.prepareStatement("SELECT * FROM attendance");
            ResultSet rst = stm.executeQuery();
            ObservableList<AttendanceTM> attendanceTMS = tblAttendance.getItems();
            attendanceTMS.clear();
            while (rst.next()) {
                attendanceTMS.add(new AttendanceTM(
                        rst.getString("id"),
                        rst.getDate("date").toLocalDate(),
                        rst.getString("status"),
                        rst.getString("student_id"),
                        rst.getString("username")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
