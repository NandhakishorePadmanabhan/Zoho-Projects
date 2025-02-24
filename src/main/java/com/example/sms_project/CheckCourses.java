package com.example.sms_project;

import org.json.JSONArray;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
public class CheckCourses {
    public static JSONArray getCourseIDsByNames(Connection conn,JSONArray courseNames) throws SQLException {
        JSONArray courseIDs = new JSONArray();
        String courseIDsSQL="SELECT courseID FROM courses WHERE courseName = ANY (?)";
        PreparedStatement courseIDsStmt = conn.prepareStatement(courseIDsSQL);
        String[] courseNamesArray=new String[courseNames.length()];
        for (int i = 0; i < courseNames.length(); i++) {
            courseNamesArray[i] = courseNames.getString(i);
        }
        courseIDsStmt.setArray(1, conn.createArrayOf("VARCHAR", courseNamesArray));
        ResultSet validCourses = courseIDsStmt.executeQuery();
        while (validCourses.next()) {
            courseIDs.put(validCourses.getInt("courseID"));
        }
        return courseIDs;
    }
}
