package util;

import java.time.LocalDate;
import java.util.Date;

public class AttendanceTM {
    String reportId;
    LocalDate date;
    String status;
    String studentId;
    String username;

    public AttendanceTM() {
    }

    public AttendanceTM(String reportId, LocalDate date, String status, String studentId, String username) {
        this.reportId = reportId;
        this.date = date;
        this.status = status;
        this.studentId = studentId;
        this.username = username;
    }

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
