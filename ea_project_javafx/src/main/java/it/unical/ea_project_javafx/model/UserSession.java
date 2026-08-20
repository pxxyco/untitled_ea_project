package it.unical.ea_project_javafx.model;

import it.unical.ea_project_javafx.dto.UserDTO;
import lombok.Getter;

@Getter
public class UserSession {

    private static final UserSession INSTANCE = new UserSession();

    private Long id;
    private String username;
    private String email;
    private String fullName;
    private String profilePhotoUrl;
    private String role;
    private boolean oauthProvider;
    private boolean loggedIn = false;

    private UserSession() {}

    public static UserSession getInstance() {
        return INSTANCE;
    }

    public void setSession(UserDTO userDTO) {
        if (userDTO != null) {
            this.id = userDTO.getId();
            this.username = userDTO.getUsername();
            this.email = userDTO.getEmail();
            this.fullName = userDTO.getFullName();
            this.profilePhotoUrl = userDTO.getProfilePhotoUrl();
            this.role = userDTO.getRole();
            this.oauthProvider = userDTO.isOauthProvider();
            this.loggedIn = true;
        }
    }

    public void clear() {
        this.id = null;
        this.username = null;
        this.email = null;
        this.fullName = null;
        this.profilePhotoUrl = null;
        this.role = null;
        this.oauthProvider = false;
        this.loggedIn = false;
    }

}