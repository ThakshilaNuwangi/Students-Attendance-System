DROP DATABASE IF EXISTS student_attendance_system;

CREATE DATABASE student_attendance_system;

USE student_attendance_system;

CREATE TABLE student
(
  id VARCHAR(30) PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  picture LONGBLOB NOT NULL
);

CREATE TABLE user
(
    username VARCHAR(30) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    password VARCHAR(50) NOT NULL,
    role ENUM('ADMIN','USER') NOT NULL
);

CREATE TABLE attendance
(
  id INT AUTO_INCREMENT PRIMARY KEY,
  date DATETIME NOT NULL,
  status ENUM ('IN','OUT') NOT NULL,
  student_id VARCHAR(30) NOT NULL,
  username VARCHAR(30) NOT NULL,
  CONSTRAINT fk_student_id FOREIGN KEY (student_id) REFERENCES student(id),
  CONSTRAINT fk_username FOREIGN KEY (username) REFERENCES user(username)
);