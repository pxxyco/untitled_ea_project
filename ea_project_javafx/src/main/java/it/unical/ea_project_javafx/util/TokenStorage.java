package it.unical.ea_project_javafx.util;

import java.util.prefs.Preferences;

public class TokenStorage {
    private static final Preferences PREFS = Preferences.userNodeForPackage(TokenStorage.class);
    private static final String ACCESS_TOKEN_KEY = "jwt_access_token";
    private static final String REFRESH_TOKEN_KEY = "jwt_refresh_token";

    public static void saveTokens(String accessToken, String refreshToken) {
        PREFS.put(ACCESS_TOKEN_KEY, accessToken != null ? accessToken : "");
        if (refreshToken != null) {
            PREFS.put(REFRESH_TOKEN_KEY, refreshToken);
        }
    }

    public static String getAccessToken() {
        return PREFS.get(ACCESS_TOKEN_KEY, null);
    }

    public static String getRefreshToken() {
        return PREFS.get(REFRESH_TOKEN_KEY, null);
    }

    public static void clear() {
        PREFS.remove(ACCESS_TOKEN_KEY);
        PREFS.remove(REFRESH_TOKEN_KEY);
    }
}