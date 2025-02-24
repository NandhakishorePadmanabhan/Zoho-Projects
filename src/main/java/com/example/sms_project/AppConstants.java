package com.example.sms_project;

public class AppConstants {

    // Regular Expressions
    public static final String REGEX = "^[a-zA-Z\\s]+$";

    // Error Messages
    public static final String ERROR_INVALID_NAME = "{\"error\": \"Invalid studentName. It should contain only alphabets.\"}";
    public static final String ERROR_INVALID_CGPA = "{\"error\": \"Invalid cgpa.\"}";
    public static final String ERROR_MISSING_STUDENT_ID = "{\"error\": \"Missing studentID parameter\"}";
    public static final String ERROR_INVALID_STUDENT_ID = "{\"error\": \"Invalid studentID. It should be a positive integer.\"}";
    public static final String ERROR_STUDENT_NOT_FOUND = "{\"error\": \"Student ID not found\"}";
    public static final String ERROR_COURSE_NOT_FOUND = "{\"error\": \"Some course names do not exist.\"}";
    public static final String ERROR_DB_CONNECTION = "Error: ";
    public static final String INVALID_CREDITS = "\"Invalid value for credits.\"";
    public static final String INVALID_JSON_INPUT = "Invalid JSON input.";
    public static final String INVALID_COURSE_NAME="Invalid courseName. It should contain only alphabets.";


    // SQL Queries
    public static final String INSERT_STUDENT_SQL = "INSERT INTO students (studentName, cgpa) VALUES (?, ?) RETURNING studentID";
    public static final String GET_COURSE_QUERY = "SELECT c.courseID, c.courseName FROM student_courses sc " +
            "JOIN courses c ON sc.courseID = c.courseID WHERE sc.studentID = ?";
    public static final String GET_STUDENT_QUERY = "SELECT studentName, cgpa FROM students WHERE studentID = ?";
    public static final String INSERT_MAPPING_SQL = "INSERT INTO student_courses (studentID, courseID) VALUES (?, ?)";

    // Response Content Types
    public static final String RESPONSE_JSON = "application/json";
    public static final String CHARSET_UTF8 = "UTF-8";
}
