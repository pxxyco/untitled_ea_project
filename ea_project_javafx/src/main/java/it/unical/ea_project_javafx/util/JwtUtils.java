package it.unical.ea_project_javafx.util;

import java.util.Base64;

public class JwtUtils {

    // Estrae  email dal JWT
    public static String extractEmail(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length > 1) {
                String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]));
                if (payloadJson.contains("\"sub\"")) {
                    int start = payloadJson.indexOf("\"sub\":\"") + 7;
                    int end = payloadJson.indexOf("\"", start);
                    return payloadJson.substring(start, end);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isExpired(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length > 1) {
                String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]));
                if (payloadJson.contains("\"exp\"")) {
                    int start = payloadJson.indexOf("\"exp\":") + 6;
                    int end = payloadJson.indexOf(",", start);
                    if (end == -1) end = payloadJson.indexOf("}", start);
                    long expTime = Long.parseLong(payloadJson.substring(start, end).trim());
                    return System.currentTimeMillis() >= (expTime * 1000);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
}