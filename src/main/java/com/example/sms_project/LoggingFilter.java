package com.example.sms_project;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import java.io.IOException;
import java.util.logging.Logger;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.ServletException;

@WebFilter("/*")
public class LoggingFilter implements Filter {
    private static final Logger LOGGER = Logger.getLogger(LoggingFilter.class.getName());

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        LOGGER.info("Incoming Request: " + req.getMethod() + " " + req.getRequestURI());
        chain.doFilter(request, response);
    }
}