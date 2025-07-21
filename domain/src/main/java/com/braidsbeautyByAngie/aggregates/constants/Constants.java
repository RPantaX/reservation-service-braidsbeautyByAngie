package com.braidsbeautyByAngie.aggregates.constants;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class Constants {
    public static String getUserInSession() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String username = request.getHeader("X-Username");
        String userId = request.getHeader("X-User-Id");
        return username + " - " + userId;
    }
}
