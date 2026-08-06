package it.unical.ea_project_javafx.model;

public class UserSession {

    private static final UserSession INSTANCE = new UserSession();

    private String username;
    private String email;
    private String role;
    private boolean loggedIn = false;

    private UserSession() {}

    public static UserSession getInstance() {
        return INSTANCE;
    }

    public void setSession(String username, String email, String role) {
        this.username = username;
        this.email = email;
        this.role = role;
        this.loggedIn = true;
    }

    public void clear() {
        username = null;
        email = null;
        role = null;
        loggedIn = false;
    }

    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public boolean isLoggedIn() { return loggedIn; }
}