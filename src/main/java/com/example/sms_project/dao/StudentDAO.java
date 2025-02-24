package com.example.sms_project.dao;


import org.json.JSONArray;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StudentDAO {
    public static int retrieveStudentId(ResultSet rs) throws SQLException {
        if (rs.next()) {
            return rs.getInt(1);
        }
        else{
            return -1;
        }
    }
    public static ResultSet insertStudent(Connection conn,String studentName, double cgpa) throws SQLException {
        String studentSQL = "INSERT INTO students (studentName, cgpa) VALUES (?, ?) RETURNING studentID";
        PreparedStatement studentStmt=conn.prepareStatement(studentSQL);
        studentStmt.setString(1, studentName);
        studentStmt.setDouble(2, cgpa);
        return studentStmt.executeQuery();
    }
    public static void insertMapping(Connection conn,int studentID, JSONArray courseIDs) throws SQLException {
        String insertMappingSQL = "INSERT INTO student_courses (studentID, courseID) VALUES (?, ?)";
        PreparedStatement insertMappingStmt = conn.prepareStatement(insertMappingSQL);
        for (int i = 0; i < courseIDs.length(); i++) {
            insertMappingStmt.setInt(1, studentID);
            insertMappingStmt.setInt(2, courseIDs.getInt(i));
            insertMappingStmt.addBatch();
        }
        insertMappingStmt.executeBatch();
        conn.commit();
    }

    public static ResultSet getStudentDetails(Connection conn, int studentID) throws SQLException {
        String studentSQL = "SELECT studentName, cgpa FROM students WHERE studentID = ?";
        PreparedStatement studentStmt=conn.prepareStatement(studentSQL);
        studentStmt.setInt(1, studentID);
        return studentStmt.executeQuery();
    }

    public static ResultSet getStudentCourses(Connection conn, int studentID) throws SQLException {
        String studentSQL="SELECT c.courseID, c.courseName FROM student_courses sc " +
                "JOIN courses c ON sc.courseID = c.courseID WHERE sc.studentID = ?";
        PreparedStatement studentStmt=conn.prepareStatement(studentSQL);
        studentStmt.setInt(1, studentID);
        return studentStmt.executeQuery();
    }
}
