package com.example.sms_project;

import com.example.sms_project.dao.CourseDAO;
import com.example.sms_project.model.Course;
import com.example.sms_project.utils.DBConnection;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;

@WebServlet(name = "CourseServlet", value = "/courses")
public class CourseServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Read JSON from the attribute (set by filter)
        JSONObject jsonObject = (JSONObject) request.getAttribute("jsonBody");

        // get courseName
        String courseName = jsonObject.getString("courseName");

        // get credits
        int credits= jsonObject.getInt("credits");

        // Insert course into database
        try {
            if (CourseDAO.addCourse(new Course(courseName, credits))) {
                response.getWriter().write("Course added successfully");
            } else {
                sendErrorResponse(response, "Failed to add course.");
            }
        } catch (SQLException e) {
            sendErrorResponse(response, e.getMessage());
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String coursename;
        if(request.getParameter("courseName")==null){
            try(Connection conn=DBConnection.getConnection()){
                String courseSQL="SELECT * FROM COURSES";
                PreparedStatement courseStmt=conn.prepareStatement(courseSQL);
                ResultSet rs=courseStmt.executeQuery();
                JSONObject course;
                while(rs.next()){
                    course=new JSONObject();
                    int CID = rs.getInt("CourseID");
                    String courseName = rs.getString("courseName");
                    int credits = rs.getInt("credits");
                    course.put("courseID",CID);
                    course.put("courseName",courseName);
                    course.put("credits",credits);
                    response.getWriter().write(course.toString());
                    response.getWriter().write("\n");
                }
            }
            catch(SQLException e){
                sendErrorResponse(response, e.getMessage());
            }
            return;
        }
        // get course name
        coursename=request.getParameter("courseName").trim();

        try {
            Course course = CourseDAO.getCourseByName(coursename);
            if (course == null) {
                sendErrorResponse(response, "Course not found.");
                return;
            }

            JSONObject responseJson = new JSONObject();
            responseJson.put("courseID", course.getCourseID());
            responseJson.put("courseName", course.getCourseName());
            responseJson.put("credits", course.getCredits());

            response.setContentType("application/json");
            response.getWriter().write(responseJson.toString());
        } catch (SQLException e) {
            sendErrorResponse(response, e.getMessage());
        }
    }

    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String coursename;
        coursename=request.getParameter("courseName").trim();

        try(Connection conn= DBConnection.getConnection()){
            String courseSQL="DELETE FROM courses WHERE courseName=?";
            PreparedStatement courseStmt=conn.prepareStatement(courseSQL);
            courseStmt.setString(1, coursename);
            int affectedRows=courseStmt.executeUpdate();
            if (affectedRows == 0) {
                sendErrorResponse(response, "Course not found.");
                return;
            }
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("Course deleted successfully");
        }
        catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(e.getMessage());
        }
    }


    private void sendErrorResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.getWriter().write("{\"error\": \"" + message + "\"}");
    }
}
