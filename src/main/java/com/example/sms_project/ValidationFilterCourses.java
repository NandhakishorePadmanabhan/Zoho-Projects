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

import org.json.JSONObject;

@WebFilter("/courses")
public class ValidationFilterCourses implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        if("POST".equals(req.getMethod())) {
            JSONObject jsonObject;
            try{
                jsonObject = ValidationUtil.getJSON(req);
            }
            catch(IOException e){
                res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                res.getWriter().write(e.getMessage());
                return;
            }
            if (req.getContentLength() == 0) {
                res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                res.getWriter().write("{\"error\": \"Request body cannot be empty\"}");
                return;
            }
            if(!jsonObject.has("courseName")) {
                res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                res.getWriter().write("{\"error\": \"Course name is missing\"}");
                return;
            }
            if(!jsonObject.has("credits")) {
                res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                res.getWriter().write("{\"error\": \"Course credits is missing\"}");
                return;
            }
            String courseName = jsonObject.getString("courseName").trim();
            boolean isValidCourseName=ValidationUtil.validateName(res, courseName);
            if(!isValidCourseName){
                return;
            }
            jsonObject.put("courseName", courseName);
            int credits;
            Object creditsObj = jsonObject.get("credits");
            if (!(creditsObj instanceof Integer)) {
                res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                res.getWriter().write("{\"error\": \"Course credits should be a whole number (integer)\"}");
                return;
            }
            credits = (Integer) creditsObj; // Safe type casting
            boolean isCreditsWithinRange=ValidationUtil.isWithinRange(credits);
            if(!isCreditsWithinRange){
                res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                res.getWriter().write("{\"error\": \"Course credits should be between 0 and 10\"}");
                return;
            }
            // ✅ Store JSON in request attribute
            request.setAttribute("jsonBody", jsonObject);
        }
        if("GET".equalsIgnoreCase(req.getMethod()) || "DELETE".equalsIgnoreCase(req.getMethod())) {
            String courseName=req.getParameter("courseName");
            courseName=courseName.trim();
            if("DELETE".equalsIgnoreCase(req.getMethod()) && courseName==null) {
                res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                res.getWriter().write("{\"error\": \"Course name cannot be null\"}");
                return;
            }
            boolean isValidCourseName=ValidationUtil.validateName(res, courseName);
            if(!isValidCourseName){
                return;
            }
        }
        chain.doFilter(request, response);
    }
}