package com.app.flashcard.shared.utils;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Utility class for IP address handling
 */
public class IpUtils {
    
    private static final String[] IP_HEADER_CANDIDATES = {
        "X-Forwarded-For",
        "Proxy-Client-IP",
        "WL-Proxy-Client-IP",
        "HTTP_X_FORWARDED_FOR",
        "HTTP_X_FORWARDED",
        "HTTP_X_CLUSTER_CLIENT_IP",
        "HTTP_CLIENT_IP",
        "HTTP_FORWARDED_FOR",
        "HTTP_FORWARDED",
        "HTTP_VIA",
        "REMOTE_ADDR"
    };
    
    /**
     * Get client IP address from HTTP request
     * Handles cases where the app is behind a proxy or load balancer
     */
    public static String getClientIpAddress(HttpServletRequest request) {
        for (String header : IP_HEADER_CANDIDATES) {
            String ipList = request.getHeader(header);
            if (ipList != null && !ipList.isEmpty() && !"unknown".equalsIgnoreCase(ipList)) {
                // Get first IP if multiple IPs are present
                return ipList.split(",")[0].trim();
            }
        }
        
        return request.getRemoteAddr();
    }
}