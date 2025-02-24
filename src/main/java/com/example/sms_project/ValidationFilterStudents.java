package com.example.sms_project;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.Filter;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONObject;


@WebFilter("/students")
public class ValidationFilterStudents implements Filter{
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        if("POST".equals(req.getMethod())) {
            JSONObject jsonObject;
            try{
                jsonObject = ValidationUtil.getJSON(req);
            }
            catch(IOException e) {
                res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                res.getWriter().write(e.getMessage());
                return;
            }
            if (jsonObject.isEmpty()) { // ✅ Check if the parsed JSON is empty
                res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                res.getWriter().write("{\"error\": \"Request body cannot be empty\"}");
                return;
            }

            if(!jsonObject.has("studentName")){
                res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                res.getWriter().write("{\"error\": \"Student name is missing\"}");
                return;
            }
            if(!jsonObject.has("cgpa")){
                res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                res.getWriter().write("{\"error\": \"CGPA is missing\"}");
                return;
            }
            if(!jsonObject.has("courseNames")){
                res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                res.getWriter().write("{\"error\": \"Course name(s) is/are missing\"}");
                return;
            }
            String studentName=jsonObject.getString("studentName").trim();
            jsonObject.put("studentName", studentName);
            boolean isValidStudentName=ValidationUtil.validateName(res, studentName);
            if(!isValidStudentName){
                return;
            }
            double cgpa;
            JSONArray courseNames=jsonObject.getJSONArray("courseNames");
            Object cgpaObj=jsonObject.get("cgpa");
            if(!(cgpaObj instanceof Number)){
                res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                res.getWriter().write("{\"error\": \"CGPA should be a number\"}");
                return;
            }
            cgpa = ((Number) cgpaObj).doubleValue();
            boolean isCgpaWithinRange=ValidationUtil.isWithinRange(cgpa);
            if(!isCgpaWithinRange){
                res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                res.getWriter().write("{\"error\": \"CGPA must be between 0 and 10\"}");
                return;
            }
            JSONArray courseNamesArr=ValidationUtil.validateCourseNames(res, courseNames);
            if(courseNamesArr==null){
                return;
            }
            jsonObject.put("courseNames", courseNamesArr);
            // ✅ Store JSON in request attribute
            request.setAttribute("jsonBody", jsonObject);
        }
        if("GET".equalsIgnoreCase(req.getMethod()) || "DELETE".equalsIgnoreCase(req.getMethod())) {
            String studentIDParam = req.getParameter("studentID"); // Always a String
            studentIDParam=studentIDParam.trim();
            if (studentIDParam == null || studentIDParam.isEmpty()) {
                res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                res.getWriter().write("{\"error\": \"Student ID is missing\"}");
                return;
            }

            try {
                int studentID = Integer.parseInt(studentIDParam); // ✅ Convert to Integer
                if (studentID < 1) { // Optional: Ensure positive ID
                    res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    res.getWriter().write("{\"error\": \"Student ID must be a positive integer\"}");
                    return;
                }
            } catch (NumberFormatException e) {
                res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                res.getWriter().write("{\"error\": \"Student ID must be an integer\"}");
                return;
            }
        }

        chain.doFilter(request, response);
    }
}
