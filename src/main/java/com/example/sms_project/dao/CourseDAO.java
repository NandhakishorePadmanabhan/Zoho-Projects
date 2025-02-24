package com.example.sms_project.dao;

import com.example.sms_project.model.Course;
import com.example.sms_project.utils.DBConnection;

import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.ResultSet;

public class CourseDAO {
    public static boolean addCourse(Course course) throws SQLException {
        String checkCourseSQL="SELECT COUNT(*) FROM courses WHERE courseName=?";
        String query = "INSERT INTO courses (courseName, credits) VALUES (?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             PreparedStatement checkCourseStmt=conn.prepareStatement(checkCourseSQL);) {
            checkCourseStmt.setString(1, course.getCourseName());
            ResultSet rs = checkCourseStmt.executeQuery();
            int count = 0;
            if(rs.next()) {
                count = rs.getInt(1);
            }
            if(count>0) {
                throw new SQLException("Course already exists");
            }
            stmt.setString(1, course.getCourseName());
            stmt.setInt(2, course.getCredits());
            return stmt.executeUpdate() > 0;
        }
    }

    public static Course getCourseByName(String courseName) throws SQLException {
        String query = "SELECT courseID, credits FROM courses WHERE courseName = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, courseName);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Course(rs.getInt("courseID"), courseName, rs.getInt("credits"));
            }
            return null; // Course not found
        }
    }
}
