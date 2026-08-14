package it.unical.ea_project_javafx.model;

public class UserSession {

    private static final UserSession INSTANCE = new UserSession();

    private Long id; // AGGIUNTO L'ID
    private String username;
    private String email;
    private String role;
    private boolean loggedIn = false;

    private UserSession() {}

    public static UserSession getInstance() {
        return INSTANCE;
    }

    // Aggiornato per accettare anche l'id
    public void setSession(Long id, String username, String email, String role) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
        this.loggedIn = true;
    }

    public void clear() {
        id = null;
        username = null;
        email = null;
        role = null;
        loggedIn = false;
    }

    public Long getId() { return id; } // METODO PER PRENDERE L'ID
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public boolean isLoggedIn() { return loggedIn; }
}