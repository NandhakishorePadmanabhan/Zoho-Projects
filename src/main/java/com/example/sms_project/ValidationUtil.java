package com.example.sms_project;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.regex.Pattern;

public class ValidationUtil {
    private static final Pattern PATTERN = Pattern.compile("^[a-zA-Z\\s]+$");
    public static JSONObject getJSON(HttpServletRequest request) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = request.getReader();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        String requestBody = sb.toString().trim();

        // Check if the body is completely missing
        if (requestBody.isEmpty()) {
            throw new IOException("Request body is empty");
        }
        return new JSONObject(requestBody);
    }
    public static boolean validateName(HttpServletResponse res,String Name) throws IOException {
        if(Name.isEmpty()){
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            res.getWriter().write("Names cannot be empty");
            return false;
        }
        if(!PATTERN.matcher(Name).matches()){
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            res.getWriter().write("Names should contain only alphabets");
            return false;
        }
        return true;
    }
    public static JSONArray validateCourseNames(HttpServletResponse res,JSONArray CourseNames) throws IOException {
        if(CourseNames.length()==0){
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            res.getWriter().write("Each student should add atleast one course");
            return null;
        }
        JSONArray courseNamesArr=new JSONArray();
        for(int i=0;i<CourseNames.length();i++){
            Object courseName=CourseNames.get(i);
            if(!(courseName instanceof String)){
                res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                res.getWriter().write("Course names should be of type String");
                return null;
            }
            boolean isValidCourseName=validateName(res,(String)courseName);
            if(!isValidCourseName){
                return null;
            }
            courseNamesArr.put(((String) courseName).trim());
        }
        return courseNamesArr;
    }
    public static boolean isWithinRange(double value){
            return value >=1 && value <=10;
    }
}
