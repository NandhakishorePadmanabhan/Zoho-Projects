package com.example.sms_project;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.example.sms_project.dao.StudentDAO;
import com.example.sms_project.utils.DBConnection;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.annotation.WebServlet;

import org.json.JSONArray;
import org.json.JSONObject;

@WebServlet(name = "StudentServlet", value = "/students")
public class StudentServlet extends HttpServlet {

    public void init() {}

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Read JSON data from request body
        JSONObject jsonData = (JSONObject) request.getAttribute("jsonBody");
        String studentName = jsonData.getString("studentName");
        double cgpa = jsonData.getDouble("cgpa");
        JSONArray courseNames = jsonData.getJSONArray("courseNames");
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            // Insert student
            ResultSet rs=StudentDAO.insertStudent(conn, studentName, cgpa);

            //Retrieve student id
            int studentID = StudentDAO.retrieveStudentId(rs);
            if(studentID == -1){
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("Student not found");
                return;
            }
            // Retrieve courseIDs based on course names
            JSONArray courseIDs = CheckCourses.getCourseIDsByNames(conn, courseNames);
            if (courseIDs.length() != courseNames.length()) {
                conn.rollback();
                response.getWriter().write(AppConstants.ERROR_COURSE_NOT_FOUND);
                return;
            }

            // Insert student-course mappings
            StudentDAO.insertMapping(conn, studentID, courseIDs);
            response.getWriter().write("Student created with ID " + studentID + " and mapped to courses successfully!");
        } catch (SQLException e) {
            response.getWriter().write(AppConstants.ERROR_DB_CONNECTION + e.getMessage());
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType(AppConstants.RESPONSE_JSON);
        response.setCharacterEncoding(AppConstants.CHARSET_UTF8);

        // get studentID
        String studentIDParam = request.getParameter("studentID").trim();
        int studentID=Integer.parseInt(studentIDParam);

        try (Connection conn = DBConnection.getConnection()) {
            ResultSet studentRs = StudentDAO.getStudentDetails(conn, studentID);

            if (!studentRs.next()) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write(AppConstants.ERROR_STUDENT_NOT_FOUND);
                return;
            }

            String studentName = studentRs.getString("studentName");
            double cgpa = studentRs.getDouble("cgpa");

            // Get student courses
            ResultSet coursesRs = StudentDAO.getStudentCourses(conn,studentID);

            JSONArray coursesArray = new JSONArray();
            while (coursesRs.next()) {
                JSONObject course = new JSONObject();
                course.put("courseID", coursesRs.getInt("courseID"));
                course.put("courseName", coursesRs.getString("courseName"));
                coursesArray.put(course);
            }

            JSONObject responseJson = new JSONObject();
            responseJson.put("studentID", studentID);
            responseJson.put("studentName", studentName);
            responseJson.put("cgpa", cgpa);
            responseJson.put("courses", coursesArray);

            response.getWriter().write(responseJson.toString());
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(AppConstants.ERROR_DB_CONNECTION + e.getMessage());
        }
    }

    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //Extract studentID parameter
        String studentIDParam = request.getParameter("studentID").trim();
        int studentID=Integer.parseInt(studentIDParam);
        try(Connection conn=DBConnection.getConnection()){
            String studentSQL="DELETE FROM students WHERE studentID = ?";
            PreparedStatement studentStmt=conn.prepareStatement(studentSQL);
            studentStmt.setInt(1, studentID);
            int rowsAffected=studentStmt.executeUpdate();
            if(rowsAffected==0){
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write(AppConstants.ERROR_STUDENT_NOT_FOUND);
                return;
            }
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("Student successfully deleted");
        }
        catch(SQLException e){
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(AppConstants.ERROR_DB_CONNECTION + e.getMessage());
        }
    }

    public void destroy() {}
}
